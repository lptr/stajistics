/* Copyright 2009 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stajistics.session;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.task.TaskService;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.AtomicDouble;

/**
 *
 * @author The Stajistics Project
 */
public class AsynchronousStatsSession extends AbstractStatsSession
{
    private static final long serialVersionUID = 1367248462643181008L;

    private static final Logger logger = LoggerFactory.getLogger(AsynchronousStatsSession.class);

    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong firstHitStamp = new AtomicLong(-1);
    private volatile long lastHitStamp = -1;
    private final AtomicLong commits = new AtomicLong(0);

    private final AtomicReference<Double> first = new AtomicReference<Double>(null);
    private volatile double last = Double.NaN;
    private final AtomicDouble min = new AtomicDouble(Double.POSITIVE_INFINITY);
    private final AtomicDouble max = new AtomicDouble(Double.NEGATIVE_INFINITY);
    private final AtomicDouble sum = new AtomicDouble(0);

    private final TaskService taskService;

    private final Lock callQueueLock = new ReentrantLock();
    private final Queue<Callable<Void>> callQueue = new ConcurrentLinkedQueue<Callable<Void>>();

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService) {
        super(key, eventManager);

        if (taskService == null) {
            throw new NullPointerException("taskService");
        }

        this.taskService = taskService;
    }

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService,
                                    final DataRecorder... dataRecorders) {
        super(key, eventManager, dataRecorders);

        if (taskService == null) {
            throw new NullPointerException("taskService");
        }

        this.taskService = taskService;
    }

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService,
                                    final List<DataRecorder> dataRecorders) {
        super(key, eventManager, dataRecorders);

        if (taskService == null) {
            throw new NullPointerException("taskService");
        }

        this.taskService = taskService;
    }

    private void scheduleCall(final Callable<Void> call) {
        callQueue.add(call);

        ProcessQueueTask processQueueTask = new ProcessQueueTask();
        taskService.submit(getClass(), processQueueTask);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void track(final StatsTracker tracker,
                      final long now)
    {
        TrackCall call = new TrackCall(tracker, now);
        scheduleCall(call);
    }

    /**
     * Called by {@link org.stajistics.session.AsynchronousStatsSession.TrackCall}.
     *
     * @param tracker
     * @param now
     */
    private synchronized void trackImpl(final StatsTracker tracker,
                                        long now)
    {
        if (now < 0) {
            now = System.currentTimeMillis();
        }

        hits.incrementAndGet();

        if (firstHitStamp.get() == -1) {
            firstHitStamp.compareAndSet(-1, now);
        }
        lastHitStamp = now;

        logger.trace("Track: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_TRACKING, key, tracker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getHits() {
        return hits.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFirstHitStamp() {
        return firstHitStamp.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastHitStamp() {
        return lastHitStamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCommits() {
        return commits.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final StatsTracker tracker,
                       final long now)
    {
        UpdateCall call = new UpdateCall(tracker, now);
        scheduleCall(call);
    }

    private synchronized void updateImpl(final StatsTracker tracker,
                                         final long now)
    {
        final double currentValue = tracker.getValue();

        commits.incrementAndGet();

        // First
        if (first.get() == null) {
            first.compareAndSet(null, currentValue);
        }

        // Last
        last = currentValue;

        // Min
        if (currentValue < min.get()) {
            min.set(currentValue);
        }

        // Max
        if (currentValue > max.get()) {
            max.set(currentValue);
        }

        // Sum
        sum.addAndGet(currentValue);

        for (DataRecorder dataRecorder : dataRecorders) {
            dataRecorder.update(this, tracker, now);
        }

        logger.info("Commit: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_COMMITTED, key, tracker);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFirst() {
        Double firstValue = first.get();

        if (firstValue == null) {
            return Double.NaN;
        }

        return firstValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLast() {
        return last;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMin() {
        return min.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMax() {
        return max.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSum() {
        return sum.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void restore(final DataSet dataSet)
    {
        hits.set(dataSet.getField(DataSet.Field.COMMITS, Long.class));
        firstHitStamp.set(dataSet.getField(DataSet.Field.FIRST_HIT_STAMP, Date.class).getTime());
        lastHitStamp = dataSet.getField(DataSet.Field.LAST_HIT_STAMP, Date.class).getTime();
        commits.set(dataSet.getField(DataSet.Field.COMMITS, Long.class));
        first.set(dataSet.getField(DataSet.Field.FIRST, Double.class));
        last = dataSet.getField(DataSet.Field.LAST, Double.class);
        min.set(dataSet.getField(DataSet.Field.MIN, Double.class));
        max.set(dataSet.getField(DataSet.Field.MAX, Double.class));
        sum.set(dataSet.getField(DataSet.Field.SUM, Double.class));

        for (DataRecorder dataRecorder : dataRecorders) {
            dataRecorder.restore(dataSet);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void clear()
    {
        hits.set(0);
        firstHitStamp.set(-1);
        lastHitStamp = -1;
        commits.set(0);
        first.set(null);
        last = Double.NaN;
        min.set(Double.POSITIVE_INFINITY);
        max.set(Double.NEGATIVE_INFINITY);
        sum.set(0);

        for (DataRecorder dataRecorder : dataRecorders) {
            dataRecorder.clear();
        }

        eventManager.fireEvent(StatsEventType.SESSION_CLEARED, key, this);
    }

    /* INNER CLASSES */

    private final class ProcessQueueTask implements Callable<Void> {
        @Override
        public Void call() {

            while (callQueue.peek() != null) {
                if (!callQueueLock.tryLock()) {
                    // Another task is processing the queue,
                    // so don't bother blocking and holding up an executor thread
                    break;
                }

                try {
                    for (;;) {
                        Callable<Void> callable = callQueue.poll();
                        if (callable == null) {
                            // No more calls to invoke
                            break;
                        }

                        try {
                            callable.call();
                        } catch (Exception e) {
                            logger.error("Failed to call " + callable, e);
                        }
                    }
                } finally {
                    callQueueLock.unlock();
                }
            }

            return null;
        }
    }

    private final class TrackCall implements Callable<Void> {

        private final StatsTracker tracker;
        private final long now;

        TrackCall(final StatsTracker tracker,
                  final long now) {
            this.tracker = tracker;
            this.now = now;
        }

        @Override
        public Void call() throws Exception {
            trackImpl(tracker, now);
            return null;
        }
    }

    private final class UpdateCall implements Callable<Void> {

        private final StatsTracker tracker;
        private final long now;

        UpdateCall(final StatsTracker tracker,
                   final long now) {
            this.tracker = tracker;
            this.now = now;
        }

        @Override
        public Void call() throws Exception {
            updateImpl(tracker, now);
            return null;
        }
    }

}

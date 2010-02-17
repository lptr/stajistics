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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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

/**
 * 
 * @author The Stajistics Project
 */
public class AsynchronousStatsSession extends AbstractStatsSession {

    private static final Logger logger = LoggerFactory.getLogger(AsynchronousStatsSession.class);

    private volatile long hits = 0;
    private volatile long firstHitStamp = -1;
    private volatile long lastHitStamp = -1;
    private volatile long commits = 0;

    private volatile Double first = null;
    private volatile double last = Double.NaN;
    private volatile double min = Double.POSITIVE_INFINITY;
    private volatile double max = Double.NEGATIVE_INFINITY;
    private volatile double sum = 0;
    
    private final Lock stateLock = new ReentrantLock();

    private final TaskService taskService;

    private final BlockingQueue<TrackerEntry> updateQueue = new LinkedBlockingQueue<TrackerEntry>();
    private final Lock updateQueueProcessingLock = new ReentrantLock();

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService) {
        this(key, eventManager, taskService, (List<DataRecorder>) null);
    }

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService,
                                    final DataRecorder... dataRecorders) {
        this(key, eventManager, taskService, Arrays.asList(dataRecorders));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void track(final StatsTracker tracker, long now) {
        if (now < 0) {
            now = System.currentTimeMillis();
        }

        stateLock.lock();
        try {
            hits++;

            if (firstHitStamp == -1) {
                firstHitStamp = now;
            }
            lastHitStamp = now;

            logger.trace("Track: {}", this);

            eventManager.fireEvent(StatsEventType.TRACKER_TRACKING, key, tracker);
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getHits() {
        return hits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFirstHitStamp() {
        return firstHitStamp;
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
        return commits;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final StatsTracker tracker, final long now) {
        updateQueue.add(new TrackerEntry(tracker, now));

        ProcessQueueTask processQueueTask = new ProcessQueueTask();
        taskService.execute(getClass(), processQueueTask);
    }

    private void updateImpl(final StatsTracker tracker, final long now) {
        final double currentValue = tracker.getValue();

        stateLock.lock();
        try {
            commits++;

            // First
            if (first == null) {
                first = currentValue;
            }

            // Last
            last = currentValue;

            // Min
            if (currentValue < min) {
                min = currentValue;
            }

            // Max
            if (currentValue > max) {
                max = currentValue;
            }

            // Sum
            sum += currentValue;

            for (DataRecorder dataRecorder : dataRecorders) {
                dataRecorder.update(this, tracker, now);
            }

            logger.trace("Commit: {}", this);

            eventManager.fireEvent(StatsEventType.TRACKER_COMMITTED, key, tracker);
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getFirst() {
        Double firstValue = first;

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
        return min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMax() {
        return max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSum() {
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restore(final DataSet dataSet) {
        stateLock.lock();
        try {
            hits = dataSet.getField(DataSet.Field.COMMITS, Long.class);
            firstHitStamp = dataSet.getField(DataSet.Field.FIRST_HIT_STAMP, Date.class).getTime();
            lastHitStamp = dataSet.getField(DataSet.Field.LAST_HIT_STAMP, Date.class).getTime();
            commits = dataSet.getField(DataSet.Field.COMMITS, Long.class);
            first = dataSet.getField(DataSet.Field.FIRST, Double.class);
            last = dataSet.getField(DataSet.Field.LAST, Double.class);
            min = dataSet.getField(DataSet.Field.MIN, Double.class);
            max = dataSet.getField(DataSet.Field.MAX, Double.class);
            sum = dataSet.getField(DataSet.Field.SUM, Double.class);

            for (DataRecorder dataRecorder : dataRecorders) {
                dataRecorder.restore(dataSet);
            }
        } finally {
            stateLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        // Must always lock update queue first to avoid deadlocks
        updateQueueProcessingLock.lock();
        stateLock.lock();
        try {
            updateQueue.clear();

            hits = 0;
            firstHitStamp = -1;
            lastHitStamp = -1;
            commits = 0;
            first = null;
            last = Double.NaN;
            min = Double.POSITIVE_INFINITY;
            max = Double.NEGATIVE_INFINITY;
            sum = 0;

            for (DataRecorder dataRecorder : dataRecorders) {
                dataRecorder.clear();
            }

            eventManager.fireEvent(StatsEventType.SESSION_CLEARED, key, this);
        } finally {
            stateLock.unlock();
            updateQueueProcessingLock.unlock();
        }
    }

    @Override
    public DataSet collectData() {
        stateLock.lock();
        try {
            return super.collectData();
        } finally {
            stateLock.unlock();
        }
    }

    /* INNER CLASSES */

    private final class ProcessQueueTask implements Runnable {

        @Override
        public void run() {
            if (!updateQueue.isEmpty()) {
                // Do not allow other threads to process entries
                updateQueueProcessingLock.lock();
                try {
                    // Re-query queue size to avoid allocating a buffer if some
                    // other thread has already processed the events
                    int count = updateQueue.size();
                    if (count > 0) {
                        TrackerEntry[] entries = updateQueue.toArray(new TrackerEntry[count]);
                        updateQueue.clear();
                        for (TrackerEntry entry : entries) {
                            updateImpl(entry.tracker, entry.now);
                        }
                    }
                } finally {
                    updateQueueProcessingLock.unlock();
                }
            }
        }
    }

    private static final class TrackerEntry {

        private final StatsTracker tracker;
        private final long now;

        TrackerEntry(final StatsTracker tracker, final long now) {
            this.tracker = tracker;
            this.now = now;
        }
    }

}

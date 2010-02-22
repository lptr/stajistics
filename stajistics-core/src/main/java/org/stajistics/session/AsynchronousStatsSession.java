/* Copyright 2009 - 2010 The Stajistics Project
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
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
 * An implementation of {@link StatsSession} that can potentially pad the
 * tracker, and thus the client of the tracker, from blocking on calls to
 * {@link #track(StatsTracker, long)}, and {@link #update(StatsTracker, long)}.
 * When either of these calls are made, they are queued for execution by the
 * associated {@link TaskService}, which will typically invoke them in a
 * background thread. This queuing behaviour does impose the overhead of queue
 * entry object creation on the client of a tracker, but this is necessary to
 * fulfil the main purpose of this implementation. {@link #collectData()} may be
 * called on an instance of this class and the resulting {@link DataSet} is
 * guaranteed to contain data fields that are related to one another for a given
 * update. For example, if {@link #collectData()} is called at the same time as
 * a update #2, the resulting {@link DataSet} will contain data related only to
 * update #1 or update #2. It will not contain the data from a partially
 * recorded update. Furthermore, it allows this data integrity without having to
 * block the client of the {@link StatsTracker}, which could sacrifice
 * performance. If better performance is preferred at the cost of potentially
 * inconsistent data, see {@link org.stajistics.session.ConcurrentStatsSession}.
 * 
 * @see org.stajistics.session.ConcurrentStatsSession
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

    private final TaskService taskService;

    private final Queue<TrackerEntry> updateQueue;
    private final Lock updateQueueProcessingLock = new ReentrantLock();

    private final Lock stateLock = new ReentrantLock();

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService,
                                    final DataRecorder... dataRecorders) {
        this(key, eventManager, taskService, new ConcurrentLinkedQueue<TrackerEntry>(), dataRecorders);
    }

    public AsynchronousStatsSession(final StatsKey key,
                                    final StatsEventManager eventManager,
                                    final TaskService taskService,
                                    final Queue<TrackerEntry> updateQueue,
                                    final DataRecorder... dataRecorders) {
        super(key, eventManager, dataRecorders);

        if (taskService == null) {
            throw new NullPointerException("taskService");
        }
        if (updateQueue == null) {
            throw new NullPointerException("updateQueue");
        }

        this.taskService = taskService;
        this.updateQueue = updateQueue;
    }

    private void queueUpdateTask(final StatsTracker tracker, final long now, final boolean update) {
        TrackerEntry entry = new TrackerEntry(tracker, now, update);
        try {
            updateQueue.add(entry);

            ProcessUpdateQueueTask processQueueTask = new ProcessUpdateQueueTask();
            taskService.execute(getClass(), processQueueTask);
        } catch (Exception e) {
            logger.error("Failed to queue task: " + entry, e);
        }
    }
    
    private <T> T submitAndWaitForTask(final Callable<T> task)  {
        Future<T> future = taskService.submit(getClass(), task);
        try {
            return future.get();
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void track(final StatsTracker tracker, long now) {
        queueUpdateTask(tracker, now, true);
    }

    private void trackImpl(final StatsTracker tracker, final long now) {
        stateLock.lock();
        try {
            hits++;

            if (firstHitStamp == -1) {
                firstHitStamp = now;
            }

            lastHitStamp = now;

        } finally {
            stateLock.unlock();
        }

        logger.trace("Track: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_TRACKING, key, tracker);
    }

    @Override
    public long getHits() {
        return hits;
    }

    @Override
    public long getFirstHitStamp() {
        return firstHitStamp;
    }

    @Override
    public long getLastHitStamp() {
        return lastHitStamp;
    }

    @Override
    public long getCommits() {
        return commits;
    }

    @Override
    public void update(final StatsTracker tracker, final long now) {
        queueUpdateTask(tracker, now, false);
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
                try {
                    dataRecorder.update(this, tracker, now);
                } catch (Exception e) {
                    logger.error("Failed to update " + dataRecorder, e);
                }
            }
        } finally {
            stateLock.unlock();
        }

        logger.trace("Commit: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_COMMITTED, key, tracker);
    }

    @Override
    public double getFirst() {
        Double firstValue = first;

        if (firstValue == null) {
            return Double.NaN;
        }

        return firstValue;
    }

    @Override
    public double getLast() {
        return last;
    }

    @Override
    public double getMin() {
        stateLock.lock();
        try {
            if (commits == 0) {
                return EMPTY_VALUE;
            }
            return min;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public double getMax() {
        stateLock.lock();
        try {
            if (commits == 0) {
                return EMPTY_VALUE;
            }
            return max;
        } finally {
            stateLock.unlock();
        }
    }

    @Override
    public double getSum() {
        return sum;
    }

    @Override
    public void restore(final DataSet dataSet) {
        stateLock.lock();
        try {
            hits = dataSet.getField(DataSet.Field.COMMITS, Long.class);
            firstHitStamp = dataSet.getField(DataSet.Field.FIRST_HIT_STAMP, Date.class).getTime();
            lastHitStamp = dataSet.getField(DataSet.Field.LAST_HIT_STAMP, Date.class).getTime();
            commits = dataSet.getField(DataSet.Field.COMMITS, Long.class);
            if (commits > 0) {
                first = dataSet.getField(DataSet.Field.FIRST, Double.class);
                last = dataSet.getField(DataSet.Field.LAST, Double.class);
                min = dataSet.getField(DataSet.Field.MIN, Double.class);
                max = dataSet.getField(DataSet.Field.MAX, Double.class);
                sum = dataSet.getField(DataSet.Field.SUM, Double.class);
            } else {
                first = null;
                last = Double.NaN;
                min = Double.POSITIVE_INFINITY;
                max = Double.NEGATIVE_INFINITY;
                sum = 0;
            }

            for (DataRecorder dataRecorder : dataRecorders) {
                try {
                    dataRecorder.restore(dataSet);
                } catch (Exception e) {
                    logger.error("failed to restore " + dataRecorder, e);
                }
            }
        } finally {
            stateLock.unlock();
        }

        logger.trace("Restore: {}", this);

        eventManager.fireEvent(StatsEventType.SESSION_RESTORED, key, this);
    }

    @Override
    public void clear() {
        submitAndWaitForTask(new ClearTask());
    }

    private void clearState() {
        stateLock.lock();
        try {
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
                try {
                    dataRecorder.clear();
                } catch (Exception e) {
                    logger.error("Failed to clear " + dataRecorder, e);
                }
            }
        } finally {
            stateLock.unlock();
        }
    }

    private void fireCleared() {
        logger.trace("Clear: {}", this);

        eventManager.fireEvent(StatsEventType.SESSION_CLEARED, key, this);
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
    
    @Override
    public DataSet drainData() {
        return submitAndWaitForTask(new DrainDataTask());
    }

    private void processUpdateQueue() {
        // Re-query queue size to avoid allocating a buffer if some
        // other thread has already processed the events
        int count = updateQueue.size();
        if (count > 0) {
            // Extract all entries using poll() to avoid obtaining
            // the queue write lock which would block a tracker
            // client.
            // A call to updateQueue.toArray(...) is likely to lock
            // the entire queue.
            // Drain the queue as soon as possible then process
            // later so that
            // other task threads can "short circuit" more
            // effectively.
            TrackerEntry[] entries = new TrackerEntry[count];
            for (int i = 0; i < count; i++) {
                entries[i] = updateQueue.poll();
                assert entries[i] != null;
            }

            // Do the updates within the updateQueueProcessingLock
            // to ensure they are executed linearly in the exact
            // order in which they were submitted.
            for (TrackerEntry entry : entries) {
                if (entry.track) {
                    trackImpl(entry.tracker, entry.now);
                } else {
                    updateImpl(entry.tracker, entry.now);
                }
            }
        }
    }

    /* INNER CLASSES */

    private final class ProcessUpdateQueueTask implements Runnable {

        @Override
        public void run() {
            // Only do work if there is something in the updateQueue
            if (!updateQueue.isEmpty()) {
                // Do not allow other threads to process entries
                updateQueueProcessingLock.lock();
                try {
                    processUpdateQueue();
                } finally {
                    updateQueueProcessingLock.unlock();
                }
            }
        }
    }

    private static final class TrackerEntry {

        private final StatsTracker tracker;
        private final long now;
        private final boolean track;

        private TrackerEntry(final StatsTracker tracker, final long now, final boolean track) {
            this.tracker = tracker;
            this.now = now;
            this.track = track;
        }
        
        @Override
        public String toString() {
            return tracker + " @ " + now + " " + (track ? "track" : "update");
        }
    }

    private final class DrainDataTask implements Callable<DataSet> {
        @Override
        public DataSet call() throws Exception {
            // Do not allow other threads to process entries
            updateQueueProcessingLock.lock();
            DataSet data;
            try {
                processUpdateQueue();
                data = collectData();
                clearState();
            } finally {
                updateQueueProcessingLock.unlock();
            }
            fireCleared();
            return data;
        }
    }

    private final class ClearTask implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            updateQueueProcessingLock.lock();
            try {
                updateQueue.clear();
                clearState();
            } finally {
                updateQueueProcessingLock.unlock();
            }
            fireCleared();
            return null;
        }
    }
}

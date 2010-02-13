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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 * An implementation of {@link StatsSession} that reads ands writes data fields atomically
 * and locks calls to {@link #track(StatsTracker, long)}, {@link #update(StatsTracker, long)},
 * {@link #collectData()}, and {@link #clear()}. This allows guaranteed consistency between the
 * values contained in the result of {@link #collectData()}, however, at the cost of increased
 * statistics collection overhead and reduced scalability.
 *
 * The other methods that are not mentioned above are not locked, but since this class
 * extends {@link ConcurrentStatsSession}, thread safety is retained.
 *
 * @author The Stajistics Project
 */
public class SynchronousStatsSession extends AbstractStatsSession {

    private static final long serialVersionUID = -32360770003453383L;

    private static final Logger logger = LoggerFactory.getLogger(SynchronousStatsSession.class);

    protected final Lock lock = new ReentrantLock();

    protected long hits = 0L;
    protected long firstHitStamp = -1;
    protected long lastHitStamp = -1;
    protected long commits = 0;

    protected Double first = null;
    protected double last = Double.NaN;
    protected double min = Double.POSITIVE_INFINITY;
    protected double max = Double.NEGATIVE_INFINITY;
    protected double sum = 0;

    /**
     * {@inheritDoc}
     */
    public SynchronousStatsSession(final StatsKey key,
                                   final StatsEventManager eventManager) {
        super(key, eventManager);
    }

    /**
     * {@inheritDoc}
     */
    public SynchronousStatsSession(final StatsKey key,
                                   final StatsEventManager eventManager,
                                   final DataRecorder... dataRecorders) {
        super(key, eventManager, dataRecorders);
    }

    /**
     * {@inheritDoc}
     */
    public SynchronousStatsSession(final StatsKey key,
                                   final StatsEventManager eventManager,
                                   final List<DataRecorder> dataRecorders) {
        super(key, eventManager, dataRecorders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void track(final StatsTracker tracker,
                      long now) {
        lock.lock();
        try {
            if (now < 0) {
                now = System.currentTimeMillis();
            }

            hits++;

            if (firstHitStamp == -1) {
                firstHitStamp = now;
            }

            lastHitStamp = now;

        } finally {
            lock.unlock();
        }

        logger.trace("Track: {}", this);

        // Fire the event outside of the lock
        eventManager.fireEvent(StatsEventType.TRACKER_TRACKING, key, tracker);
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
    public void update(final StatsTracker tracker,
                       long now) {

        //StatsSession snapshot;

        lock.lock();
        try {

            final double currentValue = tracker.getValue();

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

            //snapshot = new ImmutableStatsSession(this);

        } finally {
            lock.unlock();
        }

        logger.info("Commit: {}", this);

        // Fire the event outside of the lock
        eventManager.fireEvent(StatsEventType.TRACKER_COMMITTED, key, tracker);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public double getFirst() {
        if (first == null) {
            return Double.NaN;
        }

        return first;
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

    @Override
    public void restore(final DataSet dataSet) {
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
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
    }

}

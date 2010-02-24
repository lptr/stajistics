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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.session.recorder.DataRecorders;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.AtomicDouble;

/**
 * An implementation of {@link StatsSession} that reads and writes data fields atomically
 * without locking. This allows scalable updates that minimize the runtime overhead of statistics
 * collection. However, the cost of using this implementation is that the {@link DataSet} returned from
 * {@link #collectData()} may contain values that are not related to one another.
 * For example, the DataSet may contain all the data from update #1, but only half of the data
 * from update #2 (because update #2 is executing simultaneously to the {@link #collectData()} call).
 * For a {@link StatsSession} implementation that guarantees data integrity,
 * see {@link org.stajistics.session.AsynchronousStatsSession}.
 *
 * @see org.stajistics.session.AsynchronousStatsSession
 *
 * @author The Stajistics Project
 */
public class ConcurrentStatsSession extends AbstractStatsSession {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentStatsSession.class);

    protected final AtomicLong hits = new AtomicLong(DataSet.Field.Default.HITS);
    protected final AtomicLong firstHitStamp = new AtomicLong(DataSet.Field.Default.FIRST_HIT_STAMP);
    protected volatile long lastHitStamp = DataSet.Field.Default.LAST_HIT_STAMP;
    protected final AtomicLong commits = new AtomicLong(DataSet.Field.Default.COMMITS);

    // The proper default is taken care of in getFirst()
    protected final AtomicReference<Double> first = new AtomicReference<Double>(null);

    protected volatile double last = DataSet.Field.Default.LAST;
    protected final AtomicDouble min = new AtomicDouble(Double.POSITIVE_INFINITY);
    protected final AtomicDouble max = new AtomicDouble(Double.NEGATIVE_INFINITY);
    protected final AtomicDouble sum = new AtomicDouble(DataSet.Field.Default.SUM);

    public ConcurrentStatsSession(final StatsKey key,
                                  final StatsEventManager eventManager,
                                  final DataRecorder... dataRecorders) {
        super(key,
              eventManager,
              DataRecorders.lockingIfNeeded(dataRecorders));
    }

    @Override
    public void track(final StatsTracker tracker,
                      long now) {
        if (now < 0) {
            now = System.currentTimeMillis();
        }

        hits.incrementAndGet();

        if (firstHitStamp.get() == DataSet.Field.Default.FIRST_HIT_STAMP) {
            firstHitStamp.compareAndSet(DataSet.Field.Default.FIRST_HIT_STAMP, now);
        }
        lastHitStamp = now;

        logger.trace("Track: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_TRACKING, key, tracker);
    }

    @Override
    public long getHits() {
        return hits.get();
    }

    @Override
    public long getFirstHitStamp() {
        return firstHitStamp.get();
    }

    @Override
    public long getLastHitStamp() {
        return lastHitStamp;
    }

    @Override
    public long getCommits() {
        return commits.get();
    }

    @Override
    public void update(final StatsTracker tracker, long now) {

        final double currentValue = tracker.getValue();
        double tmp;

        commits.incrementAndGet();

        // First
        if (first.get() == null) {
            first.compareAndSet(null, currentValue);
        }

        // Last
        last = currentValue;

        // Min
        for (;;) {
            tmp = min.get();
            if (currentValue < tmp) {
                if (min.compareAndSet(tmp, currentValue)) {
                    break;
                }
            } else {
                break;
            }
        }

        // Max
        for (;;) {
            tmp = max.get();
            if (currentValue > tmp) {
                if (max.compareAndSet(tmp, currentValue)) {
                    break;
                }
            } else {
                break;
            }
        }

        // Sum
        sum.addAndGet(currentValue);

        for (DataRecorder dataRecorder : dataRecorders) {
            try {
                dataRecorder.update(this, tracker, now);
            } catch (Exception e) {
                logger.error("Failed to update " + dataRecorder, e);
            }
        }

        logger.trace("Commit: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_COMMITTED, key, tracker);
    }

    @Override
    public double getFirst() {
        Double firstValue = first.get();

        if (firstValue == null) {
            return DataSet.Field.Default.FIRST;
        }

        return firstValue;
    }

    @Override
    public double getLast() {
        return last;
    }

    @Override
    public double getMin() {
        Double result = min.get();
        if (result.equals(Double.POSITIVE_INFINITY)) {
            result = DataSet.Field.Default.MIN;
        }
        return result;
    }

    @Override
    public double getMax() {
        Double result = max.get();
        if (result.equals(Double.NEGATIVE_INFINITY)) {
            result = DataSet.Field.Default.MAX;
        }
        return result;
    }

    @Override
    public double getSum() {
        return sum.get();
    }

    @Override
    public void restore(final DataSet dataSet) {
        if (dataSet == null) {
            throw new NullPointerException("dataSet");
        }

        clearState();

        if (!dataSet.isEmpty()) {

            Long restoredHits = dataSet.getField(DataSet.Field.HITS,
                                                 DataSet.Field.Default.HITS);
            Long restoredFirstHitStamp = dataSet.getField(DataSet.Field.FIRST_HIT_STAMP, 
                                                          DataSet.Field.Default.FIRST_HIT_STAMP);
            Long restoredLastHitStamp = dataSet.getField(DataSet.Field.LAST_HIT_STAMP, 
                                                         DataSet.Field.Default.LAST_HIT_STAMP);

            // Only restore if hits, firstHitStamp, and lastHitStamp are defined
            if (restoredHits > DataSet.Field.Default.HITS &&
                    restoredFirstHitStamp > DataSet.Field.Default.FIRST_HIT_STAMP &&
                    restoredLastHitStamp > DataSet.Field.Default.LAST_HIT_STAMP) {

                hits.set(restoredHits);
                firstHitStamp.set(restoredFirstHitStamp);
                lastHitStamp = restoredLastHitStamp;

                Long restoredCommits = dataSet.getField(DataSet.Field.COMMITS,
                                                        DataSet.Field.Default.COMMITS);
                Double restoredFirst = dataSet.getField(DataSet.Field.FIRST, Double.class);
                Double restoredLast = dataSet.getField(DataSet.Field.LAST, Double.class);

                // Only restore "update()" data if commits, first, and last are defined
                if (restoredCommits > DataSet.Field.Default.COMMITS &&
                        restoredFirst != null && 
                        restoredLast != null) {

                    commits.set(restoredCommits);
                    first.set(restoredFirst);
                    last = restoredLast;
                    min.set(dataSet.getField(DataSet.Field.MIN, DataSet.Field.Default.MIN));
                    max.set(dataSet.getField(DataSet.Field.MAX, DataSet.Field.Default.MAX));
                    sum.set(dataSet.getField(DataSet.Field.SUM, DataSet.Field.Default.SUM));

                    // Restore DataRecorders
                    for (DataRecorder dataRecorder : dataRecorders) {
                        try {
                            dataRecorder.restore(dataSet);
                        } catch (Exception e) {
                            logger.error("Failed to restore " + dataRecorder, e);
                        }
                    }
                }
            }
        }

        logger.trace("Restore: {}", this);

        eventManager.fireEvent(StatsEventType.SESSION_RESTORED, key, this);
    }

    @Override
    public void clear() {
        clearState();
        fireCleared();
    }

    private void clearState() {
        hits.set(DataSet.Field.Default.HITS);
        firstHitStamp.set(DataSet.Field.Default.FIRST_HIT_STAMP);
        lastHitStamp = DataSet.Field.Default.LAST_HIT_STAMP;
        commits.set(DataSet.Field.Default.COMMITS);
        first.set(null); // The proper default is taken care of in getFirst()
        last = DataSet.Field.Default.LAST;
        min.set(DataSet.Field.Default.MIN);
        max.set(DataSet.Field.Default.MAX);
        sum.set(DataSet.Field.Default.SUM);

        for (DataRecorder dataRecorder : dataRecorders) {
            try {
                dataRecorder.clear();
            } catch (Exception e) {
                logger.error("Failed to clear " + dataRecorder, e);
            }
        }
    }

    private void fireCleared() {
        logger.trace("Clear: {}", this);

        eventManager.fireEvent(StatsEventType.SESSION_CLEARED, key, this);
    }

    @Override
    public DataSet drainData() {
        DataSet data = collectData();
        clear();
        return data;
    }
}

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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.AtomicDouble;

/**
 * An implementation of {@link StatsSession} that reads and writes data fields atomically
 * without locking. This allows scalable updates that minimize the runtime overhead of statistics
 * collection. However, the cost of using this implementation is that the result of
 * {@link #collectData()} may not contain values that are not consistent with one another.
 *
 * @author The Stajistics Project
 */
public class ConcurrentStatsSession extends AbstractStatsSession {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentStatsSession.class);

    protected final AtomicLong hits = new AtomicLong(0);
    protected final AtomicLong firstHitStamp = new AtomicLong(-1);
    protected volatile long lastHitStamp = -1;
    protected final AtomicLong commits = new AtomicLong(0);

    protected final AtomicReference<Double> first = new AtomicReference<Double>(null);
    protected volatile double last = Double.NaN;
    protected final AtomicDouble min = new AtomicDouble(Double.POSITIVE_INFINITY);
    protected final AtomicDouble max = new AtomicDouble(Double.NEGATIVE_INFINITY);
    protected final AtomicDouble sum = new AtomicDouble(0);

    public ConcurrentStatsSession(final StatsKey key,
                                  final StatsEventManager eventManager,
                                  final DataRecorder... dataRecorders) {
        super(key, eventManager, dataRecorders);
    }

    @Override
    public void track(final StatsTracker tracker,
                      long now) {
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
            dataRecorder.update(this, tracker, now);
        }

        logger.trace("Commit: {}", this);

        eventManager.fireEvent(StatsEventType.TRACKER_COMMITTED, key, tracker);
    }

    @Override
    public double getFirst() {
        Double firstValue = first.get();

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
        return min.get();
    }

    @Override
    public double getMax() {
        return max.get();
    }

    @Override
    public double getSum() {
        return sum.get();
    }

    @Override
    public void restore(final DataSet dataSet) {
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

        logger.trace("Restore: {}", this);

        eventManager.fireEvent(StatsEventType.SESSION_RESTORED, key, this);
    }

    @Override
    public void clear() {
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

        logger.trace("Clear: {}", this);

        eventManager.fireEvent(StatsEventType.SESSION_CLEARED, key, this);
    }

}

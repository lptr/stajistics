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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.FieldSetFactory;
import org.stajistics.data.DataSet.StandardMetaField;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.session.recorder.DataRecorders;
import org.stajistics.tracker.Tracker;
import org.stajistics.util.AtomicDouble;
import org.stajistics.util.Misc;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>An implementation of {@link StatsSession} that reads and writes data fields atomically
 * without locking. This allows scalable updates that minimize the runtime overhead of statistics
 * collection. However, the cost of using this implementation is that the {@link DataSet} returned from
 * {@link #collectData()} may contain values that are not related to one another.
 * For example, the DataSet may contain all the data from update #1, but only half of the data
 * from update #2 (because update #2 is executing simultaneously to the {@link #collectData()} call).
 * For a {@link StatsSession} implementation that guarantees data integrity,
 * see {@link org.stajistics.session.AsynchronousSession}.</p>
 *
 * <p>Due to the concurrent nature of this session implementation, the associated {@link DataRecorder}s
 * must be thread safe. {@link DataRecorder}s that are passed into the constructor are passed through
 * the {@link DataRecorders#lockingIfNeeded(DataRecorder[])} method in order to ensure thread safe
 * usage. Note that if any {@link DataRecorder}s are wrapped in a locking decorator, it could
 * negatively impact performance of the client application. For optimal performance, use
 * {@link DataRecorder} implementations that are thread safe through the use of atomic primitives.</p>
 *
 * @see org.stajistics.session.AsynchronousSession
 *
 * @author The Stajistics Project
 */
public class ConcurrentSession extends AbstractStatsSession {

    public static final Factory FACTORY = new Factory();

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentSession.class);

    protected final AtomicLong hits = new AtomicLong(0L);
    protected final AtomicLong firstHitStamp = new AtomicLong(DataSet.UNINITIALIZED_TIMESTAMP);
    protected volatile long lastHitStamp = DataSet.UNINITIALIZED_TIMESTAMP;
    protected final AtomicLong commits = new AtomicLong(0L);

    // The proper default is taken care of in getFirst()
    protected final AtomicReference<Double> first = new AtomicReference<Double>(null);

    protected volatile double last = DataSet.UNINITIALIZED_VALUE;
    protected final AtomicDouble min = new AtomicDouble(Double.POSITIVE_INFINITY);
    protected final AtomicDouble max = new AtomicDouble(Double.NEGATIVE_INFINITY);
    protected final AtomicDouble sum = new AtomicDouble(0D);

    public ConcurrentSession(final StatsKey key,
                                  final EventManager eventManager,
                                  final FieldSetFactory fieldSetFactory,
                                  final DataRecorder... dataRecorders) {
        super(key,
              eventManager,
              fieldSetFactory,
              DataRecorders.lockingIfNeeded(dataRecorders));
    }

    @Override
    public void track(final Tracker tracker,
                      long now) {
        if (now < 0) {
            now = System.currentTimeMillis();
        }

        hits.incrementAndGet();

        if (firstHitStamp.get() == DataSet.UNINITIALIZED_TIMESTAMP) {
            firstHitStamp.compareAndSet(DataSet.UNINITIALIZED_TIMESTAMP, now);
        }
        lastHitStamp = now;

        logger.trace("Track: {}", this);

        eventManager.fireEvent(EventType.TRACKER_TRACKING, key, tracker);
    }

    @Override
    public long getHits() {
        return hits.get();
    }

    @Override
    protected void setHits(final long hits) {
        this.hits.set(hits);
    }

    @Override
    public long getFirstHitStamp() {
        return firstHitStamp.get();
    }

    @Override
    protected void setFirstHitStamp(long firstHitStamp) {
        this.firstHitStamp.set(firstHitStamp);
    }

    @Override
    public long getLastHitStamp() {
        return lastHitStamp;
    }

    @Override
    protected void setLastHitStamp(final long lastHitStamp) {
        this.lastHitStamp = lastHitStamp;
    }

    @Override
    public long getCommits() {
        return commits.get();
    }

    @Override
    protected void setCommits(final long commits) {
        this.commits.set(commits);
    }

    @Override
    public void update(final Tracker tracker, long now) {

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
                Misc.logSwallowedException(logger,
                                           e,
                                           "Failed to update {}",
                                           dataRecorder);
            }
        }

        logger.trace("Commit: {}", this);

        eventManager.fireEvent(EventType.TRACKER_COMMITTED, key, tracker);
    }

    @Override
    public double getFirst() {
        Double firstValue = first.get();

        if (firstValue == null) {
            return DataSet.UNINITIALIZED_VALUE;
        }

        return firstValue;
    }

    @Override
    protected void setFirst(final Double first) {
        this.first.set(first);
    }

    @Override
    public double getLast() {
        return last;
    }

    @Override
    protected void setLast(final double last) {
        this.last = last;
    }

    @Override
    public double getMin() {
        Double result = min.get();
        if (result.equals(Double.POSITIVE_INFINITY)) {
            result = DataSet.UNINITIALIZED_VALUE;
        }
        return result;
    }

    @Override
    protected void setMin(final double min) {
        this.min.set(min);
    }

    @Override
    public double getMax() {
        Double result = max.get();
        if (result.equals(Double.NEGATIVE_INFINITY)) {
            result = DataSet.UNINITIALIZED_VALUE;
        }
        return result;
    }

    @Override
    protected void setMax(double max) {
        this.max.set(max);
    }

    @Override
    public double getSum() {
        return sum.get();
    }

    @Override
    protected void setSum(final double sum) {
        this.sum.set(sum);
    }

    @Override
    public void restore(final DataSet dataSet) {
        if (dataSet == null) {
            throw new NullPointerException("dataSet");
        }

        clearState();
        restoreState(dataSet);

        logger.trace("Restore: {}", this);

        eventManager.fireEvent(EventType.SESSION_RESTORED, key, this);
    }

    @Override
    public void clear() {
        clearState();

        logger.trace("Clear: {}", this);

        eventManager.fireEvent(EventType.SESSION_CLEARED, key, this);
    }

    @Override
    public DataSet drainData() {
        DataSetBuilder data = fields.newDataSetBuilder();

        data.set(StandardMetaField.drainedSession, 1L);

        clear();
        return data.build();
    }

    /* NESTED CLASSES */

    public static final class Factory implements StatsSessionFactory {
        @Override
        public StatsSession createSession(final StatsKey key,
                                          final StatsManager manager,
                                          final FieldSetFactory fieldSetFactory,
                                          final DataRecorder[] dataRecorders) {
            return new ConcurrentSession(key,
                                         manager.getEventManager(),
                                         fieldSetFactory,
                                         dataRecorders);
        }
    }

}

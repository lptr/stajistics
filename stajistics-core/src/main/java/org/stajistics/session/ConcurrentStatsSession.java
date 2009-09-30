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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.DefaultDataSet;
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
public class ConcurrentStatsSession implements StatsSession {

    private static final long serialVersionUID = -5265957157097835416L;

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentStatsSession.class);

    private static final DecimalFormat DECIMAL_FORMAT;
    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setDecimalSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.###", dfs);
        DECIMAL_FORMAT.setGroupingSize(Byte.MAX_VALUE);
    }

    protected final StatsKey key;
    protected final StatsEventManager eventManager;

    protected final AtomicLong hits = new AtomicLong(0);
    protected final AtomicLong firstHitStamp = new AtomicLong(-1);
    protected volatile long lastHitStamp = -1;
    protected final AtomicLong commits = new AtomicLong(0);

    protected final AtomicReference<Double> first = new AtomicReference<Double>(null);
    protected volatile double last = Double.NaN;
    protected final AtomicDouble min = new AtomicDouble(Double.POSITIVE_INFINITY);
    protected final AtomicDouble max = new AtomicDouble(Double.NEGATIVE_INFINITY);
    protected final AtomicDouble sum = new AtomicDouble(0);

    protected final List<DataRecorder> dataRecorders;

    public ConcurrentStatsSession(final StatsKey key, final StatsEventManager eventManager) {
        this(key, eventManager, (List<DataRecorder>)null);
    }

    public ConcurrentStatsSession(final StatsKey key, 
                                  final StatsEventManager eventManager, 
                                  final DataRecorder... dataRecorders) {
        this(key, eventManager, Arrays.asList(dataRecorders));
    }

    public ConcurrentStatsSession(final StatsKey key, 
                                  final StatsEventManager eventManager, 
                                  final List<DataRecorder> dataRecorders) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (eventManager == null) {
            throw new NullPointerException("eventManager");
        }

        this.key = key;
        this.eventManager = eventManager;

        if (dataRecorders == null || dataRecorders.isEmpty()) {
            this.dataRecorders = Collections.emptyList();
        } else {
            this.dataRecorders = new ArrayList<DataRecorder>(dataRecorders);
        }
    }

    /**
     * {@inheritDoc}
     */
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

        fireTrackingEvent(this, tracker);
    }

    protected void fireTrackingEvent(final StatsSession session,
                                     final StatsTracker tracker) {
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
    public StatsKey getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final StatsTracker tracker, long now) {

        final double currentValue = tracker.getValue();
        double tmp;

        commits.incrementAndGet();

        // First
        if (first.get() == null) {
            first.compareAndSet(null, new Double(currentValue));
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

        logger.info("Commit: {}", this);

        fireUpdateEvent(this, tracker);
    }

    protected void fireUpdateEvent(final StatsSession session,
                                   final StatsTracker tracker) {
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

        return firstValue.doubleValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getLast() {
        return this.last;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMin() {
        return this.min.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMax() {
        return this.max.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getSum() {
        return this.sum.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSet collectData() {

        DataSet dataSet = new DefaultDataSet();

        dataSet.setField(DataSet.Field.HITS, getHits());
        dataSet.setField(DataSet.Field.FIRST_HIT_STAMP, new Date(getFirstHitStamp()));
        dataSet.setField(DataSet.Field.LAST_HIT_STAMP, new Date(getLastHitStamp()));
        dataSet.setField(DataSet.Field.COMMITS, getCommits());
        dataSet.setField(DataSet.Field.FIRST, getFirst());
        dataSet.setField(DataSet.Field.LAST, getLast());
        dataSet.setField(DataSet.Field.MIN, getMin());
        dataSet.setField(DataSet.Field.MAX, getMax());
        dataSet.setField(DataSet.Field.SUM, getSum());

        for (DataRecorder dataRecorder : dataRecorders) {
            dataRecorder.collectData(this, dataSet);
        }

        return dataSet;
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
    }

    /**
     * {@inheritDoc}
     */
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

        eventManager.fireEvent(StatsEventType.SESSION_CLEARED, key, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataRecorder> getDataRecorders() {
        return Collections.unmodifiableList(dataRecorders);
    }

    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder(512);

        buf.append(StatsSession.class.getSimpleName());
        buf.append("[key=");
        buf.append(key);
        buf.append(",hits=");
        buf.append(getHits());
        buf.append(",firstHitStamp=");
        buf.append(new Date(getFirstHitStamp()));
        buf.append(",lastHitStamp=");
        buf.append(new Date(getLastHitStamp()));
        buf.append(",commits=");
        buf.append(getCommits());
        buf.append(",first=");
        buf.append(DECIMAL_FORMAT.format(getFirst()));
        buf.append(",last=");
        buf.append(DECIMAL_FORMAT.format(getLast()));
        buf.append(",min=");
        buf.append(DECIMAL_FORMAT.format(getMin()));
        buf.append(",max=");
        buf.append(DECIMAL_FORMAT.format(getMax()));
        buf.append(",sum=");
        buf.append(DECIMAL_FORMAT.format(getSum()));
        buf.append(']');

        return buf.toString();
    }

}

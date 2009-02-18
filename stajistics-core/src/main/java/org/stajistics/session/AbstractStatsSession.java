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
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.AtomicDouble;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsSession implements StatsSession {

    private static final long serialVersionUID = -5265957157097835416L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractStatsSession.class);

    private static final DecimalFormat DECIMAL_FORMAT;
    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setDecimalSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.###", dfs);
        DECIMAL_FORMAT.setGroupingSize(Byte.MAX_VALUE);
    }

    protected final StatsKey key;

    protected final AtomicLong hits = new AtomicLong(0);
    protected final AtomicLong firstHitStamp = new AtomicLong(0);
    protected final AtomicLong lastHitStamp = new AtomicLong(0);
    protected final AtomicLong commits = new AtomicLong(0);

    protected final AtomicReference<Double> first = new AtomicReference<Double>(null);
    protected final AtomicDouble last = new AtomicDouble(Double.NaN);
    protected final AtomicDouble min = new AtomicDouble(Double.MAX_VALUE);
    protected final AtomicDouble max = new AtomicDouble(Double.MIN_VALUE);
    protected final AtomicDouble sum = new AtomicDouble(0);

    protected final ConcurrentMap<String,Object> clientAttrs = new ConcurrentHashMap<String,Object>();

    protected Lock updateLock = null;

    public AbstractStatsSession(final StatsKey key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        this.key = key;
    }
    
    @Override
    public void open(final StatsTracker tracker, 
                     long now) {

        if (now < 0) {
            now = System.currentTimeMillis();
        }

        hits.incrementAndGet();

        if (firstHitStamp.get() == 0) {
            firstHitStamp.compareAndSet(0, now);
        }
        lastHitStamp.set(now);

        if (logger.isDebugEnabled()) {
            logger.debug("Open: " + this);
        }

        StatsManager.getEventManager()
                    .fireEvent(StatsEventType.TRACKER_OPENED, this, tracker);
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
        return lastHitStamp.get();
    }

    @Override
    public long getCommits() {
        return commits.get();
    }

    @Override
    public StatsKey getKey() {
        return key;
    }

    @Override
    public void update(final StatsTracker tracker, long now) {

        if (now < 0) {
            now = System.currentTimeMillis();
        }

        double currentValue = tracker.getValue();
        double tmp;

        Lock myLock = updateLock;

        try {
            if (myLock != null) {
                myLock.lock();
            }

            commits.incrementAndGet();

            // First
            if (first.get() == null) {
                first.compareAndSet(null, new Double(currentValue));
            }

            // Last
            last.set(currentValue);

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
            sum.getAndAdd(currentValue);

            updateImpl(tracker, now);

        } finally {
            if (myLock != null) {
                myLock.unlock();
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("Commit: " + this);
        }

        StatsManager.getEventManager()
                    .fireEvent(StatsEventType.TRACKER_COMMITTED, this, tracker);
    }

    @Override
    public double getFirst() {
        Double firstValue = first.get();

        if (firstValue == null) {
            return Double.NaN;
        }

        return firstValue.doubleValue();
    }

    @Override
    public double getLast() {
        return this.last.get();
    }

    @Override
    public double getMin() {
        return this.min.get();
    }

    @Override
    public double getMax() {
        return this.max.get();
    }

    public double getSum() {
        return this.sum.get();
    }

    @Override
    public Object getAttribute(final String name) {
        return clientAttrs.get(name);
    }

    @Override
    public Map<String,Object> getAttributes() {
        return Collections.unmodifiableMap(clientAttrs);
    }

    @Override
    public final StatsSession snapshot() {
        try {
            updateLock.lock();

            return new ImmutableStatsSession(this);

        } finally {
            updateLock.unlock();
        }
    }

    protected abstract void updateImpl(final StatsTracker tracker, 
                                        final long now);


    protected void appendStat(final StringBuilder buf,
                              final String name,
                              final Object value) {
        buf.append(',');
        buf.append(name);
        buf.append('=');

        if (value instanceof Double) {
            buf.append(DECIMAL_FORMAT.format(value));
        } else {
            buf.append(String.valueOf(value));
        }
    }

    protected abstract void appendStats(final StringBuilder buf);

    public String toString() {

        StringBuilder buf = new StringBuilder();

        buf.append(StatsSession.class.getSimpleName());
        buf.append("[key=");
        buf.append(key);

        appendStat(buf, Attributes.HITS, getHits());
        appendStat(buf, Attributes.FIRST_HIT_STAMP, new java.util.Date(getFirstHitStamp()).toString());
        appendStat(buf, Attributes.LAST_HIT_STAMP, new java.util.Date(getLastHitStamp()).toString());
        appendStat(buf, Attributes.COMMITS, getCommits());
        appendStat(buf, Attributes.FIRST, getFirst());
        appendStat(buf, Attributes.LAST, getLast());
        appendStat(buf, Attributes.MIN, getMin());
        appendStat(buf, Attributes.MAX, getMax());
        appendStat(buf, Attributes.SUM, getSum());

        appendStats(buf);

        buf.append(']');

        return buf.toString();
    }

    

}

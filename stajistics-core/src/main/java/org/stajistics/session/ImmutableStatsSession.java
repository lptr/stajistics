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

import java.util.Collections;
import java.util.List;

import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.NullDataSet;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 * A {@link StatsSession} implementation which does not respond to updates.
 *
 * @author The Stajistics Project
 */
public class ImmutableStatsSession implements StatsSession {

    private final StatsKey key;
    private final DataSet dataSet;

    public ImmutableStatsSession(final StatsKey key) {
        this(key, NullDataSet.getInstance());
    }

    public ImmutableStatsSession(final StatsSession copyFrom) {
        this(copyFrom.getKey(),
             copyFrom.collectData());
    }

    public ImmutableStatsSession(final StatsKey key,
                                 final DataSet dataSet) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (dataSet == null) {
            throw new NullPointerException("dataSet");
        }

        this.key = key;
        this.dataSet = dataSet;
    }

    @Override
    public StatsKey getKey() {
        return key;
    }

    /**
     * @return An empty List.
     */
    @Override
    public List<DataRecorder> getDataRecorders() {
        return Collections.emptyList();
    }

    @Override
    public DataSet collectData() {
        return dataSet;
    }

    /**
     * Does nothing.
     */
    @Override
    public void restore(DataSet dataSet) {}

    @Override
    public double getFirst() {
        return dataSet.getField(DataSet.Field.FIRST, Double.class);
    }

    @Override
    public long getFirstHitStamp() {
        return dataSet.getField(DataSet.Field.FIRST_HIT_STAMP, Long.class);
    }

    @Override
    public long getHits() {
        return dataSet.getField(DataSet.Field.HITS, Long.class);
    }

    @Override
    public long getCommits() {
        return dataSet.getField(DataSet.Field.COMMITS, Long.class);
    }

    @Override
    public double getLast() {
        return dataSet.getField(DataSet.Field.LAST, Double.class);
    }

    @Override
    public long getLastHitStamp() {
        return dataSet.getField(DataSet.Field.LAST_HIT_STAMP, Long.class);
    }

    @Override
    public double getMax() {
        return dataSet.getField(DataSet.Field.MAX, Double.class);
    }

    @Override
    public double getMin() {
        return dataSet.getField(DataSet.Field.MIN, Double.class);
    }

    @Override
    public double getSum() {
        return dataSet.getField(DataSet.Field.SUM, Double.class);
    }

    /**
     * Does nothing.
     */
    @Override
    public void track(StatsTracker tracker, long now) {}

    /**
     * Does nothing.
     */
    @Override
    public void update(StatsTracker tracker, long now) {}

    /**
     * Does nothing.
     */
    @Override
    public void clear() {}
}

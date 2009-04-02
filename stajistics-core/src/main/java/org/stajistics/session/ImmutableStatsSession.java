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
import org.stajistics.session.data.DataSet;
import org.stajistics.session.data.NullDataSet;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ImmutableStatsSession implements StatsSession {

    private static final long serialVersionUID = -1491982324221671738L;

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

    @Override
    public List<DataRecorder> getDataRecorders() {
        return Collections.emptyList();
    }

    @Override
    public DataSet collectData() {
        return dataSet;
    }

    @Override
    public double getFirst() {
        return (Double)dataSet.getField(DataSet.Field.FIRST);
    }

    @Override
    public long getFirstHitStamp() {
        return (Long)dataSet.getField(DataSet.Field.FIRST_HIT_STAMP);
    }

    @Override
    public long getHits() {
        return (Long)dataSet.getField(DataSet.Field.HITS);
    }

    @Override
    public long getCommits() {
        return (Long)dataSet.getField(DataSet.Field.COMMITS);
    }

    @Override
    public double getLast() {
        return (Double)dataSet.getField(DataSet.Field.LAST);
    }

    @Override
    public long getLastHitStamp() {
        return (Long)dataSet.getField(DataSet.Field.LAST_HIT_STAMP);
    }

    @Override
    public double getMax() {
        return (Double)dataSet.getField(DataSet.Field.MAX);
    }

    @Override
    public double getMin() {
        return (Double)dataSet.getField(DataSet.Field.MIN);
    }

    @Override
    public double getSum() {
        return (Double)dataSet.getField(DataSet.Field.SUM);
    }

    @Override
    public void track(StatsTracker tracker, long now) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(StatsTracker tracker, long now) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {}
}

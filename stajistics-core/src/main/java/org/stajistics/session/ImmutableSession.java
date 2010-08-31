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

import java.util.Collections;
import java.util.List;

import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.Field;
import org.stajistics.data.NullDataSet;
import org.stajistics.data.DataSet.StandardField;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.Tracker;

/**
 * A {@link StatsSession} implementation which does not respond to updates.
 *
 * @author The Stajistics Project
 */
public class ImmutableSession implements StatsSession {

    private final StatsKey key;
    private final DataSet dataSet;

    public ImmutableSession(final StatsKey key) {
        this(key, NullDataSet.getInstance());
    }

    public ImmutableSession(final StatsSession copyFrom) {
        this(copyFrom.getKey(),
             copyFrom.collectData());
    }

    public ImmutableSession(final StatsKey key,
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
    public double getDouble(Field field) {
        return dataSet.getDouble(field);
    }

    @Override
    public long getLong(Field field) {
        return dataSet.getLong(field);
    }

    @Override
    public Object getObject(Field field) {
        return dataSet.getObject(field);
    }

    @Override
    public Object getObject(String name) {
        return dataSet.getObject(name);
    }

    @Override
    public DataSet collectData() {
        return dataSet;
    }

    @Override
    public DataSet drainData() {
        return dataSet;
    }

    /**
     * Does nothing.
     */
    @Override
    public void restore(DataSet dataSet) {}

    @Override
    public double getFirst() {
        return dataSet.getDouble(StandardField.first);
    }

    @Override
    public long getFirstHitStamp() {
        return dataSet.getLong(StandardField.firstHitStamp);
    }

    @Override
    public long getHits() {
        return dataSet.getLong(StandardField.hits);
    }

    @Override
    public long getCommits() {
        return dataSet.getLong(StandardField.commits);
    }

    @Override
    public double getLast() {
        return dataSet.getDouble(StandardField.last);
    }

    @Override
    public long getLastHitStamp() {
        return dataSet.getLong(StandardField.lastHitStamp);
    }

    @Override
    public double getMax() {
        return dataSet.getDouble(StandardField.max);
    }

    @Override
    public double getMin() {
        return dataSet.getDouble(StandardField.min);
    }

    @Override
    public double getSum() {
        return dataSet.getDouble(StandardField.sum);
    }

    /**
     * Does nothing.
     */
    @Override
    public void track(Tracker tracker, long now) {}

    /**
     * Does nothing.
     */
    @Override
    public void update(Tracker tracker, long now) {}

    /**
     * Does nothing.
     */
    @Override
    public void clear() {}
}

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.DefaultDataSet;
import org.stajistics.event.StatsEventManager;
import org.stajistics.session.recorder.DataRecorder;

/**
 * 
 * @author The Stajistics Project
 */
public abstract class AbstractStatsSession implements StatsSession {

    protected static final DataRecorder[] EMPTY_DATA_RECORDER_ARRAY = new DataRecorder[0];

    protected static final DecimalFormat DECIMAL_FORMAT;
    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setDecimalSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.###", dfs);
        DECIMAL_FORMAT.setGroupingSize(Byte.MAX_VALUE);
    }

    protected final StatsKey key;
    protected final StatsEventManager eventManager;

    protected final DataRecorder[] dataRecorders;

    public AbstractStatsSession(final StatsKey key, 
                                final StatsEventManager eventManager, 
                                final DataRecorder... dataRecorders) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (eventManager == null) {
            throw new NullPointerException("eventManager");
        }

        this.key = key;
        this.eventManager = eventManager;

        if (dataRecorders == null) {
            this.dataRecorders = EMPTY_DATA_RECORDER_ARRAY;
        } else {
            // TBD: Copy array?
            this.dataRecorders = dataRecorders;
        }
    }

    @Override
    public StatsKey getKey() {
        return key;
    }

    @Override
    public List<DataRecorder> getDataRecorders() {
        return Collections.unmodifiableList(Arrays.asList(dataRecorders));
    }

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

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
import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.DefaultDataSet;
import org.stajistics.event.EventManager;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.util.FastPutsTableMap;
import org.stajistics.util.Misc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

/**
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsSession implements StatsSession {

    private static final Logger logger = LoggerFactory.getLogger(AbstractStatsSession.class);

    protected static final DataRecorder[] EMPTY_DATA_RECORDER_ARRAY = new DataRecorder[0];

    protected static final DecimalFormat DECIMAL_FORMAT;
    static {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dfs.setDecimalSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("0.###", dfs);
        DECIMAL_FORMAT.setGroupingSize(Byte.MAX_VALUE);
    }

    protected final StatsKey key;
    protected final EventManager eventManager;

    protected final DataRecorder[] dataRecorders;

    public AbstractStatsSession(final StatsKey key,
                                final EventManager eventManager,
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
            this.dataRecorders = dataRecorders;
        }
    }

    protected abstract void setHits(long hits);

    protected abstract void setFirstHitStamp(long firstHitStamp);

    protected abstract void setLastHitStamp(long lastHitStamp);

    protected abstract void setCommits(long commits);

    protected abstract void setFirst(Double first);

    protected abstract void setLast(double last);

    protected abstract void setMin(double min);

    protected abstract void setMax(double max);

    protected abstract void setSum(double sum);

    @Override
    public StatsKey getKey() {
        return key;
    }

    @Override
    public List<DataRecorder> getDataRecorders() {
        return Collections.unmodifiableList(Arrays.asList(dataRecorders));
    }

    @Override
    public Object getField(String name) {
        // Intern the name to allow fast reference equality checks
        name = name.intern();

        // Check basic fields

        if (name == DataSet.Field.HITS) {
            return getHits();
        }
        if (name == DataSet.Field.FIRST_HIT_STAMP) {
            return getFirstHitStamp();
        }
        if (name == DataSet.Field.LAST_HIT_STAMP) {
            return getLastHitStamp();
        }
        if (name == DataSet.Field.COMMITS) {
            return getCommits();
        }
        if (name == DataSet.Field.FIRST) {
            return getFirst();
        }
        if (name == DataSet.Field.LAST) {
            return getLast();
        }
        if (name == DataSet.Field.MIN) {
            return getMin();
        }
        if (name == DataSet.Field.MAX) {
            return getMax();
        }
        if (name == DataSet.Field.SUM) {
            return getSum();
        }

        // Check DataRecorder fields

        final int dataRecorderCount = dataRecorders.length;
        for (int i = 0; i < dataRecorderCount; i++) {
            try {
                if (dataRecorders[i].getSupportedFieldNames().contains(name)) {
                    Object result = dataRecorders[i].getField(this, name);
                    if (result != null) {
                        return result;
                    }
                }
            } catch (Exception e) {
                Misc.logHandledException(logger,
                        e,
                        "Failed to getField({}) from {}",
                        name,
                        dataRecorders[i]);
                Stats.getUncaughtExceptionHandler().uncaughtException(getKey(), e);
            }
        }

        // Not found
        return null;
    }

    /**
     * A factory method for creating a DataSet instance that will be populated with this session's data.
     * @param drainedSession
     * @return
     */
    protected DataSet createDataSet(final boolean drainedSession) {

        final int estimatedSize = 10 + (dataRecorders.length * 4);

        DataSet dataSet = new DefaultDataSet(System.currentTimeMillis(), 
                                             drainedSession,
                                             new FastPutsTableMap<String,Object>(estimatedSize)) {
            @Override
            protected Map<String, Object> createMetaDataMap() {
                return new FastPutsTableMap<String,Object>(8);
            }
        };

        return dataSet;
    }

    @Override
    public DataSet collectData() {
        final DataSet dataSet = createDataSet(false);
        collectData(dataSet);
        return dataSet;
    }

    protected void collectData(final DataSet dataSet) {
        dataSet.setField(DataSet.Field.HITS, getHits());
        dataSet.setField(DataSet.Field.FIRST_HIT_STAMP, getFirstHitStamp());
        dataSet.setField(DataSet.Field.LAST_HIT_STAMP, getLastHitStamp());
        dataSet.setField(DataSet.Field.COMMITS, getCommits());
        dataSet.setField(DataSet.Field.FIRST, getFirst());
        dataSet.setField(DataSet.Field.LAST, getLast());
        dataSet.setField(DataSet.Field.MIN, getMin());
        dataSet.setField(DataSet.Field.MAX, getMax());
        dataSet.setField(DataSet.Field.SUM, getSum());

        for (DataRecorder dataRecorder : dataRecorders) {
            try {
                dataRecorder.collectData(this, dataSet);
            } catch (Exception e) {
                Misc.logHandledException(logger,
                        e,
                        "Failed to collectData() from {}",
                        dataRecorder);
                Stats.getUncaughtExceptionHandler().uncaughtException(getKey(), e);
            }
        }
    }

    protected void restoreState(final DataSet dataSet) {
        if (dataSet == null) {
            throw new NullPointerException("dataSet");
        }

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

                setHits(restoredHits);
                setFirstHitStamp(restoredFirstHitStamp);
                setLastHitStamp(restoredLastHitStamp);

                Long restoredCommits = dataSet.getField(DataSet.Field.COMMITS,
                                                        DataSet.Field.Default.COMMITS);
                Double restoredFirst = dataSet.getField(DataSet.Field.FIRST, Double.class);
                Double restoredLast = dataSet.getField(DataSet.Field.LAST, Double.class);

                // Only restore "update()" data if commits, first, and last are defined
                if (restoredCommits > DataSet.Field.Default.COMMITS &&
                        restoredFirst != null &&
                        restoredLast != null) {

                    setCommits(restoredCommits);
                    setFirst(restoredFirst);
                    setLast(restoredLast);
                    setMin(dataSet.getField(DataSet.Field.MIN, Double.POSITIVE_INFINITY));
                    setMax(dataSet.getField(DataSet.Field.MAX, Double.NEGATIVE_INFINITY));
                    setSum(dataSet.getField(DataSet.Field.SUM, DataSet.Field.Default.SUM));

                    // Restore DataRecorders
                    for (DataRecorder dataRecorder : dataRecorders) {
                        try {
                            dataRecorder.restore(dataSet);
                        } catch (Exception e) {
                            Misc.logHandledException(logger,
                                    e,
                                    "Failed to restore {}",
                                    dataRecorder);
                            Stats.getUncaughtExceptionHandler().uncaughtException(getKey(), e);
                        }
                    }
                }
            }
        }
    }

    protected void clearState() {
        setHits(DataSet.Field.Default.HITS);
        setFirstHitStamp(DataSet.Field.Default.FIRST_HIT_STAMP);
        setLastHitStamp(DataSet.Field.Default.LAST_HIT_STAMP);
        setCommits(DataSet.Field.Default.COMMITS);
        setFirst(null); // The proper default is taken care of in getFirst()
        setLast(DataSet.Field.Default.LAST);
        setMin(Double.POSITIVE_INFINITY);
        setMax(Double.NEGATIVE_INFINITY);
        setSum(DataSet.Field.Default.SUM);

        for (DataRecorder dataRecorder : dataRecorders) {
            try {
                dataRecorder.clear();
            } catch (Exception e) {
                Misc.logHandledException(logger,
                        e,
                        "Failed to clear {}",
                        dataRecorder);
                Stats.getUncaughtExceptionHandler().uncaughtException(getKey(), e);
            }
        }
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
        buf.append(getFirstHitStamp());
        buf.append(",lastHitStamp=");
        buf.append(getLastHitStamp());
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

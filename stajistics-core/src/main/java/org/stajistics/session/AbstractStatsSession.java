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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.data.FieldSet;
import org.stajistics.data.FieldSetFactory;
import org.stajistics.data.DataSet.StandardField;
import org.stajistics.data.DataSet.StandardMetaField;
import org.stajistics.event.EventManager;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.util.Misc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

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
    protected final FieldSet fields;
    private final Map<Field,DataRecorder> fieldsToRecorders;
    
    public AbstractStatsSession(final StatsKey key,
                                final EventManager eventManager,
                                final FieldSetFactory fieldSetFactory,
                                final DataRecorder... dataRecorders) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (eventManager == null) {
            throw new NullPointerException("eventManager");
        }
        if (fieldSetFactory == null) {
            throw new NullPointerException("fieldSetFactory");
        }

        this.key = key;
        this.eventManager = eventManager;

        ImmutableList.Builder<Field> fieldBuilder = ImmutableList.builder();
        fieldBuilder.add(StandardField.values());
        fieldBuilder.add(StandardMetaField.values());
        if (dataRecorders == null) {
            this.dataRecorders = EMPTY_DATA_RECORDER_ARRAY;
            this.fieldsToRecorders = Collections.emptyMap();
        } else {
            ImmutableMap.Builder<Field,DataRecorder> fieldsToRecorderBuilder = ImmutableMap.builder();
            this.dataRecorders = dataRecorders;
            for (DataRecorder recorder : dataRecorders) {
                try {
                    List<? extends Field> recorderFields = recorder.getSupportedFields();
                    fieldBuilder.addAll(recorderFields);
                    for (Field recorderField : recorderFields) {
                        fieldsToRecorderBuilder.put(recorderField, recorder);
                    }
                } catch (Exception e) {
                    Misc.logSwallowedException(logger, e, "Could not get fields from recorder: {}", recorder);
                }
            }
            this.fieldsToRecorders = fieldsToRecorderBuilder.build();
        }
        this.fields = fieldSetFactory.newFieldSet(fieldBuilder.build());
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
    public Object getObject(Field field) {
        if (field instanceof StandardField) {
            switch (field.type()) {
            case DOUBLE:
                return getDoubleInternal((StandardField) field);
            case LONG:
                return getLongInternal((StandardField) field);
            default:
                throw new AssertionError();
            }
        } else {
            DataRecorder recorder = fieldsToRecorders.get(field);
            if (recorder == null) {
                throw new IllegalArgumentException("Field not found: " + field);
            }
            return recorder.getObject(this, field);
        }
    }
    
    @Override
    public Object getObject(String name) {
        return getObject(fields.getField(name));
    }
    
    @Override
    public long getLong(Field field) {
        if (field instanceof StandardField) {
            switch (field.type()) {
            case DOUBLE:
                return (long) getDoubleInternal((StandardField) field);
            case LONG:
                return getLongInternal((StandardField) field);
            default:
                throw new AssertionError();
            }
        } else {
            DataRecorder recorder = fieldsToRecorders.get(field);
            if (recorder == null) {
                throw new IllegalArgumentException("Field not found: " + field);
            }
            return recorder.getLong(this, field);
        }
    }

    @Override
    public double getDouble(Field field) {
        if (field instanceof StandardField) {
            switch (field.type()) {
            case DOUBLE:
                return getDoubleInternal((StandardField) field);
            case LONG:
                return getLongInternal((StandardField) field);
            default:
                throw new AssertionError();
            }
        } else {
            DataRecorder recorder = fieldsToRecorders.get(field);
            if (recorder == null) {
                throw new IllegalArgumentException("Field not found: " + field);
            }
            return recorder.getDouble(this, field);
        }
    }

    private long getLongInternal(StandardField field) {
        switch (field) {
        case hits:
            return getHits();
        case commits:
            return getCommits();
        case firstHitStamp:
            return getFirstHitStamp();
        case lastHitStamp:
            return getLastHitStamp();
        default:
            throw new AssertionError();
        }
    }
    
    private double getDoubleInternal(StandardField field) {
        switch (field) {
        case first:
            return getFirst();
        case last:
            return getLast();
        case min:
            return getMin();
        case max:
            return getMax();
        case sum:
            return getSum();
        default:
            throw new AssertionError();
        }
    }
    
    @Override
    public DataSet collectData() {
        final DataSetBuilder dataSetBuilder = fields.newDataSetBuilder();
        collectDataInternal(dataSetBuilder);
        return dataSetBuilder.build();
    }

    protected void collectDataInternal(final DataSetBuilder dataSetBuilder) {
        dataSetBuilder.set(StandardMetaField.collectionStamp,
                         System.currentTimeMillis());

        dataSetBuilder.set(StandardField.hits, getHits());
        dataSetBuilder.set(StandardField.firstHitStamp, getFirstHitStamp());
        dataSetBuilder.set(StandardField.lastHitStamp, getLastHitStamp());
        dataSetBuilder.set(StandardField.commits, getCommits());
        dataSetBuilder.set(StandardField.first, getFirst());
        dataSetBuilder.set(StandardField.last, getLast());
        dataSetBuilder.set(StandardField.min, getMin());
        dataSetBuilder.set(StandardField.max, getMax());
        dataSetBuilder.set(StandardField.sum, getSum());

        for (DataRecorder dataRecorder : dataRecorders) {
            try {
                dataRecorder.collectData(this, dataSetBuilder);
            } catch (Exception e) {
                Misc.logSwallowedException(logger,
                                           e,
                                           "Failed to collectData() from {}",
                                           dataRecorder);
            }
        }
    }

    protected void restoreState(final DataSet dataSet) {
        if (dataSet == null) {
            throw new NullPointerException("dataSet");
        }

        long restoredHits = dataSet.getLong(StandardField.hits);
        long restoredFirstHitStamp = dataSet.getLong(StandardField.firstHitStamp);
        long restoredLastHitStamp = dataSet.getLong(StandardField.lastHitStamp);

        // Only restore if hits, firstHitStamp, and lastHitStamp are defined
        if (restoredHits > 0L
                && restoredFirstHitStamp > -1L
                && restoredLastHitStamp > -1L) {

            setHits(restoredHits);
            setFirstHitStamp(restoredFirstHitStamp);
            setLastHitStamp(restoredLastHitStamp);

            long restoredCommits = dataSet.getLong(StandardField.commits);
            double restoredFirst = dataSet.getDouble(StandardField.first);
            double restoredLast = dataSet.getDouble(StandardField.last);

            // Only restore "update()" data if commits, first, and last are
            // defined
            if (restoredCommits > 0L
                    && !Double.isNaN(restoredFirst) 
                    && !Double.isNaN(restoredLast)) {

                setCommits(restoredCommits);
                setFirst(restoredFirst);
                setLast(restoredLast);
                setMin(dataSet.getDouble(StandardField.min));
                setMax(dataSet.getDouble(StandardField.max));
                setSum(dataSet.getDouble(StandardField.sum));

                // Restore DataRecorders
                for (DataRecorder dataRecorder : dataRecorders) {
                    try {
                        dataRecorder.restore(dataSet);
                    } catch (Exception e) {
                        Misc.logSwallowedException(logger, e,
                                "Failed to restore {}", dataRecorder);
                    }
                }
            }
        }
    }

    protected void clearState() {
        setHits(0L);
        setFirstHitStamp(-1L);
        setLastHitStamp(-1L);
        setCommits(0L);
        setFirst(null); // The proper default is taken care of in getFirst()
        setLast(Double.NaN);
        setMin(Double.POSITIVE_INFINITY);
        setMax(Double.NEGATIVE_INFINITY);
        setSum(0L);

        for (DataRecorder dataRecorder : dataRecorders) {
            try {
                dataRecorder.clear();
            } catch (Exception e) {
                Misc.logSwallowedException(logger,
                                           e,
                                           "Failed to clear {}",
                                           dataRecorder);
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

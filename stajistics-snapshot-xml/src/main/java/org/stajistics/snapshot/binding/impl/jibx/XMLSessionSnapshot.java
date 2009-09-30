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
package org.stajistics.snapshot.binding.impl.jibx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.stajistics.DefaultStatsKey;
import org.stajistics.StatsConfig;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.session.StatsSession;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.snapshot.binding.AbstractSessionSnapshot;
import org.stajistics.tracker.CompositeStatsTrackerFactory;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * 
 * @author The Stajistics Project
 */
public class XMLSessionSnapshot extends AbstractSessionSnapshot {

	private DefaultStatsKey key;

    private String trackerFactory;
    private String sessionFactory;
    private ArrayList<String> dataRecorders;
    private String unit;

    private DataSet dataSet;

    public XMLSessionSnapshot() {}

	public XMLSessionSnapshot(final StatsSession realSession,
	                          final StatsConfig config) {
	    setKey(realSession.getKey());

        trackerFactory = extractTrackerFactoryClassName(config.getTrackerFactory());
        sessionFactory = config.getSessionFactory().getClass().getName();

        List<DataRecorder> dataRecorders = realSession.getDataRecorders();
        this.dataRecorders = new ArrayList<String>(dataRecorders.size());
        for (DataRecorder dataRecorder : dataRecorders) {
            this.dataRecorders.add(dataRecorder.toString());
        }

        unit = config.getUnit();

        dataSet = realSession.collectData();
    }

    private String extractTrackerFactoryClassName(final StatsTrackerFactory factory) {
        if (factory instanceof CompositeStatsTrackerFactory) {
            Map<String,StatsTrackerFactory> factoryMap = ((CompositeStatsTrackerFactory)factory).getFactoryMap();

            StringBuilder buf = new StringBuilder(64 * factoryMap.size());
            for (Map.Entry<String,StatsTrackerFactory> entry : factoryMap.entrySet()) {
                buf.append(entry.getKey());
                buf.append(':');
                buf.append(entry.getValue().getClass().getName());
                buf.append(',');
            }
            buf.setLength(buf.length() - 1); // chop off trailing comma

            return buf.toString();
        }

        return factory.getClass().getName();
    }

    @Override
    public StatsKey getKey() {
        return key;
    }

    @Override
    public void setKey(final StatsKey key) {
        this.key = new DefaultStatsKey(key.getName(), null, key.getAttributes());
    }

    @Override
    public String getTrackerFactoryClassName() {
        return trackerFactory;
    }

    @Override
    public void setTrackerFactoryClassName(final String trackerFactoryClassName) {
        this.trackerFactory = trackerFactoryClassName;
    }

    @Override
    public String getSessionFactoryClassName() {
        return sessionFactory;
    }

    @Override
    public void setSessionFactoryClassName(final String sessionFactoryClassName) {
        this.sessionFactory = sessionFactoryClassName;
    }

    @Override
    public List<String> getDataRecorderClassNames() {
        return dataRecorders;
    }

    @Override
    public void setDataRecorderClassNames(final List<String> dataRecorderClassNames) {
        this.dataRecorders = new ArrayList<String>(dataRecorderClassNames);
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public void setUnit(final String unit) {
        this.unit = unit;
    }

    @Override
    public DataSet getDataSet() {
        return dataSet;
    }

    @Override
    public void setDataSet(final DataSet dataSet) {
        this.dataSet = dataSet;
    }

}

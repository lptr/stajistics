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
package org.stajistics.configuration;

import org.stajistics.StatsConstants;
import org.stajistics.StatsKey;
import org.stajistics.data.FieldSetFactory;
import org.stajistics.data.fast.FastFieldSetFactory;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.session.recorder.DataRecorderFactory;
import org.stajistics.session.recorder.DefaultDataRecorderFactory;
import org.stajistics.tracker.TrackerFactory;
import org.stajistics.tracker.span.TimeDurationTracker;

/**
 * The default implementation of a {@link StatsConfigBuilder}. Do not instantiate this class
 * directly. Instead use {@link org.stajistics.StatsManager#getConfigBuilderFactory()},
 * or {@link org.stajistics.Stats#buildConfig()}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigBuilder implements StatsConfigBuilder {

    protected final StatsConfigManager configManager;

    protected boolean enabled = true;
    protected TrackerFactory<?> trackerFactory;
    protected StatsSessionFactory sessionFactory;
    protected FieldSetFactory fieldSetFactory;
    protected DataRecorderFactory dataRecorderFactory;
    protected String unit;
    protected String description;

    /**
     * Create a new instance.
     *
     * @param configManager The {@link StatsConfigManager} to support
     *                      {@link #setConfigFor(org.stajistics.StatsKey)} calls. Must not be <tt>null</tt>.
     * @throws NullPointerException If <tt>configManager</tt> is <tt>null</tt>.
     */
    public DefaultStatsConfigBuilder(final StatsConfigManager configManager) {
        this(configManager, null);
    }

    /**
     * Create a new instance.
     *
     * @param configManager The {@link StatsConfigManager} to support
     *                      {@link #setConfigFor(org.stajistics.StatsKey)} calls. Must not be <tt>null</tt>.
     * @param config The {@link StatsConfig} from which to copy configuration as a starting point.
     *
     * @throws NullPointerException If <tt>configManager</tt> is <tt>null</tt>.
     */
    public DefaultStatsConfigBuilder(final StatsConfigManager configManager,
                                     final StatsConfig config) {
        if (configManager == null) {
            throw new NullPointerException("configManager");
        }

        this.configManager = configManager;

        if (config != null) {
            enabled = config.isEnabled();
            trackerFactory = config.getTrackerFactory();
            sessionFactory = config.getSessionFactory();
            fieldSetFactory = config.getFieldSetFactory();
            dataRecorderFactory = config.getDataRecorderFactory();
            unit = config.getUnit();
            description = config.getDescription();
        }
    }

    @Override
    public StatsConfigBuilder withEnabledState(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public StatsConfigBuilder withSessionFactory(final StatsSessionFactory sessionFactory) {
        if (sessionFactory == null) {
            throw new NullPointerException("sessionFactory");
        }

        this.sessionFactory = sessionFactory;
        return this;
    }

    @Override
    public StatsConfigBuilder withTrackerFactory(final TrackerFactory<?> trackerFactory) {
        if (trackerFactory == null) {
            throw new NullPointerException("trackerFactory");
        }

        this.trackerFactory = trackerFactory;
        return this;
    }

    @Override
    public StatsConfigBuilder withFieldSetFactory(final FieldSetFactory fieldSetFactory) {
        if (fieldSetFactory == null) {
            throw new NullPointerException("fieldSetFactory");
        }

        this.fieldSetFactory = fieldSetFactory;
        return this;
    }

    @Override
    public StatsConfigBuilder withDataRecorderFactory(final DataRecorderFactory dataRecorderFactory) {
        if (dataRecorderFactory == null) {
            throw new NullPointerException("dataRecorderFactory");
        }

        this.dataRecorderFactory = dataRecorderFactory;
        return this;
    }

    @Override
    public StatsConfigBuilder withUnit(final String unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }

        this.unit = unit;
        return this;
    }

    @Override
    public StatsConfigBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    /**
     * A factory method for getting the default {@link TrackerFactory}.
     *
     * @return The default {@link TrackerFactory}, never <tt>null</tt>.
     */
    protected TrackerFactory<?> createDefaultTrackerFactory() {
        return TimeDurationTracker.FACTORY;
    }

    /**
     * A factory method for getting the default {@link StatsSessionFactory}.
     *
     * @return The default {@link StatsSessionFactory}, never <tt>null</tt>.
     */
    protected StatsSessionFactory createDefaultSessionFactory() {
        return DefaultSessionFactory.getInstance();
    }

    protected FieldSetFactory createDefaultFieldSetFactory() {
        return FastFieldSetFactory.getInstance();
    }

    /**
     * A factory method for getting the default {@link DataRecorderFactory}.
     *
     * @return The default {@link DataRecorderFactory}, never <tt>null</tt>.
     */
    protected DataRecorderFactory createDefaultDataRecorderFactory() {
        return DefaultDataRecorderFactory.getInstance();
    }

    /**
     * A factory method for getting the default unit.
     *
     * @return The default unit, never <tt>null</tt>.
     */
    protected String createDefaultUnit() {
        return StatsConstants.DEFAULT_UNIT;
    }

    @Override
    public StatsConfig newConfig() {

        TrackerFactory<?> trackerFactory = this.trackerFactory;
        StatsSessionFactory sessionFactory = this.sessionFactory;
        DataRecorderFactory dataRecorderFactory = this.dataRecorderFactory;
        String unit = this.unit;

        if (trackerFactory == null) {
            trackerFactory = createDefaultTrackerFactory();
        }
        if (sessionFactory == null) {
            sessionFactory = createDefaultSessionFactory();
        }
        if (fieldSetFactory == null) {
            fieldSetFactory = createDefaultFieldSetFactory();
        }
        if (dataRecorderFactory == null) {
            dataRecorderFactory = createDefaultDataRecorderFactory();
        }
        if (unit == null) {
            unit = createDefaultUnit();
        }

        return new DefaultStatsConfig(enabled,
                                      trackerFactory,
                                      sessionFactory,
                                      fieldSetFactory,
                                      dataRecorderFactory,
                                      unit,
                                      this.description);
    }

    @Override
    public void setConfigFor(final StatsKey key) {
        configManager.setConfig(key, newConfig());
    }

}

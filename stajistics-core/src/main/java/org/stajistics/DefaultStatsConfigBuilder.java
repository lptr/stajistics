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
package org.stajistics;

import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.tracker.StatsTrackerFactory;
import org.stajistics.tracker.span.TimeDurationTracker;

/**
 * The default implementation of a {@link StatsConfigBuilder}. Do not instantiate this class
 * directly. Instead use {@link StatsManager#createConfigBuilder()}, or {@link Stats#buildConfig()}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigBuilder implements StatsConfigBuilder {

    private static final long serialVersionUID = 7525771505477519104L;

    protected final StatsConfigManager configManager;

    protected boolean enabled = true;
    protected StatsTrackerFactory trackerFactory;
    protected StatsSessionFactory sessionFactory;
    protected String unit;
    protected String description;

    /**
     * Create a new instance.
     *
     * @param configManager The {@link StatsConfigManager} to support 
     *                      {@link #setConfigFor(StatsKey)} calls. Must not be <tt>null</tt>.
     * @throws NullPointerException If <tt>configManager</tt> is <tt>null</tt>.
     */
    public DefaultStatsConfigBuilder(final StatsConfigManager configManager) {
        this(configManager, null);
    }

    /**
     * Create a new instance.
     *
     * @param configManager The {@link StatsConfigManager} to support 
     *                      {@link #setConfigFor(StatsKey)} calls. Must not be <tt>null</tt>.
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
            unit = config.getUnit();
            description = config.getDescription();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder withEnabledState(final boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder withSessionFactory(final StatsSessionFactory sessionFactory) {
        if (sessionFactory == null) {
            throw new NullPointerException("sessionFactory");
        }

        this.sessionFactory = sessionFactory;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder withTrackerFactory(final StatsTrackerFactory trackerFactory) {
        if (trackerFactory == null) {
            throw new NullPointerException("trackerFactory");
        }

        this.trackerFactory = trackerFactory;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder withUnit(final String unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }

        this.unit = unit;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    /**
     * A factory method for getting the default {@link StatsTrackerFactory}.
     *
     * @return The default {@link StatsTrackerFactory}, never <tt>null</tt>.
     */
    protected StatsTrackerFactory createDefaultTrackerFactory() {
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

    /**
     * A factory method for getting the default unit.
     *
     * @return The default unit, never <tt>null</tt>.
     */
    protected String createDefaultUnit() {
        return StatsConstants.DEFAULT_UNIT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfig newConfig() {

        StatsTrackerFactory trackerFactory = this.trackerFactory;
        StatsSessionFactory sessionFactory = this.sessionFactory;
        String unit = this.unit;

        if (trackerFactory == null) {
            trackerFactory = createDefaultTrackerFactory();
        }
        if (sessionFactory == null) {
            sessionFactory = createDefaultSessionFactory();
        }
        if (unit == null) {
            unit = createDefaultUnit();
        }

        return new DefaultStatsConfig(enabled,
                                      trackerFactory,
                                      sessionFactory,
                                      unit,
                                      this.description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfigFor(final StatsKey key) {
        configManager.setConfig(key, newConfig());
    }

}

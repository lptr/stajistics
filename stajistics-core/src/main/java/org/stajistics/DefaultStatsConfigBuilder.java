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
import org.stajistics.tracker.TimeDurationTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigBuilder implements StatsConfigBuilder {

    protected StatsKey key;

    protected StatsTrackerFactory trackerFactory;
    protected StatsSessionFactory sessionFactory;
    protected String unit;
    protected String description;

    public DefaultStatsConfigBuilder(final StatsKey key) {
        StatsConfig config = Stats.getConfigManager()
                                  .getConfig(key);
        if (config != null) {
            trackerFactory = config.getTrackerFactory();
            sessionFactory = config.getSessionFactory();
            unit = config.getUnit();
            description = config.getDescription();
        }
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
    public StatsConfigBuilder withTrackerFactory(final StatsTrackerFactory trackerFactory) {
        if (trackerFactory == null) {
            throw new NullPointerException("trackerFactory");
        }

        this.trackerFactory = trackerFactory;
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

    protected StatsTrackerFactory createDefaultTrackerFactory() {
        return TimeDurationTracker.FACTORY;
    }

    protected StatsSessionFactory createDefaultSessionFactory() {
        return DefaultSessionFactory.getInstance();
    }

    protected String createDefaultUnit() {
        return StatsConstants.DEFAULT_UNIT;
    }

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

        return new DefaultStatsConfig(trackerFactory,
                                      sessionFactory,
                                      unit,
                                      this.description);
    }

    @Override
    public void set() {
        StatsConfig config = newConfig();
        Stats.getConfigManager().setConfig(key, config);
    }

}

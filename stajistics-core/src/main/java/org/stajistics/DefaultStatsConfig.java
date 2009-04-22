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

import org.stajistics.session.StatsSessionFactory;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * The default implementation of {@link StatsConfig}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfig implements StatsConfig {

    protected boolean enabled = true;

    protected StatsTrackerFactory trackerFactory;
    protected StatsSessionFactory sessionFactory;

    protected String unit;
    protected String description;

    public DefaultStatsConfig(final StatsConfig template) {
        this(template.getTrackerFactory(),
             template.getSessionFactory(),
             template.getUnit(),
             template.getDescription());
    }

    public DefaultStatsConfig(final StatsTrackerFactory trackerFactory,
                              final StatsSessionFactory sessionFactory,
                              final String unit,
                              final String description) {

        if (trackerFactory == null) {
            throw new NullPointerException("trackerFactory");
        }
        if (sessionFactory == null) {
            throw new NullPointerException("sessionFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (unit.length() == 0) {
            throw new IllegalArgumentException("empty unit");
        }

        this.trackerFactory = trackerFactory;
        this.sessionFactory = sessionFactory;
        this.unit = unit;
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUnit() {
        return unit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsTrackerFactory getTrackerFactory() {
        return trackerFactory;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof StatsConfig) && equals((StatsConfig)obj);
    }

    public boolean equals(final StatsConfig other) {
        if (other == null) {
            return false;
        }

        if (!trackerFactory.equals(other.getTrackerFactory())) {
            return false;
        }
        if (!sessionFactory.equals(other.getSessionFactory())) {
            return false;
        }
        if (!unit.equals(other.getUnit())) {
            return false;
        }
        if (!Util.equalsNullAware(description, other.getDescription())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return trackerFactory.hashCode() ^
               sessionFactory.hashCode() ^
               unit.hashCode() ^
               ((description == null) ? 0 : description.hashCode());
    }
}

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
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfig implements StatsConfig {

    protected boolean enabled = true;

    protected Class<? extends StatsTracker> trackerClass;
    protected StatsSessionFactory sessionFactory;

    protected String unit;
    protected String description;

    public DefaultStatsConfig(final StatsConfig template) {
        this(template.getTrackerClass(),
             template.getSessionFactory(),
             template.getUnit(),
             template.getDescription());
    }

    public DefaultStatsConfig(final Class<? extends StatsTracker> trackerClass,
                              final StatsSessionFactory sessionFactory,
                              final String unit,
                              final String description) {

        if (trackerClass == null) {
            throw new NullPointerException("trackerClass");
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

        this.trackerClass = trackerClass;
        this.sessionFactory = sessionFactory;
        this.unit = unit;
        this.description = description;
    }

    public static StatsConfig createDefaultConfig() {
        return createDefaultConfig(null, null, null, null);
    }

    public static StatsConfig createDefaultConfig(String unit,
                                                  Class<? extends StatsTracker> trackerClass,
                                                  StatsSessionFactory sessionFactory,
                                                  String description) {
        if (unit == null) {
            unit = Constants.DEFAULT_UNIT;
        }
        if (trackerClass == null) {
            trackerClass = Constants.DEFAULT_TRACKER_CLASS;
        }
        if (sessionFactory == null) {
            sessionFactory = DefaultSessionFactory.getInstance();
        }

        return new DefaultStatsConfig(trackerClass,
                                      sessionFactory,
                                      unit,
                                      description);
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public StatsSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public Class<? extends StatsTracker> getTrackerClass() {
        return trackerClass;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof StatsConfig) && equals((StatsConfig)obj);
    }

    public boolean equals(final StatsConfig other) {
        if (other == null) {
            return false;
        }

        if (!trackerClass.equals(other.getTrackerClass())) {
            return false;
        }
        if (!sessionFactory.equals(other.getSessionFactory())) {
            return false;
        }
        if (!unit.equals(other.getUnit())) {
            return false;
        }

        // Description is not included in equality

        return true;
    }

    @Override
    public int hashCode() {
        return trackerClass.hashCode() ^
               sessionFactory.hashCode() ^
               unit.hashCode();
    }
}

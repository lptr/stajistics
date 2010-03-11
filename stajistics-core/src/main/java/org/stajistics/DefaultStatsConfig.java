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
package org.stajistics;

import org.stajistics.session.StatsSessionFactory;
import org.stajistics.session.recorder.DataRecorderFactory;
import org.stajistics.tracker.TrackerFactory;

/**
 * The default implementation of {@link StatsConfig}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfig implements StatsConfig {

    private final boolean enabled;

    private final TrackerFactory<?> trackerFactory;
    private final StatsSessionFactory sessionFactory;
    private final DataRecorderFactory dataRecorderFactory;

    private final String unit;
    private final String description;

    /**
     * Construct a new instance supplying template configuration to copy.
     *
     * @param template The {@link StatsConfig} from which to copy all configuration values.
     */
    public DefaultStatsConfig(final StatsConfig template) {
        this(template.isEnabled(),
             template.getTrackerFactory(),
             template.getSessionFactory(),
             template.getDataRecorderFactory(),
             template.getUnit(),
             template.getDescription());
    }

    /**
     * Construct a new instance supplying the configuration values.
     *
     * @param enabled The return value of {@link #isEnabled()}.
     * @param trackerFactory The return value of {@link #getTrackerFactory()}.
     * @param sessionFactory The return value of {@link #getSessionFactory()}.
     * @param dataRecorderFactory The return value of {@link #getDataRecorderFactory()}.
     * @param unit The return value of {@link #getUnit()}.
     * @param description The return value of {@link #getDescription()}. May be <tt>null</tt>.
     *
     * @throws NullPointerException If <tt>enabled</tt>, <tt>trackerFactory</tt>,
     *         <tt>sessionFactory</tt>, <tt>dataRecorderFactory</tt>, or <tt>unit</tt> are <tt>null</tt>.
     * @throws IllegalArgumentException If <tt>unit</tt> is zero length.
     */
    public DefaultStatsConfig(final boolean enabled,
                              final TrackerFactory<?> trackerFactory,
                              final StatsSessionFactory sessionFactory,
                              final DataRecorderFactory dataRecorderFactory,
                              final String unit,
                              final String description) {

        if (trackerFactory == null) {
            throw new NullPointerException("trackerFactory");
        }
        if (sessionFactory == null) {
            throw new NullPointerException("sessionFactory");
        }
        if (dataRecorderFactory == null) {
            throw new NullPointerException("dataRecorderFactory");
        }
        if (unit == null) {
            throw new NullPointerException("unit");
        }
        if (unit.length() == 0) {
            throw new IllegalArgumentException("empty unit");
        }

        this.enabled = enabled;
        this.trackerFactory = trackerFactory;
        this.sessionFactory = sessionFactory;
        this.dataRecorderFactory = dataRecorderFactory;
        this.unit = unit;
        this.description = description;
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
    public TrackerFactory<?> getTrackerFactory() {
        return trackerFactory;
    }

    @Override
    public DataRecorderFactory getDataRecorderFactory() {
        return dataRecorderFactory;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof StatsConfig) && equals((StatsConfig)obj);
    }

    public boolean equals(final StatsConfig other) {
        if (other == null) {
            return false;
        }

        if (enabled != other.isEnabled()) {
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
        return Boolean.valueOf(enabled).hashCode() +
               31 * trackerFactory.hashCode() +
               31 * sessionFactory.hashCode() +
               31 * unit.hashCode() +
               ((description == null) ? 0 : 31 * description.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(StatsConfig.class.getSimpleName());
        buf.append("[enabled=");
        buf.append(enabled);
        buf.append(",trackerFactory=");
        buf.append(trackerFactory);
        buf.append(",sessionFactory=");
        buf.append(sessionFactory);
        buf.append(",unit=");
        buf.append(unit);
        buf.append(",description=");
        buf.append(description);
        buf.append(']');

        return buf.toString();
    }
}

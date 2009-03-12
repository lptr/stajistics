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

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfig implements StatsConfig {

    protected volatile boolean enabled = true;

    protected String unit;
    protected Class<? extends StatsTracker> trackerClass;
    protected Class<? extends StatsSession> sessionClass;

    public DefaultStatsConfig(final String unit,
                              final Class<? extends StatsTracker> trackerClass,
                              final Class<? extends StatsSession> sessionClass) {

        if (trackerClass == null) {
            throw new NullPointerException("trackerClass");
        }
        if (sessionClass == null) {
            throw new NullPointerException("sessionClass");
        }

        setUnit(unit);
        this.trackerClass = trackerClass;
        this.sessionClass = sessionClass;
    }

    public static StatsConfig createDefaultConfig() {
        return new DefaultStatsConfig(Constants.DEFAULT_UNIT,
                                      Constants.DEFAULT_TRACKER_CLASS,
                                      Constants.DEFAULT_SESSION_CLASS);
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public void setUnit(final String unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }

        if (unit.length() == 0) {
            throw new IllegalArgumentException("empty unit");
        }

        this.unit = unit;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Class<? extends StatsSession> getSessionClass() {
        return sessionClass;
    }

    @Override
    public Class<? extends StatsTracker> getTrackerClass() {
        return trackerClass;
    }

}

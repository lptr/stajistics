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
public class DefaultStatsConfigBuilder extends DefaultStatsKeyBuilder implements StatsConfigBuilder {

    protected String unit;
    protected Class<? extends StatsTracker> trackerClass;
    protected Class<? extends StatsSession> sessionClass;

    public DefaultStatsConfigBuilder(final String name) {
        super(name);
    }

    public DefaultStatsConfigBuilder(final StatsKey key) {
        super(key.getName());

        //TODO
    }

    @Override
    public StatsConfigBuilder withAttribute(String name, Object value) {
        return (StatsConfigBuilder)super.withAttribute(name, value);
    }

    @Override
    public StatsConfigBuilder withSession(final Class<? extends StatsSession> sessionClass) {
        if (sessionClass == null) {
            throw new NullPointerException("sessionClass");
        }

        this.sessionClass = sessionClass;
        return this;
    }

    @Override
    public StatsConfigBuilder withTracker(final Class<? extends StatsTracker> trackerClass) {
        if (trackerClass == null) {
            throw new NullPointerException("trackerClass");
        }

        this.trackerClass = trackerClass;
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
    public StatsKey newKey() {

        StatsKey key = super.newKey();

        String unit = this.unit;
        if (unit == null) {
            unit = Constants.DEFAULT_UNIT;
        }

        Class<? extends StatsTracker> trackerClass = this.trackerClass;
        if (trackerClass == null) {
            trackerClass = Constants.DEFAULT_TRACKER_CLASS;
        }

        Class<? extends StatsSession> sessionClass = this.sessionClass;
        if (sessionClass == null) {
            sessionClass = Constants.DEFAULT_SESSION_CLASS;
        }

        // Create StatsConfig

        StatsConfigManager configManager = Stats.getInstance().getConfigManager();
        StatsConfig config = configManager.getConfig(key); 
        if (config == null) {
            config = new DefaultStatsConfig(unit, trackerClass, sessionClass);
            StatsConfig existingConfig = configManager.putConfigIfAbsent(key, config);
            if (existingConfig != null) {
                config = existingConfig;
            }
        }

        return key;
    }
}

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
package org.stajistics.management;

import java.util.Map;

import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyMatcher;
import org.stajistics.StatsManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigMBean implements StatsConfigMBean {

    protected final StatsManager statsManager;
    protected final StatsKey key;
    protected final StatsConfig config;

    public DefaultStatsConfigMBean(final StatsManager statsManager,
                                   final StatsKey key,
                                   final StatsConfig config) {
        if (statsManager == null) {
            throw new NullPointerException("statsManager");
        }
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.statsManager = statsManager;
        this.key = key;
        this.config = config;
    }

    @Override
    public boolean getEnabled() {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        if (enabled == config.isEnabled()) {
            return;
        }

        statsManager.getConfigBuilderFactory()
                    .createConfigBuilder(config)
                    .withEnabledState(enabled)
                    .setConfigFor(key);
    }

    @Override
    public String getUnit() {
        return config.getUnit();
    }

    @Override
    public void setUnit(final String unit) {
        if (unit.equals(config.getUnit())) {
            return;
        }

        statsManager.getConfigBuilderFactory()
                    .createConfigBuilder(config)
                    .withUnit(unit)
                    .setConfigFor(key);
    }

    @Override
    public String getDescription() {
        return config.getDescription();
    }

    @Override
    public void setDescription(final String description) {
        if (description.equals(config.getDescription())) {
            return;
        }

        statsManager.getConfigBuilderFactory()
                    .createConfigBuilder(config)
                    .withDescription(description)
                    .setConfigFor(key);
    }

    @Override
    public String getTrackerFactory() {
        return config.getTrackerFactory().toString();
    }

    @Override
    public String getSessionFactory() {
        return config.getSessionFactory().toString();
    }

    @Override
    public void enableTree(final boolean enabled) {

        setEnabled(enabled);

        StatsConfigBuilderFactory configBuilderFactory = statsManager.getConfigBuilderFactory();

        Map<StatsKey,StatsConfig> childMap =
            statsManager.getConfigManager()
                        .getConfigs(StatsKeyMatcher.descendentOf(key.getName()));

        for (Map.Entry<StatsKey,StatsConfig> entry : childMap.entrySet()) {
            configBuilderFactory.createConfigBuilder(entry.getValue())
                         .withEnabledState(enabled)
                         .setConfigFor(entry.getKey());
        }
    }
}

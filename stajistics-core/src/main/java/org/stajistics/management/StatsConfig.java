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
package org.stajistics.management;

import java.io.IOException;

import org.stajistics.Stats;
import org.stajistics.StatsKey;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsConfig implements StatsConfigMBean {

    private final StatsKey key;
    private final org.stajistics.StatsConfig config;

    public StatsConfig(final StatsKey key, 
                       final org.stajistics.StatsConfig config) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.key = key;
        this.config = config;
    }

    @Override
    public boolean getEnabled() throws IOException {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) throws IOException {
        if (enabled == config.isEnabled()) {
            return;
        }

        Stats.getManager()
             .getConfigFactory()
             .createConfigBuilder(config)
             .withEnabledState(enabled)
             .setConfigFor(key);
    }

    @Override
    public String getUnit() throws IOException {
        return config.getUnit();
    }

    @Override
    public void setUnit(final String unit) throws IOException {
        if (unit.equals(config.getUnit())) {
            return;
        }

        Stats.getManager()
             .getConfigFactory()
             .createConfigBuilder(config)
             .withUnit(unit)
             .setConfigFor(key);
    }

    @Override
    public String getDescription() throws IOException {
        return config.getDescription();
    }

    @Override
    public void setDescription(final String description) throws IOException {
        if (description.equals(config.getDescription())) {
            return;
        }

        Stats.getManager()
             .getConfigFactory()
             .createConfigBuilder(config)
             .withDescription(description)
             .setConfigFor(key);
    }

    @Override
    public String getTrackerFactory() throws IOException {
        return config.getTrackerFactory().toString();
    }

    @Override
    public String getSessionFactory() throws IOException {
        return config.getSessionFactory().toString();
    }

}

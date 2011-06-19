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
package org.stajistics.management.beans;

import org.stajistics.configuration.StatsConfigManager;

import java.util.Set;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManagerMXBean implements StatsConfigManagerMXBean {

    private final StatsConfigManager configManager;

    public DefaultStatsConfigManagerMXBean(final StatsConfigManager configManager) {
        if (configManager == null) {
            throw new NullPointerException("configManager");
        }

        this.configManager = configManager;
    }

    @Override
    public String getImplementation() {
        return configManager.getClass().getName();
    }

    @Override
    public int getConfigCount() {
        return configManager.getConfigCount();
    }

    @Override
    public Set<String> keyNames() {
        return configManager.getKeyNames();
    }

}

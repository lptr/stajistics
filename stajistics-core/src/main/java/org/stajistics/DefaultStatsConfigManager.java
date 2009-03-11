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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.stajistics.management.StatsManagement;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManager implements StatsConfigManager {

    private ConcurrentMap<String,StatsConfig> configMap =
        new ConcurrentHashMap<String,StatsConfig>();

    @Override
    public StatsConfig getConfig(final StatsKey key) {
        return configMap.get(key.getName());
    }

    @Override
    public StatsConfig putConfigIfAbsent(StatsKey key, StatsConfig config) {
        StatsConfig existingConfig = configMap.putIfAbsent(key.getName(), config);

        if (existingConfig == null) {
            // Not a replacement, so register MBean
            // TODO: what about replacement?
            StatsManagement.getInstance().registerConfigMBean(key, config);
        }

        return existingConfig;
    }
    
}

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

import static org.stajistics.Util.assertNotNull;

import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventHandler;
import org.stajistics.event.EventType;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.task.TaskService;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class StatsManagementEventHandler implements EventHandler {

    private final StatsMXBeanRegistrar mxBeanRegistrar;

    public StatsManagementEventHandler(final StatsMXBeanRegistrar mxBeanRegistrar) {
        assertNotNull(mxBeanRegistrar, "mxBeanRegistrar");
        this.mxBeanRegistrar = mxBeanRegistrar;
    }

    @Override
    public void handleStatsEvent(final EventType eventType,
                                 final StatsKey key,
                                 final Object target) {
        // Check for speed-sensitive events first
        switch (eventType) {
            case SESSION_CREATED:
                mxBeanRegistrar.registerSessionMXBean((StatsSession)target);
                return;
    
            case SESSION_DESTROYED:
                mxBeanRegistrar.unregisterSessionMXBeanIfNecessary(key);
                return;
    
            case CONFIG_CREATED:
                mxBeanRegistrar.registerConfigMXBean(key, (StatsConfig)target);
                return;
                
            case CONFIG_CHANGED:
                mxBeanRegistrar.unregisterConfigMXBeanIfNecessary(key);
                mxBeanRegistrar.registerConfigMXBean(key, (StatsConfig)target);
                return;
    
            case CONFIG_DESTROYED:
                mxBeanRegistrar.unregisterConfigMXBeanIfNecessary(key);
                return;
        }

        // Check for rarely occurring events second
        switch (eventType) {
            case STATS_MANAGER_INITIALIZED:
                mxBeanRegistrar.registerStatsManagerMXBean((StatsManager)target);
                return;

            case STATS_MANAGER_SHUTTING_DOWN:
                mxBeanRegistrar.unregisterStatsManagerMXBean();
                return;

            case CONFIG_MANAGER_INITIALIZED:
                mxBeanRegistrar.registerConfigManagerMXBean((StatsConfigManager)target);
                return;

            case CONFIG_MANAGER_SHUTTING_DOWN:
                mxBeanRegistrar.unregisterConfigManagerMXBean();
                return;

            case SESSION_MANAGER_INITIALIZED:
                mxBeanRegistrar.registerSessionManagerMXBean((StatsSessionManager)target);
                return;

            case SESSION_MANAGER_SHUTTING_DOWN:
                mxBeanRegistrar.unregisterSessionManagerMXBean();
                return;
        }
    }
}

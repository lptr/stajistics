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
package org.stajistics.bootstrap;

import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsManager;
import org.stajistics.StatsProperties;
import org.stajistics.management.DefaultStatsManagement;
import org.stajistics.management.StatsManagement;
import org.stajistics.management.StatsManagementEventHandler;

/**
 * @author The Stajistics Project
 */
public class DefaultStatsManagerFactory implements StatsManagerFactory {

    private static final String PROP_STAJISTICS_ENABLED = StatsManager.class.getName() + ".enabled";
    private static final String PROP_MANAGEMENT_ENABLED = StatsManagement.class.getName() + ".enabled";

    @Override
    public DefaultStatsManager createManager() {
        boolean stajisticsEnabled = StatsProperties.getBooleanProperty(PROP_STAJISTICS_ENABLED, true);
        DefaultStatsManager manager = new DefaultStatsManager.Builder()
                                                             .withEnabled(stajisticsEnabled)
                                                             .newManager();

        if (StatsProperties.getBooleanProperty(PROP_MANAGEMENT_ENABLED, true)) {
            StatsManagement management = new DefaultStatsManagement();
            management.registerManagerMBean(manager);
            management.registerConfigManagerMBean(manager);
            management.registerSessionManagerMBean(manager);

            StatsManagementEventHandler eventHandler = new StatsManagementEventHandler(manager, management);
            manager.getEventManager()
                   .addGlobalEventHandler(eventHandler);
        }

        return manager;
    }
}

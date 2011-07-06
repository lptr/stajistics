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

import static org.stajistics.Util.assertNotNull;

import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsManager;
import org.stajistics.StatsProperties;
import org.stajistics.management.DefaultStatsMXBeanRegistrar;
import org.stajistics.management.StatsMXBeanRegistrar;
import org.stajistics.management.StatsManagementEventHandler;
import org.stajistics.task.TaskService;
import org.stajistics.task.TaskServiceFactory;
import org.stajistics.task.ThreadPoolTaskService;

/**
 * @author The Stajistics Project
 */
public class DefaultStatsManagerFactory implements StatsManagerFactory {

    private static final String PROP_STAJISTICS_ENABLED = StatsManager.class.getName() + ".enabled";
    private static final String PROP_MANAGEMENT_ENABLED = StatsMXBeanRegistrar.class.getName() + ".enabled";

    @Override
    public StatsManager createManager(final String namespace) {
        assertNotNull(namespace, "namespace");

        boolean stajisticsEnabled = StatsProperties.getBooleanProperty(PROP_STAJISTICS_ENABLED, true);
        DefaultStatsManager manager = new DefaultStatsManager.Builder()
                                                             .withNamespace(namespace)
                                                             .withEnabled(stajisticsEnabled)
                                                             .newManager();

        final boolean managementEnabled = StatsProperties.getBooleanProperty(PROP_MANAGEMENT_ENABLED, true);

        // Configure MBean management
        if (managementEnabled) {
            StatsMXBeanRegistrar mxBeanRegistrar = new DefaultStatsMXBeanRegistrar(namespace);
            StatsManagementEventHandler managementEventHandler = new StatsManagementEventHandler(mxBeanRegistrar);
            manager.getEventManager()
                   .addGlobalEventHandler(managementEventHandler);
        }

        // Initialize TaskService if not already initialized
        TaskServiceFactory taskServiceFactory = TaskServiceFactory.getInstance();
        if (!taskServiceFactory.isTaskServiceLoaded()) {
            TaskService taskService = new ThreadPoolTaskService(); // TODO: make configurable
            taskService.initialize();

            if (managementEnabled) {
                DefaultStatsMXBeanRegistrar.registerTaskServiceMXBean(taskService);
            }

            taskServiceFactory.loadTaskService(taskService);
        }

        manager.initialize();

        return manager;
    }
}

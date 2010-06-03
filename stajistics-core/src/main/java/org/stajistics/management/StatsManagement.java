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

import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.session.StatsSession;

import javax.management.MBeanServer;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public interface StatsManagement {

    MBeanServer getMBeanServer();

    void registerConfigManagerMBean(StatsManager statsManager);

    void unregisterConfigManagerMBean(StatsManager statsManager);

    void registerConfigMBean(StatsManager statsManager,
                             StatsKey key,
                             StatsConfig config);

    void unregisterConfigMBeanIfNecessary(StatsManager statsManager,
                               StatsKey key);

    void registerSessionManagerMBean(StatsManager statsManager);

    void unregisterSessionManagerMBean(StatsManager statsManager);

    void registerSessionMBean(StatsManager statsManager,
                              StatsSession session);

    void unregisterSessionMBeanIfNecessary(StatsManager statsManager,
                                StatsKey key);
}

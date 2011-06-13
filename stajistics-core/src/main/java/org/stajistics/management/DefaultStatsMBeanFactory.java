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

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsMBeanFactory implements StatsMBeanFactory {

    @Override
    public StatsManagerMBean createManagerMBean(StatsManager statsManager) {
        return new DefaultStatsManagerMBean(statsManager);
    }

    @Override
    public StatsConfigManagerMBean createConfigManagerMBean(final StatsManager statsManager) {
        return new DefaultStatsConfigManagerMBean(statsManager.getConfigManager());
    }

    @Override
    public StatsConfigMBean createConfigMBean(final StatsManager statsManager,
                                              final StatsKey key,
                                              final StatsConfig config) {
        return new DefaultStatsConfigMBean(statsManager,
                                           key,
                                           config);
    }

    @Override
    public StatsSessionMBean createSessionMBean(final StatsManager statsManager,
                                                final StatsSession session) {
        return new DefaultStatsSessionMBean(statsManager.getSessionManager(), session);
    }

    @Override
    public StatsSessionManagerMBean createSessionManagerMBean(final StatsManager statsManager) {
        return new DefaultStatsSessionManagerMBean(statsManager.getSessionManager());
    }
}

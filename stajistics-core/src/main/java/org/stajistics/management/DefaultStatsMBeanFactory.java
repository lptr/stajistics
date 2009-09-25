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

import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigFactory;
import org.stajistics.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.snapshot.StatsSnapshotManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsMBeanFactory implements StatsMBeanFactory {

    private static final long serialVersionUID = 4934468666587889695L;

    @Override
    public StatsConfigManagerMBean createConfigManagerMBean(final StatsConfigManager configManager) {
        return new DefaultStatsConfigManagerMBean(configManager);
    }

    @Override
    public StatsConfigMBean createConfigMBean(final StatsConfigFactory configFactory,
                                              final StatsKey key, 
                                              final StatsConfig config) {
        return new DefaultStatsConfigMBean(configFactory, key, config);
    }

    @Override
    public StatsSessionMBean createSessionMBean(final StatsSessionManager sessionManager,
                                                final StatsSession session) {
        return new DefaultStatsSessionMBean(sessionManager, session);
    }

    @Override
    public StatsSessionManagerMBean createSessionManagerMBean(final StatsSessionManager sessionManager) {
        return new DefaultStatsSessionManagerMBean(sessionManager);
    }

    @Override
    public StatsSnapshotMBean createSnapshotMBean(final StatsSnapshotManager snapshotManager) {
        return new DefaultStatsSnapshotMBean(snapshotManager);
    }
}

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

import org.stajistics.StatsConfig;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.event.StatsEventHandler;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagementEventHandler implements StatsEventHandler {

    private final StatsManager statsManager;
    private final StatsManagement statsManagement;

    public StatsManagementEventHandler(final StatsManager statsManager,
                                       final StatsManagement statsManagement) {
        if (statsManager == null) {
            throw new NullPointerException("statsManager");
        }
        if (statsManagement == null) {
            throw new NullPointerException("statsManagement");
        }

        this.statsManager = statsManager;
        this.statsManagement = statsManagement;
    }

    @Override
    public void handleStatsEvent(final StatsEventType eventType, 
                                 final StatsKey key, 
                                 final Object target) {
        if (eventType == StatsEventType.SESSION_CREATED) {
            statsManagement.registerSessionMBean(statsManager, (StatsSession)target);

        } else if (eventType == StatsEventType.SESSION_DESTROYED) {
            statsManagement.unregisterSessionMBean(statsManager, key);

        } else if (eventType == StatsEventType.CONFIG_CREATED) {
            statsManagement.registerConfigMBean(statsManager, key, (StatsConfig)target);

        } else if (eventType == StatsEventType.CONFIG_DESTROYED) {
            statsManagement.unregisterConfigMBean(statsManager, key);

        } else if (eventType == StatsEventType.CONFIG_CHANGED) {
            statsManagement.unregisterConfigMBean(statsManager, key);
            statsManagement.registerConfigMBean(statsManager, key, (StatsConfig)target);
        }
    }
}

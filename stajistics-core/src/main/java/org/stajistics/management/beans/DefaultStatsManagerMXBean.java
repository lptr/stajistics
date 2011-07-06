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

import static org.stajistics.Util.assertNotNull;

import org.stajistics.StatsManager;

/**
 * @author The Stajistics Project
 */
public class DefaultStatsManagerMXBean implements StatsManagerMXBean {

    private final StatsManager statsManager;
    
    public DefaultStatsManagerMXBean(final StatsManager statsManager) {
        assertNotNull(statsManager, "statsManager");
        this.statsManager = statsManager;
    }

    @Override
    public String getNamespace() {
        return statsManager.getNamespace();
    }

    @Override
    public String getSessionManagerImpl() {
        return statsManager.getSessionManager().getClass().getName();
    }

    @Override
    public String getConfigManagerImpl() {
        return statsManager.getConfigManager().getClass().getName();
    }

    @Override
    public String getEventManagerImpl() {
        return statsManager.getEventManager().getClass().getName();
    }

    @Override
    public String getTaskServiceImpl() {
        return statsManager.getTaskService().getClass().getName();
    }

    @Override
    public boolean getEnabled() {
        return statsManager.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        statsManager.setEnabled(enabled);
    }

    @Override
    public boolean getRunning() {
        return statsManager.isRunning();
    }

    @Override
    public void shutdown() {
        statsManager.shutdown();
    }
}

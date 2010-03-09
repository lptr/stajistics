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
package org.stajistics;

import java.io.Serializable;

import org.stajistics.event.StatsEventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.task.TaskService;
import org.stajistics.tracker.StatsTrackerLocator;

/**
 * Acts as an aggregator of other managers and factories. Maintains a master enabled switch for
 * statistics collection.
 *
 * @author The Stajistics Project
 */
public interface StatsManager extends Serializable {

    /**
     * Get the {@link StatsConfigManager}.
     *
     * @return The {@link StatsConfigManager}, never <tt>null</tt>.
     */
    StatsConfigManager getConfigManager();

    /**
     * Get the {@link StatsSessionManager}.
     *
     * @return The @link StatsSessionManager}, never <tt>null</tt>.
     */
    StatsSessionManager getSessionManager();

    /**
     * Get the {@link StatsEventManager}.
     *
     * @return The {@link StatsEventManager}, never <tt>null</tt>.
     */
    StatsEventManager getEventManager();

    /**
     * Get the {@link StatsTrackerLocator}.
     *
     * @return The {@link StatsTrackerLocator}, never <tt>null</tt>.
     */
    StatsTrackerLocator getTrackerLocator();

    /**
     * Get the {@link StatsKeyFactory}.
     *
     * @return The {@link StatsKeyFactory}, never <tt>null</tt>.
     */
    StatsKeyFactory getKeyFactory();

    /**
     * Get the {@link StatsConfigFactory}.
     *
     * @return The {@link StatsConfigFactory}, never <tt>null</tt>.
     */
    StatsConfigFactory getConfigFactory();

    /**
     * Get the {@link TaskService}.
     *
     * @return The {@link TaskService}, never <tt>null</tt>.
     */
    TaskService getTaskService();

    /**
     * Determine if statistics collection is enabled.
     *
     * @return <tt>true</tt> if statistics collection is enabled, <tt>false</tt> otherwise.
     */
    boolean isEnabled();

    /**
     * Enabled or disable statistics collection.
     *
     * @param enabled <tt>true</tt> to enable statistics collection, <tt>false</tt> to disable.
     */
    void setEnabled(boolean enabled);

    /**
     * Firstly, set the manager state to disabled, then proceed to clean up any resources
     * associated with statistics collection. A future call to {@link #setEnabled(boolean)} passing
     * <tt>true</tt>, followed by any request to collect statistics will reinitialize any
     * necessary resources.
     */
    void shutdown();

}

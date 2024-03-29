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

import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.util.ServiceLifeCycle;

/**
 * Acts as an aggregator of other managers and factories. Maintains a master enabled switch for
 * statistics collection.
 *
 * @author The Stajistics Project
 */
public interface StatsManager extends Serializable,ServiceLifeCycle {

    /**
     * Get the unique namespace identifier for this <tt>StatsManager</tt>.
     *
     * @return The namespace, never <tt>null</tt>.
     */
    String getNamespace();
    
    /**
     * Get the {@link org.stajistics.configuration.StatsConfigManager}.
     *
     * @return The {@link org.stajistics.configuration.StatsConfigManager}, never <tt>null</tt>.
     */
    StatsConfigManager getConfigManager();

    /**
     * Get the {@link StatsSessionManager}.
     *
     * @return The @link StatsSessionManager}, never <tt>null</tt>.
     */
    StatsSessionManager getSessionManager();

    /**
     * Get the {@link EventManager}.
     *
     * @return The {@link EventManager}, never <tt>null</tt>.
     */
    EventManager getEventManager();

    /**
     * Get the {@link StatsKeyFactory}.
     *
     * @return The {@link StatsKeyFactory}, never <tt>null</tt>.
     */
    StatsKeyFactory getKeyFactory();

    /**
     * Get the {@link org.stajistics.configuration.StatsConfigBuilderFactory}.
     *
     * @return The {@link org.stajistics.configuration.StatsConfigBuilderFactory}, never <tt>null</tt>.
     */
    StatsConfigBuilderFactory getConfigBuilderFactory();

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
     * Initialize sub-components and fire initialization events. Clients do not need to call this method.
     * Called internally as part of instance creation by a {@link org.stajistics.bootstrap.StatsManagerFactory}.
     * After this method is called for the first time, subsequent calls will have no effect.
     */
    @Override
    void initialize();

    /**
     * Firstly, set the manager state to disabled, then proceed to clean up any resources
     * associated with statistics collection. A future call to {@link #setEnabled(boolean)} passing
     * <tt>true</tt>, followed by any request to collect statistics will reinitialize any
     * necessary resources.
     */
    @Override
    void shutdown();

    UncaughtExceptionHandler getUncaughtExceptionHandler();

    void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler);
}

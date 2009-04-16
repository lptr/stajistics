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
package org.stajistics;

import org.stajistics.event.StatsEventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.CompositeStatsTracker;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface StatsManager {

    /**
     * Get the {@link StatsConfigManager}.
     *
     * @return The {@link StatsConfigManager}. Never <tt>null</tt>.
     */
    StatsConfigManager getConfigManager();

    /**
     * Get the {@link StatsSessionManager}.
     *
     * @return The @link StatsSessionManager}. Never <tt>null</tt>.
     */
    StatsSessionManager getSessionManager();

    /**
     * Get the {@link StatsEventManager}.
     *
     * @return The {@link StatsEventManager}. Never <tt>null</tt>.
     */
    StatsEventManager getEventManager();

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
     * Obtain a {@link StatsTracker} for the given <tt>key</tt> that can be
     * used to collect statistics. If statistics collection is disabled, 
     * a safe no-op {@link NullTracker} instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link StatsTracker}. Never <tt>null</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     */
    StatsTracker getTracker(StatsKey key);

    /**
     * Obtain a {@link StatsTracker} for the given set of <tt>keys</tt> that can be used to
     * collect statistics. A {@link StatsTracker} is obtained for each key in <tt>keys</tt>
     * and all are wrapped in a {@link CompositeStatsTracker} instance.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link StatsTracker}. Never <tt>null</tt>.
     * @see CompositeStatsTracker
     */
    StatsTracker getTracker(StatsKey... keys);

    /**
     * Create a new {@link StatsKey} from the given <tt>name</tt>.
     *
     * @param name The name of the key to create.
     * @return A new {@link StatsKey}. Never <tt>null</tt>. 
     */
    StatsKey createKey(String name);

    /**
     * Create a new {@link StatsKeyBuilder} which can create a new {@link StatsKey}
     * for the given <tt>name</tt>.
     *
     * @param name The name of the key that the builder will create.
     * @return A {@link StatsKeyBuilder} which can be used to define key attributes. Never <tt>null</tt>.
     */
    StatsKeyBuilder createKeyBuilder(String name);

    /**
     * Create a new {@link StatsKeyBuilder} which can create a new {@link StatsKey}.
     * The builder is initialized with the name and attributes of the given {@link StatsKey}
     * <tt>template</tt>.
     *
     * @param template The key with which to initialize the {@link StatsKeyBuilder}.
     * @return A {@link StatsKeyBuilder} which can be used to define key attributes. Never <tt>null</tt>.
     */
    StatsKeyBuilder createKeyBuilder(StatsKey template);

    /**
     * Build a new {@link StatsConfig} for the given <tt>key</tt> using a {@link StatsConfigBuilder}.
     *
     * @param key The key for which to build configuration.
     * @return A {@link StatsKeyBuilder} which can be used to specify configuration. Never <tt>null</tt>.
     */
    StatsConfigBuilder createConfigBuilder(StatsKey key);
   
}

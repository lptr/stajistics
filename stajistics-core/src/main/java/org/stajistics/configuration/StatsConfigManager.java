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
package org.stajistics.configuration;

import org.stajistics.StatsKey;
import org.stajistics.StatsKeyMatcher;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>Manages associations of {@link org.stajistics.StatsKey}s to {@link StatsConfig}s. {@link StatsConfig}s are
 * organized into a hierarchy according to their associated {@link org.stajistics.StatsKey}.</p>
 *
 * <p>Configurations can only be associated with super-keys (keys without attributes),
 * or effectively key names. Any interaction with this class using sub-keys (keys with attributes),
 * gracefully resorts to using the equivalent super-key. In other words, when using a {@link org.stajistics.StatsKey}
 * with any method of this manager, the key attributes are ignored.</p>
 *
 * <p>A StatsConfigManager has a root {@link StatsConfig} which takes effect when a {@link StatsConfig}
 * has not been defined for a key.</p>
 *
 * TODO: elaborate on the semantics of the configuration heirarchy.. adding, removing, updating, etc.
 *
 * @see org.stajistics.StatsKey
 * @see StatsConfig
 *
 * @author The Stajistics Project
 */
public interface StatsConfigManager extends Serializable {

    /**
     * Get the root configuration.
     *
     * @return The root configuration.
     */
    StatsConfig getRootConfig();

    /**
     * Set the root configuration.
     *
     * @param config The new root configuration, or <tt>null</tt> to set
     *        the default root configuration.
     */
    void setRootConfig(StatsConfig config);

    /**
     * Obtain the key names for which a configuration is defined.
     *
     * @return All key names known to this manager.
     */
    Set<String> getKeyNames();

    /**
     * Get the number of configurations defined.
     *
     * @return The number of configurations known to this manager.
     */
    int getConfigCount();

    /**
     * Assign a configuration to the given key name.
     *
     * @param keyName The key name for which to assign a configuration. Must not be <tt>null</tt>.
     * @param config The configuration to assign to the given key, or <tt>null</tt> to inherit
     *               the configuration of the parent key.
     */
    void setConfig(String keyName, StatsConfig config);

    /**
     * Assign a configuration to the given key.
     *
     * @param key The key for which to assign a configuration. Must not be <tt>null</tt>.
     * @param config The configuration to assign to the given key, or <tt>null</tt> to inherit
     *               the configuration of the parent key.
     */
    void setConfig(StatsKey key, StatsConfig config);

    /**
     * Obtain the configuration for the given <tt>key</tt>.
     *
     * @param key The key for which to obtain the associated configuration.
     * @return The configuration or <tt>null</tt> if none has been defined.
     */
    StatsConfig getConfig(StatsKey key);

    /**
     * Obtain the configuration for the given <tt>key</tt>, or if it does not already
     * exist, create it by inheriting from the parent key configuration.
     *
     * @param key The key for which to obtain or create the associated configuration.
     * @return The configuration, never <tt>null</tt>.
     */
    StatsConfig getOrCreateConfig(StatsKey key);

    /**
     * Delete and return the configuration associated with the given key name. Does nothing
     * if the configuration does not exist for the key name.
     *
     * @param keyName The key name for which to delete configuration.
     * @return The deleted configuration, or <tt>null</tt> if none existed.
     */
    StatsConfig removeConfig(String keyName);

    /**
     * Delete and return the configuration associated with the given <tt>key</tt>. Does nothing
     * if the configuration does not exsit for the <tt>key</tt>.
     *
     * @param key The key for which to delete configuration.
     * @return The deleted configuration, or <tt>null</tt> if none existed.
     */
    StatsConfig removeConfig(StatsKey key);

    /**
     * Obtain a mapping of keys to configurations known to this manager. Does not include the root
     * configuration because it does not have an associated key.
     *
     * @return A Map of configurations, never <tt>null</tt>.
     */
    Map<StatsKey,StatsConfig> getConfigs();

    /**
     * Obtain a mapping of keys to configurations for the any known keys matched by <tt>matcher</tt>
     * Does not include the root configuration because it does not have an associated key.
     *
     * @param matcher The matcher with which to select certain keys to include in the returned mapping.
     * @return A Map of matching configurations, never <tt>null</tt>.
     */
    Map<StatsKey,StatsConfig> getConfigs(StatsKeyMatcher matcher);

    /**
     * Delete all known configurations except for the root configuration.
     */
    void clearConfigs();
    
    
    void shutdown();
}

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

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Manages associations of {@link StatsKey}s to {@link StatsConfig}s as well as configuration
 * hierarchy.
 *
 * A StatsConfigManager has a root {@link StatsConfig} which takes effect when a {@link StatsConfig}
 * has not been defined for a key.
 *
 * TODO: elaborate on the semantics of the configuration heirarchy.. adding, removing, updating, etc.
 *
 * @author The Stajistics Project
 */
public interface StatsConfigManager extends Serializable {

    StatsConfig getRootConfig();

    void setRootConfig(StatsConfig config);

    Set<String> getKeyNames();

    int getConfigCount();

    void setConfig(String keyName, StatsConfig config);

    void setConfig(StatsKey key, StatsConfig config);

    StatsConfig getConfig(StatsKey key);

    StatsConfig getOrCreateConfig(StatsKey key);

    StatsConfig removeConfig(String keyName);

    StatsConfig removeConfig(StatsKey key);

    Map<StatsKey,StatsConfig> getConfigs();

    Map<StatsKey,StatsConfig> getConfigs(StatsKeyMatcher matcher);

    void clearConfigs();
}

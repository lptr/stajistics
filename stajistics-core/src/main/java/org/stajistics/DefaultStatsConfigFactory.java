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

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigFactory implements StatsConfigFactory {

    private static final long serialVersionUID = 512664734977970885L;

    protected final StatsConfigManager configManager;

    public DefaultStatsConfigFactory(final StatsConfigManager configManager) {
        if (configManager == null) {
            throw new NullPointerException("configManager");
        }

        this.configManager = configManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder createConfigBuilder() {
        return new DefaultStatsConfigBuilder(configManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigBuilder createConfigBuilder(final StatsConfig template) {
        return new DefaultStatsConfigBuilder(configManager, template);
    }
}

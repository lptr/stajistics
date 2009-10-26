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
 * A factory for creating {@link StatsConfigBuilder}s.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigFactory implements StatsConfigFactory {

    private static final long serialVersionUID = 512664734977970885L;

    protected final StatsConfigManager configManager;

    /**
     * Construct a new instance.
     *
     * @param configManager The {@link StatsConfigManager} to pass into {@link StatsConfigBuilder}
     *                      instance created by this factory. Must not be <tt>null</tt>.
     * @throws NullPointerException If <tt>configManager</tt> is <tt>null</tt>.
     */
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

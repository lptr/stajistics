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

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.configuration.StatsConfig;

/**
 *
 * @author The Stajistics Project
 */
public class Issue32Test extends AbstractStajisticsTestCase {

    @Test
    public void testIndividualConfigAttributesAreNotInheritedFromParentConfigs() {
        StatsManager statsManager = new DefaultStatsManagerFactory().createManager(StatsConstants.DEFAULT_NAMESPACE);
        StatsFactory factory = new DefaultStatsFactory(statsManager);
        
        StatsConfig defaultRootConfig = statsManager.getConfigManager()
                                                    .getRootConfig();
        StatsConfig newRootConfig = statsManager.getConfigBuilderFactory()
                                                .createConfigBuilder(defaultRootConfig)
                                                .withDescription("[description inherited from root]")
                                                .newConfig();
        statsManager.getConfigManager()
                    .setRootConfig(newRootConfig);

        StatsKey key = factory.newKey("test");

        factory.buildConfig()
               .withUnit("[unit defined in child]")
               .setConfigFor(key);

        // Assert that individual attributes are not inherited from parent configs
        assertNull(statsManager.getConfigManager()
                               .getConfig(key)
                               .getDescription());
    }
}

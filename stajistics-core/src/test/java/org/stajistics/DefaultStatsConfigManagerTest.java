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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.tracker.TimeDurationTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManagerTest {

    private DefaultStatsConfigManager configManager;

    @Before
    public void setUp() {
        Stats.getEventManager().setEnabled(false);
        configManager = new DefaultStatsConfigManager();
    }

    @After
    public void tearDown() {
        Stats.getEventManager().setEnabled(true);
    }

    private StatsConfig createConfig() {
        return new DefaultStatsConfig(TimeDurationTracker.FACTORY, 
                                      DefaultSessionFactory.getInstance(), 
                                      "test", 
                                      null);
    }

    @Test
    public void testGetRootConfigNotNull() {
        assertNotNull(configManager.getRootConfig());
    }

    @Test
    public void testSetAndGetRootConfig() {
        StatsConfig config = createConfig();
        configManager.setRootConfig(config);
        assertSame(config, configManager.getRootConfig());
    }

    @Test
    public void testSetConfigNull() {
        StatsKey key = new SimpleStatsKey("test");

        StatsConfig rootConfig = configManager.getRootConfig();

        configManager.setConfig(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));

        key = new SimpleStatsKey("test.child");
        configManager.setConfig(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));

        key = new SimpleStatsKey("test.child.grandchild");
        configManager.setConfig(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));
    }

    @Test
    public void testSetConfigAscending() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        // Level 1
        StatsConfig config1 = createConfig();
        assertSame(rootConfig, configManager.getConfig(key1));
        configManager.setConfig(key1, config1);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config1, configManager.getConfig(key1));

        // Level 2
        StatsConfig config2 = createConfig();
        assertSame(config1, configManager.getConfig(key2));
        configManager.setConfig(key2, config2);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config1, configManager.getConfig(key1));
        assertSame(config2, configManager.getConfig(key2));

        // Level 3
        StatsConfig config3 = createConfig();
        assertSame(config2, configManager.getConfig(key3));
        configManager.setConfig(key3, config3);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config1, configManager.getConfig(key1));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(config3, configManager.getConfig(key3));

        // Level 4
        StatsConfig config4 = createConfig();
        assertSame(config3, configManager.getConfig(key4));
        configManager.setConfig(key4, config4);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config1, configManager.getConfig(key1));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(config3, configManager.getConfig(key3));
        assertSame(config4, configManager.getConfig(key4));
    }

    @Test
    public void testSetConfigDescending() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        // Level 4
        StatsConfig config4 = createConfig();
        configManager.setConfig(key4, config4);
        assertSame(config4, configManager.getConfig(key4));
        assertSame(rootConfig, configManager.getConfig(key3));
        assertSame(rootConfig, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(key3, config3);
        assertSame(config4, configManager.getConfig(key4));
        assertSame(config3, configManager.getConfig(key3));
        assertSame(rootConfig, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(key2, config2);
        assertSame(config4, configManager.getConfig(key4));
        assertSame(config3, configManager.getConfig(key3));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(key1, config1);
        assertSame(config4, configManager.getConfig(key4));
        assertSame(config3, configManager.getConfig(key3));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(config1, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetRootConfigAscending() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key3));
        assertSame(rootConfig, configManager.getConfig(key4));
    }

    @Test
    public void testGetRootConfigDescending() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        assertSame(rootConfig, configManager.getConfig(key4));
        assertSame(rootConfig, configManager.getConfig(key3));
        assertSame(rootConfig, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
    }

    @Test
    public void testGetLevel1SetLevel2GetLevel3SetLevel4() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        // Get level 1
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(key2, config2);
        assertSame(config2, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 3
        assertSame(config2, configManager.getConfig(key3));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 4
        StatsConfig config4 = createConfig();
        configManager.setConfig(key4, config4);
        assertSame(config4, configManager.getConfig(key4));
        assertSame(config2, configManager.getConfig(key3));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testSetLevel1GetLevel2SetLevel3GetLevel4() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        // Set level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(key1, config1);
        assertSame(config1, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 2
        assertSame(config1, configManager.getConfig(key2));
        assertSame(config1, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(key3, config3);
        assertSame(config3, configManager.getConfig(key3));
        assertSame(config1, configManager.getConfig(key2));
        assertSame(config1, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 4
        assertSame(config3, configManager.getConfig(key4));
        assertSame(config3, configManager.getConfig(key3));
        assertSame(config1, configManager.getConfig(key2));
        assertSame(config1, configManager.getConfig(key1));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetLevel4SetLevel3GetLevel2SetLevel1() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        // Get level 4
        assertSame(rootConfig, configManager.getConfig(key4));

        // Set level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(key3, config3);
        assertSame(config3, configManager.getConfig(key4));
        assertSame(config3, configManager.getConfig(key3));

        // Get level 2
        assertSame(rootConfig, configManager.getConfig(key2));
        assertSame(config3, configManager.getConfig(key4));
        assertSame(config3, configManager.getConfig(key3));

        // Set level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(key1, config1);
        assertSame(config1, configManager.getConfig(key1));
        assertSame(config1, configManager.getConfig(key2));
        assertSame(config3, configManager.getConfig(key3));
        assertSame(config3, configManager.getConfig(key4));
    }

    @Test
    public void testSetLevel4GetLevel3SetLevel2GetLevel1() {
        StatsKey key1 = new SimpleStatsKey("test");
        StatsKey key2 = new SimpleStatsKey("test.child");
        StatsKey key3 = new SimpleStatsKey("test.child.grandchild");
        StatsKey key4 = new SimpleStatsKey("test.child.grandchild.greatgrandchild");

        StatsConfig rootConfig = configManager.getRootConfig();

        // Set level 4
        StatsConfig config4 = createConfig();
        configManager.setConfig(key4, config4);
        assertSame(config4, configManager.getConfig(key4));

        // Get level 3
        assertSame(rootConfig, configManager.getConfig(key3));
        assertSame(config4, configManager.getConfig(key4));

        // Set level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(key2, config2);
        assertSame(config2, configManager.getConfig(key2));
        assertSame(config2, configManager.getConfig(key3));
        assertSame(config4, configManager.getConfig(key4));

        // Get level 1
        assertSame(rootConfig, configManager.getConfig(key1));
        assertSame(config2, configManager.getConfig(key2));
        assertSame(config2, configManager.getConfig(key3));
        assertSame(config4, configManager.getConfig(key4));
    }

    //TODO: testRemoveConfigAscending()
    //TODO: testRemoveConfigDescending()

}

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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

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

    private StatsKey[] createKeyHierarchy() {
        return new StatsKey[] {
            new SimpleStatsKey("test"),
            new SimpleStatsKey("test.child"),
            new SimpleStatsKey("test.child.grandchild"),
            new SimpleStatsKey("test.child.grandchild.greatgrandchild")
        };
    }

    @Test
    public void testConstructWithConfigs() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = createConfig();
        StatsConfig config0 = createConfig();
        StatsConfig config1 = createConfig();
        StatsConfig config2 = createConfig();
        StatsConfig config3 = createConfig();

        Map<String,StatsConfig> configMap = new HashMap<String,StatsConfig>();

        configMap.put(keys[0].getName(), config0);
        configMap.put(keys[1].getName(), config1);
        configMap.put(keys[2].getName(), config2);
        configMap.put(keys[3].getName(), config3);

        configManager = new DefaultStatsConfigManager(rootConfig, configMap);

        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));
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
    public void testSetRootConfigWithNull() {
        configManager.setRootConfig(null);
        assertNotNull(configManager.getRootConfig());
    }

    @Test
    public void testSetConfigWithNullKey() {
        try {
            configManager.setConfig(null, createConfig());
            fail("Allowed setConfig with null key");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testSetConfigWithNullConfig() {
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
        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Level 0
        StatsConfig config0 = createConfig();
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        configManager.setConfig(keys[0], config0);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));

        // Level 1
        StatsConfig config1 = createConfig();
        assertSame(config0, configManager.getConfig(keys[1]));
        configManager.setConfig(keys[1], config1);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));

        // Level 2
        StatsConfig config2 = createConfig();
        assertSame(config1, configManager.getConfig(keys[2]));
        configManager.setConfig(keys[2], config2);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));

        // Level 3
        StatsConfig config3 = createConfig();
        assertSame(config2, configManager.getConfig(keys[3]));
        configManager.setConfig(keys[3], config3);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));
    }

    @Test
    public void testSetConfigDescending() {
        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(keys[3], config3);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(rootConfig, configManager.getConfig(keys[2]));
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(keys[2], config2);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(keys[1], config1);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 0
        StatsConfig config0 = createConfig();
        configManager.setConfig(keys[0], config0);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetRootConfigAscending() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[2]));
        assertSame(rootConfig, configManager.getConfig(keys[3]));
    }

    @Test
    public void testGetRootConfigDescending() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        assertSame(rootConfig, configManager.getConfig(keys[3]));
        assertSame(rootConfig, configManager.getConfig(keys[2]));
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
    }

    @Test
    public void testGetLevel1SetLevel2GetLevel3SetLevel4() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Get level 0
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(keys[1], config1);
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 2
        assertSame(config1, configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(keys[3], config3);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config1, configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testSetLevel1GetLevel2SetLevel3GetLevel4() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Set level 0
        StatsConfig config0 = createConfig();
        configManager.setConfig(keys[0], config0);
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 1
        assertSame(config0, configManager.getConfig(keys[1]));
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(keys[2], config2);
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config0, configManager.getConfig(keys[1]));
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 3
        assertSame(config2, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config0, configManager.getConfig(keys[1]));
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetLevel4SetLevel3GetLevel2SetLevel1() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Get level 3
        assertSame(rootConfig, configManager.getConfig(keys[3]));

        // Set level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(keys[2], config2);
        assertSame(config2, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));

        // Get level 1
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));

        // Set level 0
        StatsConfig config0 = createConfig();
        configManager.setConfig(keys[0], config0);
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config0, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config2, configManager.getConfig(keys[3]));
    }

    @Test
    public void testSetLevel4GetLevel3SetLevel2GetLevel1() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Set level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(keys[3], config3);
        assertSame(config3, configManager.getConfig(keys[3]));

        // Get level 2
        assertSame(rootConfig, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));

        // Set level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(keys[1], config1);
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config1, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));

        // Get level 0
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config1, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));
    }

    @Test
    public void testHasConfig() {

        StatsKey[] keys = createKeyHierarchy();

        configManager.setConfig(keys[0], createConfig());
        assertTrue(configManager.hasConfig(keys[0]));

        configManager.setConfig(keys[1], createConfig());
        assertTrue(configManager.hasConfig(keys[1]));

        configManager.setConfig(keys[2], createConfig());
        assertTrue(configManager.hasConfig(keys[2]));

        configManager.setConfig(keys[3], createConfig());
        assertTrue(configManager.hasConfig(keys[3]));
    }

    @Test
    public void testRemoveConfigAscending() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig config0 = createConfig();
        StatsConfig config1 = createConfig();
        StatsConfig config2 = createConfig();
        StatsConfig config3 = createConfig();

        configManager.setConfig(keys[0], config0);
        configManager.setConfig(keys[1], config1);
        configManager.setConfig(keys[2], config2);
        configManager.setConfig(keys[3], config3);

        assertSame(config0, configManager.removeConfig(keys[0]));
        assertFalse(configManager.hasConfig(keys[0]));
        assertFalse(configManager.hasConfig(keys[1]));
        assertFalse(configManager.hasConfig(keys[2]));
        assertFalse(configManager.hasConfig(keys[3]));
        assertNull(configManager.removeConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[1]));
        assertNull(configManager.removeConfig(keys[2]));
        assertNull(configManager.removeConfig(keys[3]));
    }

    @Test
    public void testRemoveConfigDescending() {

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig config0 = createConfig();
        StatsConfig config1 = createConfig();
        StatsConfig config2 = createConfig();
        StatsConfig config3 = createConfig();

        configManager.setConfig(keys[0], config0);
        configManager.setConfig(keys[1], config1);
        configManager.setConfig(keys[2], config2);
        configManager.setConfig(keys[3], config3);

        assertSame(config3, configManager.removeConfig(keys[3]));
        assertFalse(configManager.hasConfig(keys[3]));
        assertTrue(configManager.hasConfig(keys[2]));
        assertTrue(configManager.hasConfig(keys[1]));
        assertTrue(configManager.hasConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[3]));

        assertSame(config2, configManager.removeConfig(keys[2]));
        assertFalse(configManager.hasConfig(keys[3]));
        assertFalse(configManager.hasConfig(keys[2]));
        assertTrue(configManager.hasConfig(keys[1]));
        assertTrue(configManager.hasConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[2]));

        assertSame(config1, configManager.removeConfig(keys[1]));
        assertFalse(configManager.hasConfig(keys[3]));
        assertFalse(configManager.hasConfig(keys[2]));
        assertFalse(configManager.hasConfig(keys[1]));
        assertTrue(configManager.hasConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[1]));

        assertSame(config0, configManager.removeConfig(keys[0]));
        assertFalse(configManager.hasConfig(keys[3]));
        assertFalse(configManager.hasConfig(keys[2]));
        assertFalse(configManager.hasConfig(keys[1]));
        assertFalse(configManager.hasConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[0]));
    }

    @Test
    public void testClearConfigs() {
        StatsKey[] keys = createKeyHierarchy();

        StatsConfig config0 = createConfig();
        StatsConfig config1 = createConfig();
        StatsConfig config2 = createConfig();
        StatsConfig config3 = createConfig();

        configManager.setConfig(keys[0], config0);
        configManager.setConfig(keys[1], config1);
        configManager.setConfig(keys[2], config2);
        configManager.setConfig(keys[3], config3);

        configManager.clearConfigs();

        assertFalse(configManager.hasConfig(keys[3]));
        assertFalse(configManager.hasConfig(keys[2]));
        assertFalse(configManager.hasConfig(keys[1]));
        assertFalse(configManager.hasConfig(keys[0]));
    }

}

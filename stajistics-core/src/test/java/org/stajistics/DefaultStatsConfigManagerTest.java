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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.event.StatsEventManager;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.tracker.TimeDurationTracker;

/**
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManagerTest {

    // TODO: set up mock expectations for event firing

    private Mockery mockery;
    private StatsEventManager eventManager;

    private DefaultStatsConfigManager configManager;

    @Before
    public void setUp() {
        mockery = new Mockery();
        eventManager = mockery.mock(StatsEventManager.class);

        configManager = new DefaultStatsConfigManager(eventManager);
    }

    private StatsConfig createConfig() {
        return new DefaultStatsConfig(true,
                                      TimeDurationTracker.FACTORY, 
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

        final StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = createConfig();
        final StatsConfig config0 = createConfig();
        final StatsConfig config1 = createConfig();
        final StatsConfig config2 = createConfig();
        final StatsConfig config3 = createConfig();

        Map<String,StatsConfig> configMap = new HashMap<String,StatsConfig>();

        configMap.put(keys[0].getName(), config0);
        configMap.put(keys[1].getName(), config1);
        configMap.put(keys[2].getName(), config2);
        configMap.put(keys[3].getName(), config3);

        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        configManager = new DefaultStatsConfigManager(eventManager, rootConfig, configMap);

        mockery.assertIsSatisfied();

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
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

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
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

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
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Level 0
        StatsConfig config0 = createConfig();
        assertNull(configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        configManager.setConfig(keys[0], config0);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));

        // Level 1
        StatsConfig config1 = createConfig();
        assertNull(configManager.getConfig(keys[1]));
        assertSame(config0, configManager.getOrCreateConfig(keys[1]));
        configManager.setConfig(keys[1], config1);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));

        // Level 2
        StatsConfig config2 = createConfig();
        assertNull(configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getOrCreateConfig(keys[2]));
        configManager.setConfig(keys[2], config2);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));

        // Level 3
        StatsConfig config3 = createConfig();
        assertNull(configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getOrCreateConfig(keys[3]));
        configManager.setConfig(keys[3], config3);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));
    }

    @Test
    public void testSetConfigDescending() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

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
    public void testGetOrCreateRootConfigAscending() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[2]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[3]));
    }

    @Test
    public void testGetOrCreateRootConfigDescending() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        assertSame(rootConfig, configManager.getOrCreateConfig(keys[3]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[2]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
    }

    @Test
    public void testGetOrCreate_GetLevel1_SetLevel2_GetLevel3_SetLevel4() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Get level 0
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(keys[1], config1);
        assertSame(config1, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 2
        assertSame(config1, configManager.getOrCreateConfig(keys[2]));
        assertSame(config1, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(keys[3], config3);
        assertSame(config3, configManager.getOrCreateConfig(keys[3]));
        assertSame(config1, configManager.getOrCreateConfig(keys[2]));
        assertSame(config1, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetOrCreate_SetLevel1_GetLevel2_SetLevel3_GetLevel4() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Set level 0
        StatsConfig config0 = createConfig();
        configManager.setConfig(keys[0], config0);
        assertSame(config0, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 1
        assertSame(config0, configManager.getOrCreateConfig(keys[1]));
        assertSame(config0, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Set level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(keys[2], config2);
        assertSame(config2, configManager.getOrCreateConfig(keys[2]));
        assertSame(config0, configManager.getOrCreateConfig(keys[1]));
        assertSame(config0, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Get level 3
        assertSame(config2, configManager.getOrCreateConfig(keys[3]));
        assertSame(config2, configManager.getOrCreateConfig(keys[2]));
        assertSame(config0, configManager.getOrCreateConfig(keys[1]));
        assertSame(config0, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetOrCreate_GetLevel4_SetLevel3_GetLevel2_SetLevel1() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Get level 3
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[3]));

        // Set level 2
        StatsConfig config2 = createConfig();
        configManager.setConfig(keys[2], config2);
        assertSame(config2, configManager.getOrCreateConfig(keys[3]));
        assertSame(config2, configManager.getOrCreateConfig(keys[2]));

        // Get level 1
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[1]));
        assertSame(config2, configManager.getOrCreateConfig(keys[3]));
        assertSame(config2, configManager.getOrCreateConfig(keys[2]));

        // Set level 0
        StatsConfig config0 = createConfig();
        configManager.setConfig(keys[0], config0);
        assertSame(config0, configManager.getOrCreateConfig(keys[0]));
        assertSame(config0, configManager.getOrCreateConfig(keys[1]));
        assertSame(config2, configManager.getOrCreateConfig(keys[2]));
        assertSame(config2, configManager.getOrCreateConfig(keys[3]));
    }

    @Test
    public void testGetOrCreate_SetLevel4_GetLevel3_SetLevel2_GetLevel1() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        StatsConfig rootConfig = configManager.getRootConfig();

        // Set level 3
        StatsConfig config3 = createConfig();
        configManager.setConfig(keys[3], config3);
        assertSame(config3, configManager.getOrCreateConfig(keys[3]));

        // Get level 2
        assertSame(rootConfig, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getOrCreateConfig(keys[3]));

        // Set level 1
        StatsConfig config1 = createConfig();
        configManager.setConfig(keys[1], config1);
        assertSame(config1, configManager.getOrCreateConfig(keys[1]));
        assertSame(config1, configManager.getOrCreateConfig(keys[2]));
        assertSame(config3, configManager.getOrCreateConfig(keys[3]));

        // Get level 0
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(config1, configManager.getOrCreateConfig(keys[1]));
        assertSame(config1, configManager.getOrCreateConfig(keys[2]));
        assertSame(config3, configManager.getOrCreateConfig(keys[3]));
    }

    @Test
    public void testGetConfig() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

        StatsKey[] keys = createKeyHierarchy();

        configManager.setConfig(keys[0], createConfig());
        assertNotNull(configManager.getConfig(keys[0]));

        configManager.setConfig(keys[1], createConfig());
        assertNotNull(configManager.getConfig(keys[1]));

        configManager.setConfig(keys[2], createConfig());
        assertNotNull(configManager.getConfig(keys[2]));

        configManager.setConfig(keys[3], createConfig());
        assertNotNull(configManager.getConfig(keys[3]));
    }

    @Test
    public void testRemoveConfigAscending() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

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
        assertNull(configManager.getConfig(keys[0]));
        assertNull(configManager.getConfig(keys[1]));
        assertNull(configManager.getConfig(keys[2]));
        assertNull(configManager.getConfig(keys[3]));
        assertNull(configManager.removeConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[1]));
        assertNull(configManager.removeConfig(keys[2]));
        assertNull(configManager.removeConfig(keys[3]));
    }

    @Test
    public void testRemoveConfigDescending() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

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
        assertNull(configManager.getConfig(keys[3]));
        assertNotNull(configManager.getConfig(keys[2]));
        assertNotNull(configManager.getConfig(keys[1]));
        assertNotNull(configManager.getConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[3]));

        assertSame(config2, configManager.removeConfig(keys[2]));
        assertNull(configManager.getConfig(keys[3]));
        assertNull(configManager.getConfig(keys[2]));
        assertNotNull(configManager.getConfig(keys[1]));
        assertNotNull(configManager.getConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[2]));

        assertSame(config1, configManager.removeConfig(keys[1]));
        assertNull(configManager.getConfig(keys[3]));
        assertNull(configManager.getConfig(keys[2]));
        assertNull(configManager.getConfig(keys[1]));
        assertNotNull(configManager.getConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[1]));

        assertSame(config0, configManager.removeConfig(keys[0]));
        assertNull(configManager.getConfig(keys[3]));
        assertNull(configManager.getConfig(keys[2]));
        assertNull(configManager.getConfig(keys[1]));
        assertNull(configManager.getConfig(keys[0]));
        assertNull(configManager.removeConfig(keys[0]));
    }

    @Test
    public void testClearConfigs() {
        mockery.checking(new Expectations() {{
            ignoring(eventManager);
        }});

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

        assertNull(configManager.getConfig(keys[3]));
        assertNull(configManager.getConfig(keys[2]));
        assertNull(configManager.getConfig(keys[1]));
        assertNull(configManager.getConfig(keys[0]));
    }

}

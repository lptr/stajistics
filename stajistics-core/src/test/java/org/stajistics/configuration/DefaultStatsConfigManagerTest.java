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

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.*;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.session.recorder.DefaultDataRecorderFactory;
import org.stajistics.tracker.span.TimeDurationTracker;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManagerTest extends AbstractStajisticsTestCase {

    private EventManager mockEventManager;
    private StatsKeyFactory mockKeyFactory;

    private DefaultStatsConfigManager configManager;

    @Before
    public void setUp() {
        mockEventManager = mockery.mock(EventManager.class);
        mockKeyFactory = new DefaultStatsKeyFactory(); // TODO: actually mock this

        configManager = new DefaultStatsConfigManager(mockEventManager, mockKeyFactory);
    }

    private StatsConfig createConfig() {
        return new DefaultStatsConfig(true,
                                      TimeDurationTracker.FACTORY,
                                      DefaultSessionFactory.getInstance(),
                                      DefaultDataRecorderFactory.getInstance(),
                                      "test",
                                      null);
    }

    private StatsKey[] createKeyHierarchy() {
        return new StatsKey[] {
            new SimpleStatsKey("test", mockKeyFactory),
            new SimpleStatsKey("test.child", mockKeyFactory),
            new SimpleStatsKey("test.child.grandchild", mockKeyFactory),
            new SimpleStatsKey("test.child.grandchild.greatgrandchild", mockKeyFactory)
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
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[0]),
                                            with(config0));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[1]),
                                            with(config1));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[2]),
                                            with(config2));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[3]),
                                            with(config3));
        }});

        configManager = new DefaultStatsConfigManager(mockEventManager,
                                                      mockKeyFactory,
                                                      rootConfig,
                                                      configMap);

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

        final StatsConfig config = createConfig();

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
            configManager.setConfig((StatsKey)null, createConfig());
            fail("Allowed setConfig with null key");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testSetConfigWithNullConfig() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        StatsKey key = new SimpleStatsKey("test", mockKeyFactory);

        StatsConfig rootConfig = configManager.getRootConfig();

        configManager.setConfig(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));

        key = new SimpleStatsKey("test.child", mockKeyFactory);
        configManager.setConfig(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));

        key = new SimpleStatsKey("test.child.grandchild", mockKeyFactory);
        configManager.setConfig(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));
    }

    @Test
    public void testSetConfigAscending() {

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig rootConfig = configManager.getRootConfig();
        final StatsConfig config0 = createConfig();
        final StatsConfig config1 = createConfig();
        final StatsConfig config2 = createConfig();
        final StatsConfig config3 = createConfig();

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[0]),
                                            with(config0));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[1]),
                                            with(config1));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[2]),
                                            with(config2));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[3]),
                                            with(config3));
        }});

        // Level 0
        assertNull(configManager.getConfig(keys[0]));
        configManager.setConfig(keys[0], config0);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));

        // Level 1
        assertNull(configManager.getConfig(keys[1]));
        configManager.setConfig(keys[1], config1);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));

        // Level 2
        assertNull(configManager.getConfig(keys[2]));
        configManager.setConfig(keys[2], config2);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));

        // Level 3
        assertNull(configManager.getConfig(keys[3]));
        configManager.setConfig(keys[3], config3);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config3, configManager.getConfig(keys[3]));
    }

    @Test
    public void testSetConfigDescending() {

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig rootConfig = configManager.getRootConfig();
        final StatsConfig config0 = createConfig();
        final StatsConfig config1 = createConfig();
        final StatsConfig config2 = createConfig();
        final StatsConfig config3 = createConfig();

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[0]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[1]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[2]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[3]),
                                            with(config3));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CHANGED),
                                            with(keys[2]),
                                            with(config2));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CHANGED),
                                            with(keys[1]),
                                            with(config1));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CHANGED),
                                            with(keys[0]),
                                            with(config0));
        }});

        // Level 3
        configManager.setConfig(keys[3], config3);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(rootConfig, configManager.getConfig(keys[2]));
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 2
        configManager.setConfig(keys[2], config2);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(rootConfig, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 1
        configManager.setConfig(keys[1], config1);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(rootConfig, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());

        // Level 0
        configManager.setConfig(keys[0], config0);
        assertSame(config3, configManager.getConfig(keys[3]));
        assertSame(config2, configManager.getConfig(keys[2]));
        assertSame(config1, configManager.getConfig(keys[1]));
        assertSame(config0, configManager.getConfig(keys[0]));
        assertSame(rootConfig, configManager.getRootConfig());
    }

    @Test
    public void testGetOrCreateRootConfigAscending() {

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig rootConfig = configManager.getRootConfig();

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[0]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[1]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[2]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[3]),
                                            with(rootConfig));
        }});

        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[2]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[3]));
    }

    @Test
    public void testGetOrCreateRootConfigDescending() {

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig rootConfig = configManager.getRootConfig();

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[0]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[1]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[2]),
                                            with(rootConfig));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                            with(keys[3]),
                                            with(rootConfig));
        }});

        assertSame(rootConfig, configManager.getOrCreateConfig(keys[3]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[2]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[1]));
        assertSame(rootConfig, configManager.getOrCreateConfig(keys[0]));
    }

    @Test
    public void testGetOrCreate_GetLevel1_SetLevel2_GetLevel3_SetLevel4() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
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
            ignoring(mockEventManager);
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
            ignoring(mockEventManager);
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
            ignoring(mockEventManager);
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
            exactly(4).of(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                                      with(any(StatsKey.class)),
                                                      with(any(StatsConfig.class)));
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

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig config0 = createConfig();
        final StatsConfig config1 = createConfig();
        final StatsConfig config2 = createConfig();
        final StatsConfig config3 = createConfig();

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                                 with(any(StatsKey.class)),
                                                 with(aNonNull(StatsConfig.class)));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[0]),
                                            with(config0));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[1]),
                                            with(config1));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[2]),
                                            with(config2));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[3]),
                                            with(config3));
        }});

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

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig config0 = createConfig();
        final StatsConfig config1 = createConfig();
        final StatsConfig config2 = createConfig();
        final StatsConfig config3 = createConfig();

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                                 with(any(StatsKey.class)),
                                                 with(aNonNull(StatsConfig.class)));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[3]),
                                            with(config3));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[2]),
                                            with(config2));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[1]),
                                            with(config1));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[0]),
                                            with(config0));
        }});

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

        final StatsKey[] keys = createKeyHierarchy();

        final StatsConfig config0 = createConfig();
        final StatsConfig config1 = createConfig();
        final StatsConfig config2 = createConfig();
        final StatsConfig config3 = createConfig();

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager).fireEvent(with(EventType.CONFIG_CREATED),
                                                 with(any(StatsKey.class)),
                                                 with(aNonNull(StatsConfig.class)));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[0]),
                                            with(config0));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[1]),
                                            with(config1));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[2]),
                                            with(config2));
            one(mockEventManager).fireEvent(with(EventType.CONFIG_DESTROYED),
                                            with(keys[3]),
                                            with(config3));
        }});

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

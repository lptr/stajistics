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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.tracker.NullTracker;
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

    @Test
    public void testSetAndGetRootConfig() {
        StatsConfig config = new DefaultStatsConfig("test", NullTracker.class, DefaultSessionFactory.getInstance());

        configManager.setRootConfig(config);
        assertSame(config, configManager.getRootConfig());
    }

    @Test
    public void testRegisterNull() {
        StatsKey key = new SimpleStatsKey("test");

        StatsConfig rootConfig = configManager.getRootConfig();

        configManager.register(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));

        key = new SimpleStatsKey("test.child");
        configManager.register(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));

        key = new SimpleStatsKey("test.child.grandchild");
        configManager.register(key, null);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), configManager.getConfig(key));
    }

    @Test
    public void testRegister() {
        StatsKey key = new SimpleStatsKey("test");
        StatsConfig config = new DefaultStatsConfig("test", NullTracker.class, DefaultSessionFactory.getInstance());

        StatsConfig rootConfig = configManager.getRootConfig();

        configManager.register(key, config);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config, configManager.getConfig(key));

        key = new SimpleStatsKey("test.child");
        config = new DefaultStatsConfig("test2", NullTracker.class, DefaultSessionFactory.getInstance());
        configManager.register(key, config);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config, configManager.getConfig(key));

        key = new SimpleStatsKey("test.child.grandchild");
        config = new DefaultStatsConfig("test3", NullTracker.class, DefaultSessionFactory.getInstance());
        configManager.register(key, config);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(config, configManager.getConfig(key));
    }

    @Test
    public void testGetConfig() {
        StatsKey key = new SimpleStatsKey("test");

        StatsConfig config;
        StatsConfig rootConfig = configManager.getRootConfig();

        config = configManager.getConfig(key);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), config);

        key = new SimpleStatsKey("test.child");
        config = configManager.getConfig(key);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), config);

        key = new SimpleStatsKey("test.child.grandchild");
        config = configManager.getConfig(key);
        assertSame(rootConfig, configManager.getRootConfig());
        assertSame(configManager.getRootConfig(), config);
    }
}

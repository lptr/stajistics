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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.event.StatsEventManager;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsManagerTest {

    private Mockery mockery;

    @Before
    public void setUp() {
        mockery = new Mockery();
    }

    private DefaultStatsManager newDefaultStatsManager() {
        DefaultStatsManager mgr = DefaultStatsManager.createWithDefaults();
        Stats.loadManager(mgr); // TODO: this means we need more mocking/mockability
        return mgr;
    }

    @Test
    public void testConstruct() {
        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.getConfigManager());
        assertNotNull(mgr.getSessionManager());
        assertNotNull(mgr.getEventManager());
    }

    @Test
    public void testConstructWithNullConfigManager() {
        try {
            new DefaultStatsManager(null,
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(StatsEventManager.class));
            fail("Allowed null StatsConfigManager");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testConstructWithNullSessionManager() {
        try {
            new DefaultStatsManager(mockery.mock(StatsConfigManager.class),
                                    null,
                                    mockery.mock(StatsEventManager.class));
            fail("Allowed null StatsSessionManager");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new DefaultStatsManager(mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    null);
            fail("Allowed null StatsEventManager");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testGetConfigManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);

        StatsManager mgr = new DefaultStatsManager(configManager, sessionManager, eventManager);

        assertSame(configManager, mgr.getConfigManager());
    }

    @Test
    public void testGetSessionManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);

        StatsManager mgr = new DefaultStatsManager(configManager, sessionManager, eventManager);

        assertSame(sessionManager, mgr.getSessionManager());
    }

    @Test
    public void testGetEventManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);

        StatsManager mgr = new DefaultStatsManager(configManager, sessionManager, eventManager);

        assertSame(eventManager, mgr.getEventManager());
    }

    @Test
    public void testIsEnabledSetEnabled() {
        StatsManager mgr = newDefaultStatsManager();
        assertTrue(mgr.isEnabled());
        mgr.setEnabled(false);
        assertFalse(mgr.isEnabled());
        mgr.setEnabled(true);
        assertTrue(mgr.isEnabled());
    }

    @Test
    public void testCreateKey() {
        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.createKey("test"));
    }

    @Test
    public void testCreateKeyWithNullName() {
        StatsManager mgr = newDefaultStatsManager();
        try {
            mgr.createKey(null);
            fail("Allowed createKey with null name");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testCreateKeyBuilder() {
        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.createKey("test"));
    }

    @Test
    public void testCreateKeyBuilderWithNullName() {
        StatsManager mgr = newDefaultStatsManager();
        try {
            mgr.createKeyBuilder((String)null);
            fail("Allowed createKeyBuilder with null name");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testCreateKeyBuilderWithTemplate() {
        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.createKeyBuilder(mgr.createKey("test")));
    }

    @Test
    public void testCreateKeyBuilderWithNullTemplate() {
        StatsManager mgr = newDefaultStatsManager();
        try {
            mgr.createKeyBuilder((StatsKey)null);
            fail("Allowed createKeyBuilder with null template");
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testCreateConfigBuilder() {
        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.createConfigBuilder());
    }

    @Test
    public void testCreateConfigBuilderWithTemplate() {
        final StatsConfig config = mockery.mock(StatsConfig.class);
        mockery.checking(new Expectations() {{
            ignoring(config);
        }});

        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.createConfigBuilder(config));
    }

    @Test
    public void testGetTracker() {
        StatsManager mgr = newDefaultStatsManager();
        assertNotNull(mgr.getTracker(mgr.createKey("test")));
    }
}

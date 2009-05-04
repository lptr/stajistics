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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.event.StatsEventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.StatsTracker;

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
        return DefaultStatsManager.createWithDefaults();
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
                                    mockery.mock(StatsEventManager.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigFactory.class));
            fail("Allowed null StatsConfigManager");
        } catch (NullPointerException npe) {
            assertEquals("configManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullSessionManager() {
        try {
            new DefaultStatsManager(mockery.mock(StatsConfigManager.class),
                                    null,
                                    mockery.mock(StatsEventManager.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigFactory.class));
            fail("Allowed null StatsSessionManager");
        } catch (NullPointerException npe) {
            assertEquals("sessionManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new DefaultStatsManager(mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    null,
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigFactory.class));
            fail("Allowed null StatsEventManager");
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullKeyFactory() {
        try {
            new DefaultStatsManager(mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(StatsEventManager.class),
                                    null,
                                    mockery.mock(StatsConfigFactory.class));
            fail("Allowed null StatsKeyFactory");
        } catch (NullPointerException npe) {
            assertEquals("keyFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullConfigFactory() {
        try {
            new DefaultStatsManager(mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(StatsEventManager.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    null);
            fail("Allowed null StatsConfigFactory");
        } catch (NullPointerException npe) {
            assertEquals("configFactory", npe.getMessage());
        }
    }

    @Test
    public void testGetConfigManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigFactory configFactory = mockery.mock(StatsConfigFactory.class);

        StatsManager mgr = new DefaultStatsManager(configManager, 
                                                   sessionManager, 
                                                   eventManager,
                                                   keyFactory,
                                                   configFactory);

        assertSame(configManager, mgr.getConfigManager());
    }

    @Test
    public void testGetSessionManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigFactory configFactory = mockery.mock(StatsConfigFactory.class);

        StatsManager mgr = new DefaultStatsManager(configManager, 
                                                   sessionManager, 
                                                   eventManager,
                                                   keyFactory,
                                                   configFactory);

        assertSame(sessionManager, mgr.getSessionManager());
    }

    @Test
    public void testGetEventManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigFactory configFactory = mockery.mock(StatsConfigFactory.class);

        StatsManager mgr = new DefaultStatsManager(configManager, 
                                                   sessionManager, 
                                                   eventManager,
                                                   keyFactory,
                                                   configFactory);

        assertSame(eventManager, mgr.getEventManager());
    }

    @Test
    public void testGetKeyFactory() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigFactory configFactory = mockery.mock(StatsConfigFactory.class);

        StatsManager mgr = new DefaultStatsManager(configManager, 
                                                   sessionManager, 
                                                   eventManager,
                                                   keyFactory,
                                                   configFactory);

        assertSame(keyFactory, mgr.getKeyFactory());
    }

    @Test
    public void testGetConfigFactory() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        StatsEventManager eventManager = mockery.mock(StatsEventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigFactory configFactory = mockery.mock(StatsConfigFactory.class);

        StatsManager mgr = new DefaultStatsManager(configManager, 
                                                   sessionManager, 
                                                   eventManager,
                                                   keyFactory,
                                                   configFactory);

        assertSame(configFactory, mgr.getConfigFactory());
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
    public void testGetTracker() {
        final StatsKey key = mockery.mock(StatsKey.class);

        mockery.checking(new Expectations() {{
            ignoring(key).getName(); will(returnValue("test"));
            ignoring(key).getAttributes(); will(returnValue(Collections.emptyMap()));
        }});

        StatsManager mgr = newDefaultStatsManager();
        StatsTracker tracker = mgr.getTracker(key);

        assertNotNull(tracker);
        assertSame(key, tracker.getSession().getKey());

        mockery.assertIsSatisfied();
    }
}

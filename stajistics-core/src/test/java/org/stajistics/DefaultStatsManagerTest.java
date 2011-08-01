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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.session.StatsSessionManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsManagerTest extends AbstractStajisticsTestCase {

    private StatsManager newDefaultStatsManager() {
        return new DefaultStatsManagerFactory().createManager(StatsConstants.DEFAULT_NAMESPACE); // TODO: mock the managers
    }

    @Test
    public void testCreateWithDefaults() {
        StatsManager mgr = newDefaultStatsManager();

        assertNotNull(mgr.getConfigManager());
        assertNotNull(mgr.getSessionManager());
        assertNotNull(mgr.getEventManager());
        assertNotNull(mgr.getConfigBuilderFactory());
    }

    @Test
    public void testConstructionWithNullNamespace() {
        StatsManager manager = new DefaultStatsManager(null,
                                                       mockery.mock(StatsConfigManager.class),
                                                       mockery.mock(StatsSessionManager.class),
                                                       mockery.mock(EventManager.class),
                                                       mockery.mock(StatsKeyFactory.class),
                                                       mockery.mock(StatsConfigBuilderFactory.class));
        assertNull(manager.getNamespace());
    }

    @Test
    public void testConstructionWithEmptyNamespace() {
        StatsManager manager = new DefaultStatsManager("",
                                                       mockery.mock(StatsConfigManager.class),
                                                       mockery.mock(StatsSessionManager.class),
                                                       mockery.mock(EventManager.class),
                                                       mockery.mock(StatsKeyFactory.class),
                                                       mockery.mock(StatsConfigBuilderFactory.class));
        assertNull(manager.getNamespace());
    }

    @Test
    public void testConstructWithNullConfigManager() {
        try {
            new DefaultStatsManager("ns",
                                    null,
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class));
            fail("Allowed null StatsConfigManager");
        } catch (NullPointerException npe) {
            assertEquals("configManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullSessionManager() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    null,
                                    mockery.mock(EventManager.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class));
            fail("Allowed null StatsSessionManager");
        } catch (NullPointerException npe) {
            assertEquals("sessionManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    null,
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class));
            fail("Allowed null EventManager");
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullKeyFactory() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    null,
                                    mockery.mock(StatsConfigBuilderFactory.class));
            fail("Allowed null StatsKeyFactory");
        } catch (NullPointerException npe) {
            assertEquals("keyFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullConfigFactory() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    null);
            fail("Allowed null StatsConfigBuilderFactory");
        } catch (NullPointerException npe) {
            assertEquals("configBuilderFactory", npe.getMessage());
        }
    }

    private void expectInitialize(final StatsManager statsManager,
                                  final StatsKeyFactory keyFactory,
                                  final EventManager eventManager,
                                  final StatsConfigManager configManager,
                                  final StatsSessionManager sessionManager) {
        mockery.checking(new Expectations() {{
            one(keyFactory).setNamespace(statsManager.getNamespace());

            one(eventManager).initialize();
            one(configManager).initialize();
            one(sessionManager).initialize();

            one(eventManager).fireEvent(EventType.STATS_MANAGER_INITIALIZED, null, statsManager);
        }});
    }

    @Test
    public void testInitialize() {
        final StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        final StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        final EventManager eventManager = mockery.mock(EventManager.class);
        final StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        final StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        final StatsManager mgr = new DefaultStatsManager("ns",
                                                         configManager,
                                                         sessionManager,
                                                         eventManager,
                                                         keyFactory,
                                                         configBuilderFactory);

        expectInitialize(mgr, keyFactory, eventManager, configManager, sessionManager);

        try {
            StatsManagerRegistry.getInstance().getStatsManager("ns");
            fail("Found namespace: ns");
        } catch (StatsNamespaceNotFoundException e) {
            // Expected
        }

        try {
            mgr.initialize();

            assertEquals(mgr, StatsManagerRegistry.getInstance().getStatsManager("ns"));

            // Try again to test no effect
            mgr.initialize();
        } finally {
            StatsManagerRegistry.getInstance().removeStatsManager("ns");
        }
    }

    @Test
    public void testShutdown() {

        final StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        final StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        final EventManager eventManager = mockery.mock(EventManager.class);
        final StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        final StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        final StatsManager mgr = new DefaultStatsManager("ns",
                                                         configManager,
                                                         sessionManager,
                                                         eventManager,
                                                         keyFactory,
                                                         configBuilderFactory);

        expectInitialize(mgr, keyFactory, eventManager, configManager, sessionManager);
        mgr.initialize();

        mockery.checking(new Expectations() {{
            one(eventManager).fireEvent(EventType.STATS_MANAGER_SHUTTING_DOWN, null, mgr);

            one(sessionManager).shutdown();
            one(configManager).shutdown();
            one(eventManager).shutdown();
        }});

        mgr.shutdown();

        assertFalse(mgr.isEnabled());
    }

    @Test
    public void testGetConfigManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   keyFactory,
                                                   configBuilderFactory);

        assertSame(configManager, mgr.getConfigManager());
    }

    @Test
    public void testGetSessionManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   keyFactory,
                                                   configBuilderFactory);

        assertSame(sessionManager, mgr.getSessionManager());
    }

    @Test
    public void testGetEventManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   keyFactory,
                                                   configBuilderFactory);

        assertSame(eventManager, mgr.getEventManager());
    }

    @Test
    public void testGetKeyFactory() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   keyFactory,
                                                   configBuilderFactory);

        assertSame(keyFactory, mgr.getKeyFactory());
    }

    @Test
    public void testGetConfigFactory() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   keyFactory,
                                                   configBuilderFactory);

        assertSame(configBuilderFactory, mgr.getConfigBuilderFactory());
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
    public void testSerializeDeserialize() {

        StatsManager manager = newDefaultStatsManager();

        // Populate the data structures a bit
        StatsFactory factory = new DefaultStatsFactory(manager);

        StatsKey key1 = manager.getKeyFactory().createKey("test1");
        factory.getSpanTracker(key1).track().commit();
        StatsKey key2 = manager.getKeyFactory().createKey("test2");
        factory.getSpanTracker(key2).track().commit();
        StatsKey key3 = manager.getKeyFactory().createKey("test3");
        factory.getSpanTracker(key3).track().commit();

        assertSerializable(manager);
    }
}

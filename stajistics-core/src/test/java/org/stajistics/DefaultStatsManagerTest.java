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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.task.TaskService;
import org.stajistics.tracker.TrackerLocator;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsManagerTest extends AbstractStajisticsTestCase {

    private DefaultStatsManager newDefaultStatsManager() {
        return new DefaultStatsManagerFactory().createManager(); // TODO: mock the managers
    }

    @Test
    public void testCreateWithDefaults() {
        StatsManager mgr = newDefaultStatsManager();
        Stats.loadManager(mgr);

        assertNotNull(mgr.getConfigManager());
        assertNotNull(mgr.getSessionManager());
        assertNotNull(mgr.getEventManager());
        assertNotNull(mgr.getTrackerLocator());
        assertNotNull(mgr.getConfigBuilderFactory());
        assertNotNull(mgr.getTaskService());
    }

    @Test
    public void testConstructionWithNullNamespace() {
        StatsManager manager = new DefaultStatsManager(null,
                                                       mockery.mock(StatsConfigManager.class),
                                                       mockery.mock(StatsSessionManager.class),
                                                       mockery.mock(EventManager.class),
                                                       mockery.mock(TrackerLocator.class),
                                                       mockery.mock(StatsKeyFactory.class),
                                                       mockery.mock(StatsConfigBuilderFactory.class),
                                                       mockery.mock(TaskService.class));
        assertNotNull(manager.getNamespace());
    }

    @Test
    public void testConstructionWithEmptyNamespace() {
        StatsManager manager = new DefaultStatsManager("",
                                                       mockery.mock(StatsConfigManager.class),
                                                       mockery.mock(StatsSessionManager.class),
                                                       mockery.mock(EventManager.class),
                                                       mockery.mock(TrackerLocator.class),
                                                       mockery.mock(StatsKeyFactory.class),
                                                       mockery.mock(StatsConfigBuilderFactory.class),
                                                       mockery.mock(TaskService.class));
        assertNotNull(manager.getNamespace());
        assertFalse(manager.getNamespace().isEmpty());
    }

    @Test
    public void testConstructWithNullConfigManager() {
        try {
            new DefaultStatsManager("ns",
                                    null,
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    mockery.mock(TrackerLocator.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class),
                                    mockery.mock(TaskService.class));
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
                                    mockery.mock(TrackerLocator.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class),
                                    mockery.mock(TaskService.class));
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
                                    mockery.mock(TrackerLocator.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class),
                                    mockery.mock(TaskService.class));
            fail("Allowed null EventManager");
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullTrackerLocator() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    null,
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class),
                                    mockery.mock(TaskService.class));
            fail("Allowed null TrackerLocator");
        } catch (NullPointerException npe) {
            assertEquals("trackerLocator", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullKeyFactory() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    mockery.mock(TrackerLocator.class),
                                    null,
                                    mockery.mock(StatsConfigBuilderFactory.class),
                                    mockery.mock(TaskService.class));
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
                                    mockery.mock(TrackerLocator.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    null,
                                    mockery.mock(TaskService.class));
            fail("Allowed null StatsConfigBuilderFactory");
        } catch (NullPointerException npe) {
            assertEquals("configBuilderFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullTaskService() {
        try {
            new DefaultStatsManager("ns",
                                    mockery.mock(StatsConfigManager.class),
                                    mockery.mock(StatsSessionManager.class),
                                    mockery.mock(EventManager.class),
                                    mockery.mock(TrackerLocator.class),
                                    mockery.mock(StatsKeyFactory.class),
                                    mockery.mock(StatsConfigBuilderFactory.class),
                                    null);
            fail("Allowed null TaskService");
        } catch (NullPointerException npe) {
            assertEquals("taskService", npe.getMessage());
        }
    }

    @Test
    public void testGetConfigManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        TrackerLocator trackerLocator = mockery.mock(TrackerLocator.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        TaskService taskService = mockery.mock(TaskService.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   trackerLocator,
                                                   keyFactory,
                                                   configBuilderFactory,
                                                   taskService);

        assertSame(configManager, mgr.getConfigManager());
    }

    @Test
    public void testGetSessionManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        TrackerLocator trackerLocator = mockery.mock(TrackerLocator.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        TaskService taskService = mockery.mock(TaskService.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   trackerLocator,
                                                   keyFactory,
                                                   configBuilderFactory,
                                                   taskService);

        assertSame(sessionManager, mgr.getSessionManager());
    }

    @Test
    public void testGetEventManager() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        TrackerLocator trackerLocator = mockery.mock(TrackerLocator.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        TaskService taskService = mockery.mock(TaskService.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   trackerLocator,
                                                   keyFactory,
                                                   configBuilderFactory,
                                                   taskService);

        assertSame(eventManager, mgr.getEventManager());
    }

    @Test
    public void testGetTrackerLocator() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);

        TrackerLocator trackerLocator = mockery.mock(TrackerLocator.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        TaskService taskService = mockery.mock(TaskService.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   trackerLocator,
                                                   keyFactory,
                                                   configBuilderFactory,
                                                   taskService);

        assertSame(trackerLocator, mgr.getTrackerLocator());
    }

    @Test
    public void testGetKeyFactory() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        TrackerLocator trackerLocator = mockery.mock(TrackerLocator.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        TaskService taskService = mockery.mock(TaskService.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   trackerLocator,
                                                   keyFactory,
                                                   configBuilderFactory,
                                                   taskService);

        assertSame(keyFactory, mgr.getKeyFactory());
    }

    @Test
    public void testGetConfigFactory() {
        StatsConfigManager configManager = mockery.mock(StatsConfigManager.class);
        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);
        EventManager eventManager = mockery.mock(EventManager.class);
        TrackerLocator trackerLocator = mockery.mock(TrackerLocator.class);
        StatsKeyFactory keyFactory = mockery.mock(StatsKeyFactory.class);
        StatsConfigBuilderFactory configBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        TaskService taskService = mockery.mock(TaskService.class);

        StatsManager mgr = new DefaultStatsManager("ns",
                                                   configManager,
                                                   sessionManager,
                                                   eventManager,
                                                   trackerLocator,
                                                   keyFactory,
                                                   configBuilderFactory,
                                                   taskService);

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
        StatsKey key1 = manager.getKeyFactory().createKey("test1");
        manager.getTrackerLocator().getSpanTracker(key1).track().commit();
        StatsKey key2 = manager.getKeyFactory().createKey("test2");
        manager.getTrackerLocator().getSpanTracker(key2).track().commit();
        StatsKey key3 = manager.getKeyFactory().createKey("test3");
        manager.getTrackerLocator().getSpanTracker(key3).track().commit();

        assertSerializable(manager);
    }
}

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.bootstrap.StatsManagerFactory;
import org.stajistics.configuration.StatsConfigBuilder;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.task.TaskService;
import org.stajistics.tracker.TrackerLocator;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 *
 *mockTrackerLocator = mockery.mock(TrackerLocator.class);

 * @author The Stajistics Project
 */
public class StatsTest extends AbstractStajisticsTestCase {

    private StatsManager mockManager;
    private TrackerLocator mockTrackerLocator;

    @Before
    public void setUp() {
        mockManager = mockery.mock(StatsManager.class);
        mockTrackerLocator = mockery.mock(TrackerLocator.class);

        mockery.checking(new Expectations() {{
            allowing(mockManager).getTrackerLocator(); will(returnValue(mockTrackerLocator));
        }});

        Stats.loadManager(mockManager);
    }

    @Test
    public void testGetManager() {
        assertSame(mockManager, Stats.getManager());
    }

    @Test
    public void testLoadManagerWithNull() {
        try {
            Stats.loadManager(null);
        } catch (NullPointerException npe) {
            assertEquals("manager", npe.getMessage());
        }
    }

    @Test
    public void testLoadManager() {
        Stats.loadManager(mockManager);
        assertEquals(mockManager, Stats.getManager());
    }

    @Test
    public void testLoadDefaultStatsManagerReturnsNotNull() {
        assertNotNull(Stats.loadDefaultStatsManager());
    }

    @Test
    public void testLoadStatsManagerFactoryFromProperties() throws Exception {
        try {
            System.getProperties()
                  .setProperty(StatsManagerFactory.class.getName(),
                               ClassLoadableMockStatsManagerFactory.class.getName());

            StatsManagerFactory factory = Stats.loadStatsManagerFactoryFromProperties();

            assertNotNull(factory);
            assertInstanceOf(factory, ClassLoadableMockStatsManagerFactory.class);

        } finally {
            System.getProperties()
                  .remove(StatsManagerFactory.class.getName());
        }
    }

    @Test
    public void testLoadStatsManagerFromProperties() throws Exception {
        try {
            System.getProperties()
                  .setProperty(StatsManager.class.getName(),
                               ClassLoadableMockStatsManager.class.getName());

            StatsManager mgr = Stats.loadStatsManagerFromProperties();

            assertNotNull(mgr);
            assertInstanceOf(mgr, ClassLoadableMockStatsManager.class);

        } finally {
            System.getProperties()
                  .remove(StatsManager.class.getName());
        }
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadInvalidStatsManagerFromProperties() throws Exception {

        try {
            System.getProperties()
                  .setProperty(StatsManager.class.getName(),
                               "org.stajistics.DoesntExistAtAllInAnyWayWhatSoEverSoThereHa");

            StatsManager mgr = Stats.loadStatsManagerFromProperties();

            assertNotNull(mgr);
            assertInstanceOf(mgr, ClassLoadableMockStatsManager.class);

        } finally {
            System.getProperties()
                  .remove(StatsManager.class.getName());
        }
    }

    @Test
    public void testGetConfigManager() {
        mockery.checking(new Expectations() {{
            one(mockManager).getConfigManager();
        }});

        Stats.getConfigManager();
    }

    @Test
    public void testGetSessionManager() {
        mockery.checking(new Expectations() {{
            one(mockManager).getSessionManager();
        }});

        Stats.getSessionManager();
    }

    @Test
    public void testGetEventManager() {
        mockery.checking(new Expectations() {{
            one(mockManager).getEventManager();
        }});

        Stats.getEventManager();
    }

    @Test
    public void testIsEnabled() {
        mockery.checking(new Expectations() {{
            one(mockManager).isEnabled();
        }});

        Stats.isEnabled();
    }


    /* NESTED CLASSES */

    @SuppressWarnings("serial")
    public static final class ClassLoadableMockStatsManager implements StatsManager {

        @Override
        public String getNamespace() {
            return "ns";
        }

        @Override
        public void initialize() {}

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public void shutdown() {}

        @Override
        public StatsConfigManager getConfigManager() {
            return null;
        }

        @Override
        public EventManager getEventManager() {
            return null;
        }

        @Override
        public StatsSessionManager getSessionManager() {
            return null;
        }

        @Override
        public TrackerLocator getTrackerLocator() {
            return null;
        }

        @Override
        public StatsKeyFactory getKeyFactory() {
            return null;
        }

        @Override
        public StatsConfigBuilderFactory getConfigBuilderFactory() {
            return null;
        }

        @Override
        public TaskService getTaskService() {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean enabled) {}

        @Override
        public UncaughtExceptionHandler getUncaughtExceptionHandler() {
            return NullUncaughtExceptionHandler.getInstance();
        }

        @Override
        public void setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler) {}

    }

    public static final class ClassLoadableMockStatsManagerFactory implements StatsManagerFactory {
        @Override
        public StatsManager createManager() {
            return null;
        }
    }
}

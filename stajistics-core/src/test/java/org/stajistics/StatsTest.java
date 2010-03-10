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
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.event.StatsEventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.task.TaskService;
import org.stajistics.tracker.StatsTrackerLocator;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class StatsTest {

    private Mockery mockery;
    private StatsManager mockManager;
    private StatsTrackerLocator mockTrackerLocator;
    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockManager = mockery.mock(StatsManager.class);
        mockTrackerLocator = mockery.mock(StatsTrackerLocator.class);

        mockery.checking(new Expectations() {{
            allowing(mockManager).getTrackerLocator(); will(returnValue(mockTrackerLocator));
        }});

        mockKey = mockery.mock(StatsKey.class);
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
    public void testLoadStatsManagerFromSystemProperties() throws Exception {

        try {
            System.getProperties()
                  .setProperty(StatsManager.class.getName(),
                               ClassLoadableMockStatsManager.class.getName());

            StatsManager mgr = Stats.loadStatsManagerFromSystemProperties();

            assertNotNull(mgr);
            assertTrue(mgr instanceof ClassLoadableMockStatsManager);

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

    @Test
    public void testStartWithKeyName() {
        final String keyName = "test.name";
        final SpanTracker mockTracker = mockery.mock(SpanTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockTrackerLocator).getSpanTracker(with(any(StatsKey.class))); will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.track(keyName));
    }

    @Test
    public void testStartWithStatsKey() {
        final SpanTracker mockTracker = mockery.mock(SpanTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getSpanTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.track(mockKey));
    }
/*
    @Test
    public void testSpanTrackWithStatsKeys() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getTracker(with(new StatsKey[] { mockKey, mockKey2 }));
            will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.start(mockKey, mockKey2));
    }
*/
    @Test
    public void testIncidentWithKeyName() {
        final String keyName = "test.name";
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockTrackerLocator).getIncidentTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).incident();
        }});

        Stats.incident(keyName);
    }

    @Test
    public void testIncidentWithStatsKey() {
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getIncidentTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).incident(); will(returnValue(mockTracker));
        }});

        Stats.incident(mockKey);
    }

    @Test
    public void testIncidentWithStatsKeys() {
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getIncidentTracker(with(new StatsKey[] { mockKey, mockKey2 }));
            will(returnValue(mockTracker));
            one(mockTracker).incident(); will(returnValue(mockTracker));
        }});

        Stats.incident(mockKey, mockKey2);
    }

    @Test
    public void testFailureWithKeyName() {

        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final String keyName = "test.name";
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");
        final StatsKeyBuilder mockKeyBuilder = mockery.mock(StatsKeyBuilder.class);

        final Exception e = new Exception();

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockKey).buildCopy(); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withNameSuffix("exception"); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withAttribute("threw", e.getClass().getName()); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).newKey(); will(returnValue(mockKey2));
            one(mockTrackerLocator).getIncidentTracker(mockKey2); will(returnValue(mockTracker));
            one(mockTracker).incident();
        }});

        Stats.failure(e, keyName);
    }

    @Test
    public void testFailureWithStatsKey() {

        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");
        final StatsKeyBuilder mockKeyBuilder = mockery.mock(StatsKeyBuilder.class);

        final Exception e = new Exception();

        mockery.checking(new Expectations() {{
            one(mockKey).buildCopy(); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withNameSuffix("exception"); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withAttribute("threw", e.getClass().getName()); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).newKey(); will(returnValue(mockKey2));
            one(mockTrackerLocator).getIncidentTracker(mockKey2); will(returnValue(mockTracker));
            one(mockTracker).incident();
        }});

        Stats.failure(e, mockKey);
    }

    @Test
    public void testManualWithKeyName() {
        final String keyName = "test.name";
        final ManualTracker mockManualTracker = mockery.mock(ManualTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockTrackerLocator).getManualTracker(with(any(StatsKey.class))); will(returnValue(mockManualTracker));
        }});

        assertEquals(mockManualTracker, Stats.getManualTracker(keyName));
    }

    @Test
    public void testManualWithStatsKey() {

        final ManualTracker mockManualTracker = mockery.mock(ManualTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getManualTracker(with(mockKey)); will(returnValue(mockManualTracker));
        }});

        assertSame(mockManualTracker, Stats.getManualTracker(mockKey));
    }

    @Test
    public void testNewKey() {
        final String keyName = "test";
        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(with(keyName)); will(returnValue(mockKey));
        }});

        assertSame(mockKey, Stats.newKey(keyName));
    }

    @Test
    public void testBuildKey() {
        final String keyName = "test";
        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);
        final StatsKeyBuilder mockKeyBuilder = mockery.mock(StatsKeyBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKeyBuilder(with(keyName)); will(returnValue(mockKeyBuilder));
        }});

        assertSame(mockKeyBuilder, Stats.buildKey(keyName));
    }

    @Test
    public void testBuildConfig() {
        final StatsConfigFactory mockConfigFactory = mockery.mock(StatsConfigFactory.class);
        final StatsConfigBuilder mockConfigBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getConfigFactory(); will(returnValue(mockConfigFactory));
            one(mockConfigFactory).createConfigBuilder(); will(returnValue(mockConfigBuilder));
        }});

        assertSame(mockConfigBuilder, Stats.buildConfig());
    }

    /* NESTED CLASSES */

    @SuppressWarnings("serial")
    public static final class ClassLoadableMockStatsManager implements StatsManager {

        @Override
        public StatsConfigManager getConfigManager() {
            return null;
        }

        @Override
        public StatsEventManager getEventManager() {
            return null;
        }

        @Override
        public StatsSessionManager getSessionManager() {
            return null;
        }

        @Override
        public StatsTrackerLocator getTrackerLocator() {
            return null;
        }

        @Override
        public StatsKeyFactory getKeyFactory() {
            return null;
        }

        @Override
        public StatsConfigFactory getConfigFactory() {
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
        public void shutdown() {}
    }
}

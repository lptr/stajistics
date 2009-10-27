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
import org.stajistics.snapshot.StatsSnapshotManager;
import org.stajistics.tracker.ManualStatsTracker;
import org.stajistics.tracker.StatsTracker;

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
    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockManager = mockery.mock(StatsManager.class);
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
    public void testGetTrackerWithKeyName() {
        final String keyName = "test.name";
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockManager).getTracker(with(any(StatsKey.class))); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.getTracker(keyName));
    }

    @Test
    public void testGetTrackerWithStatsKey() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
        }});

        assertSame(mockTracker, Stats.getTracker(mockKey));
    }

    @Test
    public void testGetTrackerWithStatsKeys() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(new StatsKey[] { mockKey, mockKey2 })); 
            will(returnValue(mockTracker));
        }});

        assertSame(mockTracker, Stats.getTracker(mockKey, mockKey2));
    }
    
    @Test
    public void testTrackWithKeyName() {
        final String keyName = "test.name";
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockManager).getTracker(with(any(StatsKey.class))); will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.track(keyName));
    }

    @Test
    public void testTrackWithStatsKey() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.track(mockKey));
    }

    @Test
    public void testTrackWithStatsKeys() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(new StatsKey[] { mockKey, mockKey2 })); 
            will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, Stats.track(mockKey, mockKey2));
    }

    @Test
    public void testIncidentWithKeyName() {
        final String keyName = "test.name";
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).incident();
        }});

        Stats.incident(keyName);
    }
    
    @Test
    public void testIncidentWithStatsKey() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).incident(); will(returnValue(mockTracker));
        }});

        Stats.incident(mockKey);
    }

    @Test
    public void testIncidentWithStatsKeys() {
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");

        mockery.checking(new Expectations() {{
            one(mockManager).getTracker(with(new StatsKey[] { mockKey, mockKey2 })); 
            will(returnValue(mockTracker));
            one(mockTracker).incident(); will(returnValue(mockTracker));
        }});

        Stats.incident(mockKey, mockKey2);
    }

    @Test
    public void testFailureWithKeyName() {

        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final String keyName = "test.name";
        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

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
            one(mockManager).getTracker(mockKey2); will(returnValue(mockTracker));
            one(mockTracker).incident();
        }});

        Stats.failure(e, keyName);
    }

    @Test
    public void testFailureWithStatsKey() {

        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");
        final StatsKeyBuilder mockKeyBuilder = mockery.mock(StatsKeyBuilder.class); 

        final Exception e = new Exception();

        mockery.checking(new Expectations() {{
            one(mockKey).buildCopy(); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withNameSuffix("exception"); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).withAttribute("threw", e.getClass().getName()); will(returnValue(mockKeyBuilder));
            one(mockKeyBuilder).newKey(); will(returnValue(mockKey2));
            one(mockManager).getTracker(mockKey2); will(returnValue(mockTracker));
            one(mockTracker).incident();
        }});

        Stats.failure(e, mockKey);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailureWithNoStatsKeys() {
        Stats.failure(new Throwable());
    }

    @Test
    public void testManualWithKeyName() {
        final String keyName = "test.name";
        final ManualStatsTracker mockManualTracker = mockery.mock(ManualStatsTracker.class);

        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(keyName); will(returnValue(mockKey));
            one(mockManager).getManualTracker(with(any(StatsKey.class))); will(returnValue(mockManualTracker));
        }});

        assertEquals(mockManualTracker, Stats.manual(keyName));
    }

    @Test
    public void testManualWithStatsKey() {

        final ManualStatsTracker mockManualTracker = mockery.mock(ManualStatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getManualTracker(with(mockKey)); will(returnValue(mockManualTracker));
        }});

        assertSame(mockManualTracker, Stats.manual(mockKey));
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
        public StatsConfigFactory getConfigFactory() {
            return null;
        }

        @Override
        public StatsConfigManager getConfigManager() {
            return null;
        }

        @Override
        public StatsEventManager getEventManager() {
            return null;
        }

        @Override
        public StatsKeyFactory getKeyFactory() {
            return null;
        }

        @Override
        public ManualStatsTracker getManualTracker(StatsKey key) {
            return null;
        }

        @Override
        public StatsSessionManager getSessionManager() {
            return null;
        }

        @Override
        public StatsSnapshotManager getSnapshotManager() {
            return null;
        }

        @Override
        public StatsTracker getTracker(StatsKey key) {
            return null;
        }

        @Override
        public StatsTracker getTracker(StatsKey... keys) {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean enabled) {}

    }
}

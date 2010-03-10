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
package org.stajistics.tracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
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
public class DefaultStatsTrackerLocatorTest {

    private Mockery mockery;
    private StatsConfigManager mockConfigManager;
    private StatsSessionManager mockSessionManager;

    private StatsKey mockKey;
    private StatsSession mockSession;
    private StatsConfig mockConfig;
    private StatsTrackerFactory<?> mockTrackerFactory;
    private StatsTracker mockTracker;

    private StatsTrackerLocator trackerLocator;

    @Before
    public void setUp() {

        mockery = new Mockery();

        mockConfigManager = mockery.mock(StatsConfigManager.class);
        mockSessionManager = mockery.mock(StatsSessionManager.class);

        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        mockConfig = mockery.mock(StatsConfig.class);
        mockSession = mockery.mock(StatsSession.class);
        mockTrackerFactory = mockery.mock(StatsTrackerFactory.class);

        mockTracker = mockery.mock(StatsTracker.class);

        trackerLocator = new DefaultStatsTrackerLocator(mockConfigManager, mockSessionManager);
    }

    protected void buildExpectations(final boolean configEnabled,
                                     final StatsTracker returnMockTracker) {
        mockery.checking(new Expectations() {{
            allowing(mockSessionManager).getOrCreateSession(with(mockKey)); will(returnValue(mockSession));
            allowing(mockConfigManager).getOrCreateConfig(with(mockKey)); will(returnValue(mockConfig));
            allowing(mockConfig).isEnabled(); will(returnValue(configEnabled));
            allowing(mockConfig).getTrackerFactory(); will(returnValue(mockTrackerFactory));
            allowing(mockTrackerFactory).createTracker(with(mockKey),
                                                       with(mockSessionManager));
            will(returnValue(returnMockTracker));
        }});   
    }

    @Test
    public void testConstructWithNullConfigManager() {
        try {
            new DefaultStatsTrackerLocator(null, mockSessionManager);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("configManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullSessionManager() {
        try {
            new DefaultStatsTrackerLocator(mockConfigManager, null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("sessionManager", npe.getMessage());
        }
    }

    @Test
    public void testInitialState() {
        assertTrue(trackerLocator.isEnabled());
    }

    @Test
    public void testIsEnabledSetEnabled() {
        trackerLocator.setEnabled(false);
        assertFalse(trackerLocator.isEnabled());
        trackerLocator.setEnabled(true);
        assertTrue(trackerLocator.isEnabled());
    }

    /* STATS TRACKER */

    @Test
    public void testGetTracker() {
        buildExpectations(true, mockTracker);

        StatsTracker tracker = trackerLocator.getTracker(mockKey);

        assertNotNull(tracker);
        assertSame(mockTracker, tracker);
    }

    @Test
    public void testGetTrackerWithNullKey() {
        try {
            trackerLocator.getTracker(null);
            fail("Allowed getTracker with null StatsKey");

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testGetTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getTracker(mockKey));
    }

    @Test
    public void testGetTrackerWhenConfigDisabled() {
        buildExpectations(false, mockTracker);

        assertEquals(NullTracker.getInstance(), trackerLocator.getTracker(mockKey));
    }

    /* SPAN TRACKER */

    @Test
    public void testGetSpanTracker() {
        SpanTracker mockSpanTracker = mockery.mock(SpanTracker.class);
        buildExpectations(true, mockSpanTracker);

        SpanTracker tracker = trackerLocator.getSpanTracker(mockKey);

        assertNotNull(tracker);
        assertEquals(mockSpanTracker, tracker);
    }

    @Test
    public void testGetSpanTrackerWithNullKey() {
        try {
            trackerLocator.getSpanTracker((StatsKey)null);
            fail("Allowed getSpanTracker with null StatsKey");

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testGetSpanTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getSpanTracker(mockKey));
    }

    @Test
    public void testGetSpanTrackerWhenConfigDisabled() {
        buildExpectations(false, mockTracker);

        assertEquals(NullTracker.getInstance(), trackerLocator.getSpanTracker(mockKey));
    }

    @Test
    public void testGetSpanTrackerWithInvalidConfig() {
        buildExpectations(true, mockTracker);

        SpanTracker tracker = trackerLocator.getSpanTracker(mockKey);
        SpanTracker expectedTracker = SpanTracker.FACTORY.createTracker(mockKey, mockSessionManager);

        assertNotNull(tracker);
        assertEquals(expectedTracker.getClass(), tracker.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSpanTrackerWithSizeZeroArray() {
        trackerLocator.getSpanTracker(new StatsKey[] {});
    }

    @Test
    public void testGetSpanTrackerWithSizeOneKeyArray() {
        buildExpectations(true, mockTracker);

        StatsTracker tracker = trackerLocator.getSpanTracker(new StatsKey[] { mockKey });
        SpanTracker expectedTracker = SpanTracker.FACTORY.createTracker(mockKey, mockSessionManager);

        assertNotNull(tracker);
        assertFalse(tracker instanceof CompositeStatsTracker<?>);
        assertEquals(expectedTracker.getClass(), tracker.getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetSpanTrackerWithSizeTwoKeyArray() {
        SpanTracker mockSpanTracker = mockery.mock(SpanTracker.class);
        buildExpectations(true, mockSpanTracker);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "StatsKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "test2");

        final StatsSession mockSession2 = mockery.mock(StatsSession.class, "StatsSession2");
        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "StatsConfig2");
        final StatsTrackerFactory<SpanTracker> mockTrackerFactory2 = 
            mockery.mock(StatsTrackerFactory.class, "StatsTrackerFactory2");

        final SpanTracker mockSpanTracker2 = mockery.mock(SpanTracker.class, "SpanTracker2");

        mockery.checking(new Expectations() {{
            allowing(mockSessionManager).getOrCreateSession(with(mockKey2)); will(returnValue(mockSession2));
            allowing(mockConfigManager).getOrCreateConfig(with(mockKey2)); will(returnValue(mockConfig2));
            allowing(mockConfig2).isEnabled(); will(returnValue(true));
            allowing(mockConfig2).getTrackerFactory(); will(returnValue(mockTrackerFactory2));
            allowing(mockTrackerFactory2).createTracker(with(mockKey2),
                                                        with(mockSessionManager));
            will(returnValue(mockSpanTracker2));
        }});

        StatsTracker tracker = trackerLocator.getSpanTracker(new StatsKey[] { mockKey, mockKey2 });

        assertNotNull(tracker);
        assertTrue(tracker instanceof CompositeStatsTracker<?>);

        CompositeStatsTracker<?> cTracker = (CompositeStatsTracker<?>)tracker;
        assertEquals(2, cTracker.composites().size());

        Iterator<?> itr = cTracker.composites().iterator();
        assertSame(mockSpanTracker, itr.next());
        assertSame(mockSpanTracker2, itr.next());
    }

    /* INCIDENT TRACKER */

    @Test
    public void testGetIncidentTracker() {
        IncidentTracker mockIncidentTracker = mockery.mock(IncidentTracker.class);
        buildExpectations(true, mockIncidentTracker);

        IncidentTracker tracker = trackerLocator.getIncidentTracker(mockKey);

        assertNotNull(tracker);
        assertEquals(mockIncidentTracker, tracker);
    }

    @Test
    public void testGetIncidentTrackerWithNullKey() {
        try {
            trackerLocator.getIncidentTracker((StatsKey)null);
            fail("Allowed getIncidentTracker with null StatsKey");

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testGetIncidentTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getIncidentTracker(mockKey));
    }

    @Test
    public void testGetIncidentTrackerWhenConfigDisabled() {
        buildExpectations(false, mockTracker);

        assertEquals(NullTracker.getInstance(), trackerLocator.getIncidentTracker(mockKey));
    }
    
    @Test
    public void testGetIncidentTrackerWithInvalidConfig() {
        buildExpectations(true, mockTracker);

        IncidentTracker tracker = trackerLocator.getIncidentTracker(mockKey);
        IncidentTracker expectedTracker = IncidentTracker.FACTORY.createTracker(mockKey, mockSessionManager);

        assertNotNull(tracker);
        assertEquals(expectedTracker.getClass(), tracker.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIncidentTrackerWithSizeZeroArray() {
        trackerLocator.getIncidentTracker(new StatsKey[] {});
    }

    @Test
    public void testGetIncidentTrackerWithSizeOneKeyArray() {
        buildExpectations(true, mockTracker);

        IncidentTracker tracker = trackerLocator.getIncidentTracker(new StatsKey[] { mockKey });
        IncidentTracker expectedTracker = IncidentTracker.FACTORY.createTracker(mockKey, mockSessionManager);

        assertNotNull(tracker);
        assertFalse(tracker instanceof CompositeStatsTracker<?>);
        assertEquals(expectedTracker.getClass(), tracker.getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetIncidentTrackerWithSizeTwoKeyArray() {
        IncidentTracker mockIncidentTracker = mockery.mock(IncidentTracker.class);
        buildExpectations(true, mockIncidentTracker);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "StatsKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "test2");

        final StatsSession mockSession2 = mockery.mock(StatsSession.class, "StatsSession2");
        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "StatsConfig2");
        final StatsTrackerFactory<IncidentTracker> mockTrackerFactory2 = 
            mockery.mock(StatsTrackerFactory.class, "StatsTrackerFactory2");

        final IncidentTracker mockIncidentTracker2 = mockery.mock(IncidentTracker.class, "IncidentTracker2");

        mockery.checking(new Expectations() {{
            allowing(mockSessionManager).getOrCreateSession(with(mockKey2)); will(returnValue(mockSession2));
            allowing(mockConfigManager).getOrCreateConfig(with(mockKey2)); will(returnValue(mockConfig2));
            allowing(mockConfig2).isEnabled(); will(returnValue(true));
            allowing(mockConfig2).getTrackerFactory(); will(returnValue(mockTrackerFactory2));
            allowing(mockTrackerFactory2).createTracker(with(mockKey2),
                                                        with(mockSessionManager));
            will(returnValue(mockIncidentTracker2));
        }});

        StatsTracker tracker = trackerLocator.getIncidentTracker(new StatsKey[] { mockKey, mockKey2 });

        assertNotNull(tracker);
        assertTrue(tracker instanceof CompositeStatsTracker<?>);

        CompositeStatsTracker<?> cTracker = (CompositeStatsTracker<?>)tracker;
        assertEquals(2, cTracker.composites().size());

        Iterator<?> itr = cTracker.composites().iterator();
        assertSame(mockIncidentTracker, itr.next());
        assertSame(mockIncidentTracker2, itr.next());
    }

    /* MANUAL TRACKER */

    @Test
    public void testGetManualTracker() {
        ManualTracker mockManualTracker = mockery.mock(ManualTracker.class);
        buildExpectations(true, mockManualTracker);

        ManualTracker tracker = trackerLocator.getManualTracker(mockKey);

        assertNotNull(tracker);
        assertEquals(mockManualTracker, tracker);
    }

    @Test
    public void testGetManualTrackerWithNullKey() {
        try {
            trackerLocator.getManualTracker((StatsKey)null);
            fail("Allowed getManualTracker with null StatsKey");

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testGetManualTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getManualTracker(mockKey));
    }

    @Test
    public void testGetManualTrackerWhenConfigDisabled() {
        buildExpectations(false, mockTracker);

        assertEquals(NullTracker.getInstance(), trackerLocator.getManualTracker(mockKey));
    }

    @Test
    public void testGetManualTrackerWithInvalidConfig() {
        buildExpectations(true, mockTracker);

        ManualTracker tracker = trackerLocator.getManualTracker(mockKey);
        ManualTracker expectedTracker = ManualTracker.FACTORY.createTracker(mockKey, mockSessionManager);

        assertNotNull(tracker);
        assertEquals(expectedTracker.getClass(), tracker.getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetManualTrackerWithSizeZeroArray() {
        trackerLocator.getManualTracker(new StatsKey[] {});
    }

    @Test
    public void testGetManualTrackerWithSizeOneKeyArray() {
        buildExpectations(true, mockTracker);

        ManualTracker tracker = trackerLocator.getManualTracker(new StatsKey[] { mockKey });
        ManualTracker expectedTracker = ManualTracker.FACTORY.createTracker(mockKey, mockSessionManager);

        assertNotNull(tracker);
        assertFalse(tracker instanceof CompositeStatsTracker<?>);
        assertEquals(expectedTracker.getClass(), tracker.getClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetManualTrackerWithSizeTwoKeyArray() {
        ManualTracker mockManualTracker = mockery.mock(ManualTracker.class);
        buildExpectations(true, mockManualTracker);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "StatsKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "test2");

        final StatsSession mockSession2 = mockery.mock(StatsSession.class, "StatsSession2");
        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "StatsConfig2");
        final StatsTrackerFactory<ManualTracker> mockTrackerFactory2 = 
            mockery.mock(StatsTrackerFactory.class, "StatsTrackerFactory2");

        final ManualTracker mockManualTracker2 = mockery.mock(ManualTracker.class, "ManualTracker2");

        mockery.checking(new Expectations() {{
            allowing(mockSessionManager).getOrCreateSession(with(mockKey2)); will(returnValue(mockSession2));
            allowing(mockConfigManager).getOrCreateConfig(with(mockKey2)); will(returnValue(mockConfig2));
            allowing(mockConfig2).isEnabled(); will(returnValue(true));
            allowing(mockConfig2).getTrackerFactory(); will(returnValue(mockTrackerFactory2));
            allowing(mockTrackerFactory2).createTracker(with(mockKey2),
                                                        with(mockSessionManager));
            will(returnValue(mockManualTracker2));
        }});

        StatsTracker tracker = trackerLocator.getManualTracker(new StatsKey[] { mockKey, mockKey2 });

        assertNotNull(tracker);
        assertTrue(tracker instanceof CompositeStatsTracker<?>);

        CompositeStatsTracker<?> cTracker = (CompositeStatsTracker<?>)tracker;
        assertEquals(2, cTracker.composites().size());

        Iterator<?> itr = cTracker.composites().iterator();
        assertSame(mockManualTracker, itr.next());
        assertSame(mockManualTracker2, itr.next());
    }

}

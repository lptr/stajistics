package org.stajistics.tracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

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
import org.stajistics.session.StatsSessionManager;

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
        mockTrackerFactory = mockery.mock(StatsTrackerFactory.class);

        mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            allowing(mockConfigManager).getOrCreateConfig(with(mockKey)); will(returnValue(mockConfig));
            allowing(mockConfig).isEnabled(); will(returnValue(true));
            allowing(mockConfig).getTrackerFactory(); will(returnValue(mockTrackerFactory));
            allowing(mockTrackerFactory).createTracker(with(mockKey),
                                                       with(mockSessionManager));
            will(returnValue(mockTracker));
        }});

        trackerLocator = new DefaultStatsTrackerLocator(mockConfigManager, mockSessionManager);
    }

    @Test
    public void testGetTracker() {
        StatsTracker tracker = trackerLocator.getTracker(mockKey);

        assertNotNull(tracker);
        assertSame(mockTracker, tracker);
    }
/*
    @Test(expected = IllegalArgumentException.class)
    public void testGetTrackerWithSizeZeroArray() {
        trackerLocator.getTracker(new StatsKey[] {});
    }

    @Test
    public void testGetTrackerWithSizeOneKeyArray() {
        StatsTracker tracker = trackerLocator.getTracker(new StatsKey[] { mockKey });

        assertNotNull(tracker);
        assertFalse(tracker instanceof CompositeStatsTracker<?>);
        assertSame(mockTracker, tracker);
    }

    @Test
    public void testGetTrackerWithSizeTwoKeyArray() {
        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "StatsKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "test2");

        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "StatsConfig2");
        final StatsTrackerFactory mockTrackerFactory2 = mockery.mock(StatsTrackerFactory.class, "StatsTrackerFactory2");

        final StatsTracker mockTracker2 = mockery.mock(StatsTracker.class, "StatsTracker2");

        mockery.checking(new Expectations() {{
            allowing(mockConfigManager).getOrCreateConfig(with(mockKey2)); will(returnValue(mockConfig2));
            allowing(mockConfig2).isEnabled(); will(returnValue(true));
            allowing(mockConfig2).getTrackerFactory(); will(returnValue(mockTrackerFactory2));
            allowing(mockTrackerFactory2).createTracker(with(mockKey2),
                                                        with(mockSessionManager));
            will(returnValue(mockTracker2));
        }});

        StatsTracker tracker = trackerLocator.getTracker(new StatsKey[] { mockKey, mockKey2 });

        assertNotNull(tracker);
        assertTrue(tracker instanceof CompositeStatsTracker<?>);

        CompositeStatsTracker<?> cTracker = (CompositeStatsTracker<?>)tracker;
        assertEquals(2, cTracker.getTrackers().size());
        assertSame(mockTracker, cTracker.getTrackers().get(0));
        assertSame(mockTracker2, cTracker.getTrackers().get(1));
    }
*/
    @Test
    public void testGetTrackerWithNullKey() {
        try {
            trackerLocator.getTracker((StatsKey)null);
            fail("Allowed getTracker with null StatsKey");

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }
/*
    @Test
    public void testGetTrackerWithNullKeys() {
        try {
            trackerLocator.getTracker((StatsKey[])null);
            fail("Allowed getTracker with null StatsKey[]");

        } catch (NullPointerException npe) {
            assertEquals("keys", npe.getMessage());
        }
    }
*/
    @Test
    public void testGetTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getTracker(mockKey));
    }

    @Test
    public void testGetIncidentTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getIncidentTracker(mockKey));
    }
    
    @Test
    public void testGetManualTrackerWhenDisabled() {
        trackerLocator.setEnabled(false);

        assertEquals(NullTracker.getInstance(), trackerLocator.getManualTracker(mockKey));
    }

}

package org.stajistics;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.configuration.StatsConfigBuilder;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.tracker.TrackerLocator;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author The Stajistics Project
 */
public class StatsFactoryTest extends AbstractStajisticsTestCase {

    private StatsFactory factory;

    private StatsManager mockManager;
    private TrackerLocator mockTrackerLocator;
    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockManager = mockery.mock(StatsManager.class);
        mockTrackerLocator = mockery.mock(TrackerLocator.class);
        mockKey = mockery.mock(StatsKey.class);

        mockery.checking(new Expectations() {{
            allowing(mockManager).getTrackerLocator();
            will(returnValue(mockTrackerLocator));
        }});

        factory = new StatsFactory(mockManager);
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

        assertEquals(mockTracker, factory.track(keyName));
    }

    @Test
    public void testStartWithStatsKey() {
        final SpanTracker mockTracker = mockery.mock(SpanTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getSpanTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, factory.track(mockKey));
    }

    @Test
    public void testSpanTrackWithStatsKeys() {
        final SpanTracker mockTracker = mockery.mock(SpanTracker.class);
        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getSpanTracker(with(new StatsKey[] { mockKey, mockKey2 }));
            will(returnValue(mockTracker));
            one(mockTracker).track(); will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, factory.track(mockKey, mockKey2));
    }

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

        factory.incident(keyName);
    }

    @Test
    public void testIncidentWithStatsKey() {
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getIncidentTracker(with(mockKey)); will(returnValue(mockTracker));
            one(mockTracker).incident(); will(returnValue(mockTracker));
        }});

        factory.incident(mockKey);
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

        factory.incident(mockKey, mockKey2);
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

        factory.failure(e, keyName);
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

        factory.failure(e, mockKey);
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

        assertEquals(mockManualTracker, factory.getManualTracker(keyName));
    }

    @Test
    public void testManualWithStatsKey() {

        final ManualTracker mockManualTracker = mockery.mock(ManualTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTrackerLocator).getManualTracker(with(mockKey)); will(returnValue(mockManualTracker));
        }});

        assertSame(mockManualTracker, factory.getManualTracker(mockKey));
    }

    @Test
    public void testNewKey() {
        final String keyName = "test";
        final StatsKeyFactory mockKeyFactory = mockery.mock(StatsKeyFactory.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getKeyFactory(); will(returnValue(mockKeyFactory));
            one(mockKeyFactory).createKey(with(keyName)); will(returnValue(mockKey));
        }});

        assertSame(mockKey, factory.newKey(keyName));
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

        assertSame(mockKeyBuilder, factory.buildKey(keyName));
    }

    @Test
    public void testBuildConfig() {
        final StatsConfigBuilderFactory mockConfigBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        final StatsConfigBuilder mockConfigBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockManager).getConfigBuilderFactory(); will(returnValue(mockConfigBuilderFactory));
            one(mockConfigBuilderFactory).createConfigBuilder(); will(returnValue(mockConfigBuilder));
        }});

        assertSame(mockConfigBuilder, factory.buildConfig());
    }

}

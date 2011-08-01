package org.stajistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.bootstrap.StatsManagerFactory;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigBuilder;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.CompositeSpanTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * @author The Stajistics Project
 */
public class DefaultStatsFactoryTest extends AbstractStajisticsTestCase {

    private StatsFactory factory;

    private StatsManager mockManager;
    private StatsKey mockKey;
    private StatsConfig mockConfig;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "key1");

        mockManager = TestUtil.createMockStatsManager(mockery, mockKey);
        mockConfig = mockery.mock(StatsConfig.class);

        TestUtil.buildStatsConfigExpectations(mockery, mockKey, mockConfig);

        factory = new DefaultStatsFactory(mockManager);

        mockery.checking(new Expectations() {{
            allowing(mockKey).getName();
            will(returnValue(mockKey.getName()));
        }});
    }

    private SpanTracker expectSpanTrackerConfiguration(final StatsKey mockKey,
                                                       final StatsConfig mockConfig) {
        final SpanTracker mockTracker = mockery.mock(SpanTracker.class, "spanTracker_" + mockKey.getName());
        TestUtil.expectKeyConfiguration(mockery, mockManager, mockKey, mockConfig, mockTracker);
        return mockTracker;
    }

    private IncidentTracker expectIncidentTrackerConfiguration(final StatsKey mockKey,
                                                               final StatsConfig mockConfig) {

        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class, "incidentTracker_" + mockKey.getName());
        TestUtil.expectKeyConfiguration(mockery, mockManager, mockKey, mockConfig, mockTracker);
        return mockTracker;
    }

    private ManualTracker expectManualTrackerConfiguration(final StatsKey mockKey,
                                                           final StatsConfig mockConfig) {
        final ManualTracker mockTracker = mockery.mock(ManualTracker.class, "manualTracker_" + mockKey.getName());
        TestUtil.expectKeyConfiguration(mockery, mockManager, mockKey, mockConfig, mockTracker);
        return mockTracker;
    }

    @Test
    public void testConstructWithNullManager() {
        try {
            new DefaultStatsFactory(null);
            fail("Allowed construction of " + StatsFactory.class.getSimpleName() + " with null statsManager");
        } catch (NullPointerException npe) {
            assertEquals("statsManager", npe.getMessage());
        }
    }

    @Test
    public void testGetManager() {
        assertSame(mockManager, factory.getManager());
    }

    @Test
    public void testTrackWithKeyName() {
        final SpanTracker mockTracker = expectSpanTrackerConfiguration(mockKey, mockConfig);

        mockery.checking(new Expectations() {{
            one(mockTracker).track();
            will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, factory.track(mockKey.getName()));
    }

    @Test
    public void testStartWithStatsKey() {
        final SpanTracker mockTracker = expectSpanTrackerConfiguration(mockKey, mockConfig);

        mockery.checking(new Expectations() {{
            one(mockTracker).track();
            will(returnValue(mockTracker));
        }});

        assertEquals(mockTracker, factory.track(mockKey));
    }

    @Test
    public void testSpanTrackWithStatsKeys() {
        final SpanTracker mockTracker = expectSpanTrackerConfiguration(mockKey, mockConfig);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "key2");

        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "statsConfig2");
        TestUtil.buildStatsConfigExpectations(mockery, mockKey2, mockConfig2);
        final SpanTracker mockTracker2 = expectSpanTrackerConfiguration(mockKey2, mockConfig2);

        mockery.checking(new Expectations() {{
            one(mockTracker).track();
            will(returnValue(mockTracker));
            one(mockTracker2).track();
            will(returnValue(mockTracker2));
        }});

        SpanTracker resultTracker = factory.track(mockKey, mockKey2);
        assertEquals(CompositeSpanTracker.class, resultTracker.getClass());
        Iterator<SpanTracker> itr = ((CompositeSpanTracker)resultTracker).composites().iterator();
        assertSame(mockTracker, itr.next());
        assertSame(mockTracker2, itr.next());
    }

    @Test
    public void testIncidentWithKeyName() {
        final IncidentTracker mockTracker = expectIncidentTrackerConfiguration(mockKey, mockConfig);

        mockery.checking(new Expectations() {{
            one(mockTracker).incident();
            will(returnValue(mockTracker));
        }});

        factory.incident(mockKey.getName());
    }

    @Test
    public void testIncidentWithStatsKey() {
        final IncidentTracker mockTracker = expectIncidentTrackerConfiguration(mockKey, mockConfig);

        mockery.checking(new Expectations() {{
            one(mockTracker).incident();
            will(returnValue(mockTracker));
        }});

        factory.incident(mockKey);
    }

    @Test
    public void testIncidentWithStatsKeys() {
        final IncidentTracker mockTracker = expectIncidentTrackerConfiguration(mockKey, mockConfig);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "mockKey2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "key2");

        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "statsConfig2");
        TestUtil.buildStatsConfigExpectations(mockery, mockKey2, mockConfig2);
        final IncidentTracker mockTracker2 = expectIncidentTrackerConfiguration(mockKey2, mockConfig2);

        mockery.checking(new Expectations() {{
            one(mockTracker).incident();
            will(returnValue(mockTracker));
            one(mockTracker2).incident();
            will(returnValue(mockTracker2));
        }});

        factory.incident(mockKey, mockKey2);
    }

    @Test
    public void testFailureWithKeyName() {
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class, "incidentTracker_" + mockKey.getName());
        final StatsKeyBuilder mockKeyBuilder = TestUtil.expectKeyConfiguration(mockery, mockManager, mockKey, mockConfig, mockTracker);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "key2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "key2");
        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "config2");
        TestUtil.buildStatsConfigExpectations(mockery, mockKey2, mockConfig2);
        final IncidentTracker mockTracker2 = expectIncidentTrackerConfiguration(mockKey2, mockConfig2);

        final Exception e = new Exception();

        mockery.checking(new Expectations() {{
            one(mockKeyBuilder).withNameSuffix("exception");
            will(returnValue(mockKeyBuilder));
            
            one(mockKeyBuilder).withAttribute("threw", e.getClass().getName());
            will(returnValue(mockKeyBuilder));

            one(mockKeyBuilder).newKey();
            will(returnValue(mockKey2));

            one(mockTracker2).incident();
            will(returnValue(mockTracker2));
        }});

        factory.failure(e, mockKey.getName());
    }

    @Test
    public void testFailureWithStatsKey() {
        final IncidentTracker mockTracker = mockery.mock(IncidentTracker.class, "incidentTracker_" + mockKey.getName());
        final StatsKeyBuilder mockKeyBuilder = TestUtil.expectKeyConfiguration(mockery, mockManager, mockKey, mockConfig, mockTracker);

        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "key2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "key2");
        final StatsConfig mockConfig2 = mockery.mock(StatsConfig.class, "config2");
        TestUtil.buildStatsConfigExpectations(mockery, mockKey2, mockConfig2);
        final IncidentTracker mockTracker2 = expectIncidentTrackerConfiguration(mockKey2, mockConfig2);

        final Exception e = new Exception();

        mockery.checking(new Expectations() {{
            one(mockKeyBuilder).withNameSuffix("exception");
            will(returnValue(mockKeyBuilder));

            one(mockKeyBuilder).withAttribute("threw", e.getClass().getName());
            will(returnValue(mockKeyBuilder));

            one(mockKeyBuilder).newKey();
            will(returnValue(mockKey2));

            one(mockTracker2).incident();
            will(returnValue(mockTracker2));
        }});

        factory.failure(e, mockKey);
    }

    @Test
    public void testManualWithKeyName() {
        final ManualTracker mockTracker = expectManualTrackerConfiguration(mockKey, mockConfig);
        
        assertSame(mockTracker, factory.getManualTracker(mockKey.getName()));
    }

    @Test
    public void testManualWithStatsKey() {
        final ManualTracker mockTracker = expectManualTrackerConfiguration(mockKey, mockConfig);

        assertSame(mockTracker, factory.getManualTracker(mockKey));
    }

    @Test
    public void testNewKey() {
        mockery.checking(new Expectations() {{
            one(mockManager.getKeyFactory()).createKey(with(mockKey.getName()));
            will(returnValue(mockKey));
        }});

        assertSame(mockKey, factory.newKey(mockKey.getName()));
    }

    @Test
    public void testBuildKey() {
        StatsKeyBuilder mockKeyBuilder = TestUtil.expectKeyConfiguration(mockery, mockManager, mockKey, mockConfig, null);

        assertSame(mockKeyBuilder, factory.buildKey(mockKey.getName()));
    }

    @Test
    public void testBuildConfig() {
        final StatsConfigBuilder mockConfigBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockManager.getConfigBuilderFactory()).createConfigBuilder();
            will(returnValue(mockConfigBuilder));
        }});

        assertSame(mockConfigBuilder, factory.buildConfig());
    }

    public static final class ClassLoadableMockStatsManagerFactory implements StatsManagerFactory {
        @Override
        public StatsManager createManager(String namespace) {
            return null;
        }
    }
}

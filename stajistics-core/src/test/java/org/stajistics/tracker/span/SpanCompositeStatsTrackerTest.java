package org.stajistics.tracker.span;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.tracker.AbstractCompositeStatsTrackerTestCase;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class SpanCompositeStatsTrackerTest extends AbstractCompositeStatsTrackerTestCase<SpanTracker> {

    @Override
    protected SpanCompositeStatsTracker createCompositeStatsTracker() {
        return new SpanCompositeStatsTracker(mockTrackers);
    }

    @Override
    protected SpanTracker[] createMockTrackers() {
        return new SpanTracker[] {
            mockery.mock(SpanTracker.class, "SpanStatsTracker1"),
            mockery.mock(SpanTracker.class, "SpanStatsTracker2"),
            mockery.mock(SpanTracker.class, "SpanStatsTracker3")
        };
    }


    @Test
    public void testIsTracking() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).isTracking(); will(returnValue(false));
            one(mockTrackers[1]).isTracking(); will(returnValue(true));
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker();

        assertTrue(cTracker.isTracking());
    }

    @Test
    public void testStart() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).start();
            one(mockTrackers[1]).start();
            one(mockTrackers[2]).start();
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker();
        cTracker.start();
    }

    @Test
    public void testStop() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).stop();
            one(mockTrackers[1]).stop();
            one(mockTrackers[2]).stop();
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker();
        cTracker.stop();
    }

}

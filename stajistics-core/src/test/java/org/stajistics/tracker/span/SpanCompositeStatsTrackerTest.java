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
package org.stajistics.tracker.span;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
    protected SpanCompositeStatsTracker createCompositeStatsTracker(final List<SpanTracker> mockTrackers) {
        return new SpanCompositeStatsTracker(mockTrackers);
    }

    @Override
    protected SpanCompositeStatsTracker createCompositeStatsTracker(final SpanTracker[] mockTrackers) {
        return new SpanCompositeStatsTracker(mockTrackers);
    }

    @Override
    protected SpanTracker[] createMockTrackers() {
        return new SpanTracker[] {
            mockery.mock(SpanTracker.class, "SpanTracker1"),
            mockery.mock(SpanTracker.class, "SpanTracker2"),
            mockery.mock(SpanTracker.class, "SpanTracker3")
        };
    }

    @Test
    public void testIsTracking1() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).isTracking(); will(returnValue(false));
            one(mockTrackers[1]).isTracking(); will(returnValue(true));
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);

        assertTrue(cTracker.isTracking());
    }

    @Test
    public void testIsTracking2() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).isTracking(); will(returnValue(false));
            one(mockTrackers[1]).isTracking(); will(returnValue(false));
            one(mockTrackers[2]).isTracking(); will(returnValue(false));
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);

        assertFalse(cTracker.isTracking());
    }

    @Test
    public void testStart() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).track();
            one(mockTrackers[1]).track();
            one(mockTrackers[2]).track();
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.track();
    }

    @Test
    public void testStop() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).commit();
            one(mockTrackers[1]).commit();
            one(mockTrackers[2]).commit();
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.commit();
    }

    @Test
    public void testGetStartTime() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).getStartTime(); will(returnValue(2L));
            one(mockTrackers[1]).getStartTime(); will(returnValue(3L));
            one(mockTrackers[2]).getStartTime(); will(returnValue(1L));
        }});

        SpanCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        assertEquals(1L, cTracker.getStartTime());
    }
}

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
package org.stajistics.tracker;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class CompositeStatsTrackerTest {

    private Mockery mockery;
    private StatsTracker[] mockTrackers;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockTrackers = new StatsTracker[] {
           mockery.mock(StatsTracker.class, "StatsTracker1"),
           mockery.mock(StatsTracker.class, "StatsTracker2"),
           mockery.mock(StatsTracker.class, "StatsTracker3")
       };
    }

    @Test
    public void testConstructWithNullTrackerArray() {
        try {
            new CompositeStatsTracker((StatsTracker[])null);
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testConstructWithNullTrackerList() {
        try {
            new CompositeStatsTracker((List<StatsTracker>)null);
        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testConstructWithEmptyTrackerArray() {
        try {
            new CompositeStatsTracker();
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test
    public void testConstructWithEmptyTrackerList() {
        try {
            new CompositeStatsTracker(Collections.<StatsTracker>emptyList());
        } catch (IllegalArgumentException iae) {
            // expected
        }
    }

    @Test
    public void testGetTrackersWhenConstructedWithArray() {
        CompositeStatsTracker cTracker = new CompositeStatsTracker(mockTrackers);

        int i = 0;
        for (StatsTracker tracker : cTracker.getTrackers()) {
            assertSame(mockTrackers[i++], tracker);
        }
    }

    @Test
    public void testGetTrackersWhenConstructedWithList() {

        CompositeStatsTracker cTracker = new CompositeStatsTracker(Arrays.asList(mockTrackers));

        int i = 0;
        for (StatsTracker tracker : cTracker.getTrackers()) {
            assertSame(mockTrackers[i++], tracker);
        }
    }

    @Test
    public void testIsTracking() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).isTracking(); will(returnValue(false));
            one(mockTrackers[1]).isTracking(); will(returnValue(true));
        }});

        CompositeStatsTracker cTracker = new CompositeStatsTracker(mockTrackers);

        assertTrue(cTracker.isTracking());
    }

    @Test
    public void testTrack() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).track();
            one(mockTrackers[1]).track();
            one(mockTrackers[2]).track();
        }});

        CompositeStatsTracker cTracker = new CompositeStatsTracker(mockTrackers);
        cTracker.track();
    }

    @Test
    public void testCommit() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).commit();
            one(mockTrackers[1]).commit();
            one(mockTrackers[2]).commit();
        }});

        CompositeStatsTracker cTracker = new CompositeStatsTracker(mockTrackers);
        cTracker.commit();
    }

    @Test
    public void testReset() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).reset();
            one(mockTrackers[1]).reset();
            one(mockTrackers[2]).reset();
        }});

        CompositeStatsTracker cTracker = new CompositeStatsTracker(mockTrackers);
        cTracker.reset();
    }

}

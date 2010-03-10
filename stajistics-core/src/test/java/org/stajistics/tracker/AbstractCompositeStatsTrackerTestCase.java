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
import static org.junit.Assert.assertSame;

import java.util.Collection;
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
public abstract class AbstractCompositeStatsTrackerTestCase<T extends StatsTracker> {

    protected Mockery mockery;
    protected T[] mockTrackers;

    protected abstract T[] createMockTrackers();

    protected abstract CompositeStatsTracker<T> createCompositeStatsTracker(T[] mockTrackers);

    protected abstract CompositeStatsTracker<T> createCompositeStatsTracker(List<T> mockTrackers);

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockTrackers = createMockTrackers();
    }

    @Test(expected = NullPointerException.class)
    public void testConstructWithNullTrackerArray() {
        createCompositeStatsTracker((T[])null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructWithNullTrackerList() {
        createCompositeStatsTracker((List<T>)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructWithEmptyTrackerList() {
        createCompositeStatsTracker(Collections.<T>emptyList());
    }

    @Test
    public void testGetTrackersWhenConstructedWithArray() {
        CompositeStatsTracker<T> cTracker = createCompositeStatsTracker(mockTrackers);

        Collection<T> trackers = cTracker.composites();

        int i = 0;
        for (StatsTracker tracker : trackers) {
            assertSame(mockTrackers[i++], tracker);
        }

        assertEquals(mockTrackers.length, trackers.size());
    }

    @Test
    public void testReset() {
        mockery.checking(new Expectations() {{
            one(mockTrackers[0]).reset();
            one(mockTrackers[1]).reset();
            one(mockTrackers[2]).reset();
        }});

        CompositeStatsTracker<T> cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.reset();
    }

}

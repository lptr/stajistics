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
package org.stajistics.session;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ConcurrentStatsSessionTest {

    protected static final double DELTA = 0.0000000000001;

    private Mockery mockery;
    private StatsKey mockKey;
    private StatsTracker mockTracker;

    private StatsSession session;

    @Before
    public void setUp() {
        mockery = new Mockery();

        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        mockTracker = mockery.mock(StatsTracker.class);

        session = new ConcurrentStatsSession(mockKey);
    }

    @Test
    public void testConstructWithNullKey() {
        try {
            new ConcurrentStatsSession(null, (List<DataRecorder>)null);

        } catch (NullPointerException npe) {
            // expected
        }
    }

    @Test
    public void testInitialData() {
        assertEquals(0, session.getHits());
        assertEquals(-1, session.getFirstHitStamp());
        assertEquals(-1, session.getLastHitStamp());
        assertEquals(0, session.getCommits());
        assertEquals(Double.NaN, session.getFirst(), DELTA);
        assertEquals(Double.POSITIVE_INFINITY, session.getMin(), DELTA);
        assertEquals(Double.NEGATIVE_INFINITY, session.getMax(), DELTA);
        assertEquals(Double.NaN, session.getLast(), DELTA);
        assertEquals(0, session.getSum(), DELTA);
    }

    @Test
    public void testTrack() {
        final long firstNow = System.currentTimeMillis();

        session.track(mockTracker, firstNow);

        assertEquals(1, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(firstNow, session.getLastHitStamp());
        assertEquals(0, session.getCommits());
        assertEquals(Double.NaN, session.getFirst(), DELTA);
        assertEquals(Double.POSITIVE_INFINITY, session.getMin(), DELTA);
        assertEquals(Double.NEGATIVE_INFINITY, session.getMax(), DELTA);
        assertEquals(Double.NaN, session.getLast(), DELTA);
        assertEquals(0, session.getSum(), DELTA);

        final long secondNow = System.currentTimeMillis();

        session.track(mockTracker, secondNow);

        assertEquals(2, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(secondNow, session.getLastHitStamp());
        assertEquals(0, session.getCommits());
        assertEquals(Double.NaN, session.getFirst(), DELTA);
        assertEquals(Double.POSITIVE_INFINITY, session.getMin(), DELTA);
        assertEquals(Double.NEGATIVE_INFINITY, session.getMax(), DELTA);
        assertEquals(Double.NaN, session.getLast(), DELTA);
        assertEquals(0, session.getSum(), DELTA);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testUpdate() {
        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(1.0));
        }});

        long now = System.currentTimeMillis();

        session.update(mockTracker, now);

        assertEquals(1, session.getCommits());
        assertEquals(1, session.getFirst(), DELTA);
        assertEquals(1, session.getMin(), DELTA);
        assertEquals(1, session.getMax(), DELTA);
        assertEquals(1, session.getLast(), DELTA);
        assertEquals(1, session.getSum(), DELTA);

        mockery.assertIsSatisfied();

        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(2.0));
        }});

        now = System.currentTimeMillis();

        session.update(mockTracker, now);

        assertEquals(2, session.getCommits());
        assertEquals(1, session.getFirst(), DELTA);
        assertEquals(1, session.getMin(), DELTA);
        assertEquals(2, session.getMax(), DELTA);
        assertEquals(2, session.getLast(), DELTA);
        assertEquals(3, session.getSum(), DELTA);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testTrackAndUpdate() {
        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(2.0));
        }});

        final long firstNow = System.currentTimeMillis();

        session.track(mockTracker, firstNow);
        session.update(mockTracker, firstNow);

        assertEquals(1, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(firstNow, session.getLastHitStamp());
        assertEquals(1, session.getCommits());
        assertEquals(2, session.getFirst(), DELTA);
        assertEquals(2, session.getMin(), DELTA);
        assertEquals(2, session.getMax(), DELTA);
        assertEquals(2, session.getLast(), DELTA);
        assertEquals(2, session.getSum(), DELTA);

        mockery.assertIsSatisfied();

        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(4.0));
        }});

        final long secondNow = System.currentTimeMillis();

        session.track(mockTracker, secondNow);
        session.update(mockTracker, secondNow);

        assertEquals(2, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(secondNow, session.getLastHitStamp());
        assertEquals(2, session.getCommits());
        assertEquals(2, session.getFirst(), DELTA);
        assertEquals(2, session.getMin(), DELTA);
        assertEquals(4, session.getMax(), DELTA);
        assertEquals(4, session.getLast(), DELTA);
        assertEquals(6, session.getSum(), DELTA);

        mockery.assertIsSatisfied();
    }
    
    
}

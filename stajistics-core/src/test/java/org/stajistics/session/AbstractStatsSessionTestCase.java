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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.data.DataSet;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public abstract class AbstractStatsSessionTestCase {

    protected static final double DELTA = 0.0000000000001;

    protected Mockery mockery;
    protected StatsKey mockKey;
    protected StatsTracker mockTracker;
    protected StatsEventManager mockEventManager;

    protected StatsSession session;

    @Before
    public void setUp() {
        mockery = new Mockery();

        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        mockTracker = mockery.mock(StatsTracker.class);
        mockEventManager = mockery.mock(StatsEventManager.class);

        initMocks();

        session = createStatsSession();
    }

    protected void initMocks() {}

    protected StatsSession createStatsSession() {
        return new ConcurrentStatsSession(mockKey, mockEventManager);
    }

    @Test
    public void testConstructWithNullKey() {
        try {
            new ConcurrentStatsSession(null, mockEventManager, (DataRecorder[])null);

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new ConcurrentStatsSession(mockKey, null, (DataRecorder[])null);

        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
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
    public void testInitialDataEqualsClearedData() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        long hits = session.getHits();
        long firstHitStamp = session.getFirstHitStamp();
        long lastHitStamp  = session.getLastHitStamp();
        long commits = session.getCommits();
        double first = session.getFirst();
        double min = session.getMin();
        double max = session.getMax();
        double last = session.getLast();
        double sum = session.getSum();

        session.clear();

        assertEquals(hits, session.getHits());
        assertEquals(firstHitStamp, session.getFirstHitStamp());
        assertEquals(lastHitStamp, session.getLastHitStamp());
        assertEquals(commits, session.getCommits());
        assertEquals(first, session.getFirst(), DELTA);
        assertEquals(min, session.getMin(), DELTA);
        assertEquals(max, session.getMax(), DELTA);
        assertEquals(last, session.getLast(), DELTA);
        assertEquals(sum, session.getSum(), DELTA);
    }

    @Test
    public void testClearFiresSessionClearedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(StatsEventType.SESSION_CLEARED),
                                            with(mockKey),
                                            with(session));
        }});

        session.clear();
    }

    @Test
    public void testTrack() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

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
    }

    @Test
    public void testTrackFiresTrackerTrackingEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(StatsEventType.TRACKER_TRACKING),
                                            with(mockKey),
                                            with(mockTracker));
        }});

        session.track(mockTracker, 0L);
    }

    @Test
    public void testUpdate() {
        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(1.0));
            ignoring(mockEventManager);
        }});

        long now = System.currentTimeMillis();

        session.update(mockTracker, now);

        assertEquals(1, session.getCommits());
        assertEquals(1, session.getFirst(), DELTA);
        assertEquals(1, session.getMin(), DELTA);
        assertEquals(1, session.getMax(), DELTA);
        assertEquals(1, session.getLast(), DELTA);
        assertEquals(1, session.getSum(), DELTA);

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
    }

    @Test
    public void testUpdateFiresTrackerCommittedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(StatsEventType.TRACKER_COMMITTED),
                                            with(mockKey),
                                            with(mockTracker));
            ignoring(mockTracker).getValue(); will(returnValue(1.0));
        }});

        session.update(mockTracker, 0L);
    }

    @Test
    public void testTrackAndUpdate() {
        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(2.0));
            ignoring(mockEventManager);
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
    }

    @Test
    public void testRestore() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        DataSet dataSet = session.collectData();

        StatsSession anotherSession = createStatsSession();

        anotherSession.restore(dataSet);

        assertEquals(session.getHits(), anotherSession.getHits());
        assertEquals(session.getFirstHitStamp(), anotherSession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), anotherSession.getLastHitStamp());
        assertEquals(session.getCommits(), anotherSession.getCommits());
        assertEquals(session.getFirst(), anotherSession.getFirst(), DELTA);
        assertEquals(session.getMin(), anotherSession.getMin(), DELTA);
        assertEquals(session.getMax(), anotherSession.getMax(), DELTA);
        assertEquals(session.getLast(), anotherSession.getLast(), DELTA);
        assertEquals(session.getSum(), anotherSession.getSum(), DELTA);
    }

}

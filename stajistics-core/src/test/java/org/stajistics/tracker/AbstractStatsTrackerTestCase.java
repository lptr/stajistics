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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.session.StatsSession;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsTrackerTestCase {

    protected StatsSession mockSession;

    protected Mockery mockery;

    protected abstract StatsTracker createStatsTracker();

    @Before
    public void setUp() throws Exception {
        mockery = new Mockery();
        mockSession = mockery.mock(StatsSession.class);
    }

    @Test
    public void testCreate() {
        final StatsTracker tracker = createStatsTracker();

        assertEquals(mockSession, tracker.getSession());
        assertEquals(0, tracker.getValue(), 0);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testTrack() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).open(with(tracker), with(any(long.class)));
        }});

        tracker.track();

        assertEquals(0, tracker.getValue(), 0);

        mockery.assertIsSatisfied();
    }

    @Test
    public void testCommit() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).open(with(tracker), with(any(long.class)));
            one(mockSession).update(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getCommits();
        }});

        tracker.track();
        tracker.commit();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetTimeStamp() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).open(with(tracker), with(any(long.class)));
            one(mockSession).update(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getCommits();
        }});

        assertEquals(0, tracker.getTimeStamp());

        tracker.track();

        long timeStamp = tracker.getTimeStamp();

        assertTrue(System.currentTimeMillis() >= timeStamp);

        tracker.commit();

        assertEquals(timeStamp, tracker.getTimeStamp());
        
        mockery.assertIsSatisfied();
    }
    
    @Test
    public void testIsTracking() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).open(with(tracker), with(any(long.class)));
            one(mockSession).update(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getCommits();
        }});

        assertFalse(tracker.isTracking());

        tracker.track();

        assertTrue(tracker.isTracking());

        tracker.commit();

        assertFalse(tracker.isTracking());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testResetBeforeTracking() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession).open(with(tracker), with(any(long.class)));
            ignoring(mockSession).update(with(tracker), with(any(long.class)));
        }});

        boolean isTracking = tracker.isTracking();
        double value = tracker.getValue();
        long timeStamp = tracker.getTimeStamp();

        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), 0.0000000001);
        assertEquals(timeStamp, tracker.getTimeStamp());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testResetWhileTracking() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession).open(with(tracker), with(any(long.class)));
            ignoring(mockSession).update(with(tracker), with(any(long.class)));
        }});

        boolean isTracking = tracker.isTracking();
        double value = tracker.getValue();
        long timeStamp = tracker.getTimeStamp();

        tracker.track();

        assertTrue(isTracking != tracker.isTracking());
        assertTrue(timeStamp != tracker.getTimeStamp());

        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), 0.0000000001);
        assertEquals(timeStamp, tracker.getTimeStamp());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testResetAfterCommit() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession).open(with(tracker), with(any(long.class)));
            ignoring(mockSession).update(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getCommits();
        }});

        boolean isTracking = tracker.isTracking();
        double value = tracker.getValue();
        long timeStamp = tracker.getTimeStamp();

        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), 0.0000000001);
        assertEquals(timeStamp, tracker.getTimeStamp());

        tracker.track();

        assertTrue(isTracking != tracker.isTracking());
        assertTrue(timeStamp != tracker.getTimeStamp());

        tracker.commit();
        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), 0.0000000001);
        assertEquals(timeStamp, tracker.getTimeStamp());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testCommitWithoutOpen() {

        final StatsTracker tracker = createStatsTracker();

        // Expecting no calls to the session

        try {
            tracker.commit();
            fail("Allowed unopened commit");
        } catch (IllegalStateException ise) {
            // Success
        }

        mockery.assertIsSatisfied();
    }

    @Test
    public void testOpenTwice() {
        final StatsTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).open(with(tracker), with(any(long.class)));
        }});

        tracker.track();

        try {
            tracker.track();
            fail("Allowed second open without commit");
        } catch (IllegalStateException ise) {
            // Success
        }

        mockery.assertIsSatisfied();
    }
}

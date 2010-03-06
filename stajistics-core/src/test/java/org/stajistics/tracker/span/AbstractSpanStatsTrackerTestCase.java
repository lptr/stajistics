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
package org.stajistics.tracker.span;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.TestUtil;
import org.stajistics.tracker.AbstractStatsTrackerTestCase;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractSpanStatsTrackerTestCase 
    extends AbstractStatsTrackerTestCase<SpanTracker> {

    @Test
    public void testStart() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).track(with(tracker), with(any(long.class)));
            ignoring(mockSession).getLastHitStamp();
        }});

        tracker.track();

        assertEquals(0, tracker.getValue(), 0);
    }

    @Test
    public void testStartStop() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).track(with(tracker), with(any(long.class)));
            between(0, 1).of(mockSession).update(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getLastHitStamp();
            ignoring(mockSession).getCommits();
        }});

        tracker.track();
        tracker.commit();
    }

    @Test
    public void testGetStartTime() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession);
        }});

        assertEquals(0, tracker.getStartTime());

        tracker.track();

        long timeStamp = tracker.getStartTime();

        assertTrue(System.currentTimeMillis() >= timeStamp);

        tracker.commit();

        assertEquals(timeStamp, tracker.getStartTime());
    }

    @Test
    public void testIsTracking() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession);
        }});

        assertFalse(tracker.isTracking());

        tracker.track();

        assertTrue(tracker.isTracking());

        tracker.commit();

        assertFalse(tracker.isTracking());
    }

    @Test
    public void testResetBeforeTracking() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession).track(with(tracker), with(any(long.class)));
            ignoring(mockSession).update(with(tracker), with(any(long.class)));
        }});

        boolean isTracking = tracker.isTracking();
        double value = tracker.getValue();
        long timeStamp = tracker.getStartTime();

        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), TestUtil.DELTA);
        assertEquals(timeStamp, tracker.getStartTime());
    }

    @Test
    public void testResetWhileTracking() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession);
        }});

        boolean isTracking = tracker.isTracking();
        double value = tracker.getValue();
        long timeStamp = tracker.getStartTime();

        tracker.track();

        assertTrue(isTracking != tracker.isTracking());
        assertTrue(timeStamp != tracker.getStartTime());

        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), TestUtil.DELTA);
        assertEquals(timeStamp, tracker.getStartTime());
    }

    @Test
    public void testResetAfterStop() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            ignoring(mockSession);
        }});

        boolean isTracking = tracker.isTracking();
        double value = tracker.getValue();
        long timeStamp = tracker.getStartTime();

        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), TestUtil.DELTA);
        assertEquals(timeStamp, tracker.getStartTime());

        tracker.track();

        assertTrue(isTracking != tracker.isTracking());
        assertTrue(timeStamp != tracker.getStartTime());

        tracker.commit();
        tracker.reset();

        assertEquals(isTracking, tracker.isTracking());
        assertEquals(value, tracker.getValue(), TestUtil.DELTA);
        assertEquals(timeStamp, tracker.getStartTime());
    }

    @Test
    public void testStartWithoutStop() {

        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).track(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getLastHitStamp();
            ignoring(mockSession).getCommits();
        }});

        assertFalse(tracker.isTracking());
        tracker.track();
        assertTrue(tracker.isTracking());
    }

    @Test
    public void testStartTwice() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            one(mockSession).track(with(tracker), with(any(long.class)));
            ignoring(mockSession).getHits();
            ignoring(mockSession).getLastHitStamp();
            ignoring(mockSession).getCommits();
        }});

        tracker.track();

        assertTrue(tracker.isTracking());

        long start = tracker.getStartTime();
        tracker.track();

        assertTrue(tracker.isTracking());
        assertEquals(start, tracker.getStartTime());
    }

    @Test
    public void testTrackEatsSessionException() {
        final SpanTracker tracker = createStatsTracker(new NastySession());

        tracker.track();
    }

    @Test
    public void testCommitEatsSessionException() {
        final SpanTracker tracker = createStatsTracker(new NastySession());

        tracker.commit();
    }
}

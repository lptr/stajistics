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
package org.stajistics.tracker.manual;

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.TestUtil;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.AbstractTrackerTestCase;

import static org.junit.Assert.assertEquals;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultManualTrackerTest extends AbstractTrackerTestCase<ManualTracker> {

    @Override
    protected DefaultManualTracker createStatsTracker(final StatsSession session) {
        return new DefaultManualTracker(session);
    }

    @Test
    public void testAddValue() {
        DefaultManualTracker mTracker = createStatsTracker(mockSession);

        int total = 0;

        for (int i = 0; i < 100; i++) {
            mTracker.addValue(i);
            total += i;

            assertEquals(total, mTracker.getValue(), TestUtil.DELTA);
        }
    }

    @Test
    public void testSetValue() {
        DefaultManualTracker mTracker = createStatsTracker(mockSession);

        for (int i = 0; i < 100; i++) {
            mTracker.setValue(i);

            assertEquals(i, mTracker.getValue(), TestUtil.DELTA);
        }
    }

    @Test
    public void testReset() {
        final ManualTracker tracker = createStatsTracker(mockSession);

        tracker.setValue(3);

        assertEquals(3.0, tracker.getValue(), TestUtil.DELTA);

        tracker.reset();

        assertEquals(0.0, tracker.getValue(), TestUtil.DELTA);
    }

    @Test
    public void testCommit() {
        final ManualTracker tracker = createStatsTracker(mockSession);

        mockery.checking(new Expectations() {{
            one(mockSession).track(with(tracker), with(any(long.class)));
            one(mockSession).update(with(tracker), with(any(long.class)));
        }});

        tracker.commit();
    }

    /**
     * {@link ManualTracker#addValue(double)} doesn't touch the session, but still a good test.
     */
    @Test
    public void testAddValueEatsSessionException() {
        final ManualTracker tracker = createStatsTracker(new NastySession());

        tracker.addValue(1);
    }

    /**
     * {@link ManualTracker#setValue(double)} doesn't touch the session, but still a good test.
     */
    @Test
    public void testSetValueEatsSessionException() {
        final ManualTracker tracker = createStatsTracker(new NastySession());

        tracker.setValue(1);
    }

    @Test
    public void testCommitEatsSessionException() {
        final ManualTracker tracker = createStatsTracker(new NastySession());

        tracker.commit();
    }
}

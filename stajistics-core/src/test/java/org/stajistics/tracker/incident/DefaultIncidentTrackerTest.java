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

package org.stajistics.tracker.incident;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.Stats;
import org.stajistics.TestUtil;
import org.stajistics.UncaughtExceptionHandler;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.AbstractTrackerTestCase;

/**
 *
 * @author The Stajistics Project
 *
 */
public class DefaultIncidentTrackerTest extends AbstractTrackerTestCase<IncidentTracker> {

    @Override
    protected IncidentTracker createStatsTracker(final StatsSession session) {
        return new DefaultIncidentTracker(session);
    }

    @Test
    public void testIncident() {
        final IncidentTracker tracker = new DefaultIncidentTracker(mockSession);

        mockery.checking(new Expectations() {{
            exactly(2).of(mockSession).track(with(tracker), with(any(long.class)));
            exactly(2).of(mockSession).update(with(tracker), with(any(long.class)));
        }});

        tracker.incident();

        assertEquals(1.0, tracker.getValue(), TestUtil.DELTA);

        tracker.incident();

        assertEquals(1.0, tracker.getValue(), TestUtil.DELTA);
    }

    @Test
    public void testReset() {
        final IncidentTracker tracker = new DefaultIncidentTracker(mockSession);

        mockery.checking(new Expectations() {{
            one(mockSession).track(with(tracker), with(any(long.class)));
            one(mockSession).update(with(tracker), with(any(long.class)));
        }});

        tracker.incident();

        assertEquals(1.0, tracker.getValue(), TestUtil.DELTA);

        tracker.reset();

        assertEquals(0.0, tracker.getValue(), TestUtil.DELTA);
    }

    @Test
    public void testIncidentEatsSessionException() {
        final UncaughtExceptionHandler mockUncaughtExceptionHandler = mockery.mock(UncaughtExceptionHandler.class);
        mockery.checking(new Expectations() {{
            one(mockUncaughtExceptionHandler).uncaughtException(with(mockKey),
                                                                with(aNonNull(RuntimeException.class)));
        }});

        Stats.getManager().setUncaughtExceptionHandler(mockUncaughtExceptionHandler);
        try {
            final IncidentTracker tracker = createStatsTracker(new NastySession(mockKey));

            tracker.incident();
        } finally {
            Stats.getManager().setUncaughtExceptionHandler(null);
        }
    }
}

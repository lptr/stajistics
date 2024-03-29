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

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.TrackerFactory;

/**
 * A common base class for time duration trackers. The class chooses a
 * {@link #FACTORY} that provides the most precise measurements on the current
 * JVM. If <code>sun.misc.Perf</code> is available, the
 * {@link PerfTimeDurationTracker#FACTORY} is chosen, otherwise the
 * {@link NanoTimeDurationTracker#FACTORY} is chosen.
 *
 * @author The Stajistics Project
 */
public abstract class TimeDurationTracker extends AbstractSpanTracker {

    public static final TrackerFactory<SpanTracker> FACTORY;

    static {
        TrackerFactory<SpanTracker> trackerFactory = NanoTimeDurationTracker.FACTORY;

        try {
            Class.forName("sun.misc.Perf");

            /* Note: When trackerFactory is assigned to PerfTimeDurationTracker.FACTORY 
             * on the line below, for some reason it returns null when running unit tests 
             * from maven. How that is even possible, I have no idea.
             */
            trackerFactory = new PerfTimeDurationTracker.Factory();

        } catch (ClassNotFoundException ex) {
            // Ignore

        } finally {
            FACTORY = trackerFactory;
        }
    }

    public TimeDurationTracker(final StatsSession session) {
        super(session);
    }
}

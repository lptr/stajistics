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

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

import sun.misc.Perf;

/**
 * A tracker that tracks time duration with high precision. It uses
 * <code>sun.misc.Perf</code>, a high performance time measurement service. 
 * The actual time is calculated as <code>(endTicks - startTicks) * 1000 / frequency</code>. 
 * The value is stored as a fraction of milliseconds.
 *
 * <p>
 * <b>Note:</b> This class uses proprietary Sun APIs, therefore it is not
 * guaranteed to work with future versions of the Sun VM, or other VMs. The safest way 
 * to use this tracker is to create it using {@link TimeDurationTracker#FACTORY} 
 * which safely selects the most precise method of time duration measurement available.
 *
 * @author The Stajistics Project
 */
@SuppressWarnings("restriction")
public class PerfTimeDurationTracker extends TimeDurationTracker {

    private static final long serialVersionUID = 9134751181550384765L;

    private static final Perf PERF = Perf.getPerf();

    private static final double CONVERSION = 1e3 / PERF.highResFrequency();

    public static final Factory FACTORY = new Factory();

    private long startTicks;

    public PerfTimeDurationTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void startImpl(final long now) {
        startTicks = PERF.highResCounter();

        super.startImpl(now);
    }

    @Override
    protected void stopImpl(final long now) {
        long endTicks = PERF.highResCounter();
        value = (endTicks - startTicks) * CONVERSION;

        session.update(this, now);
    }

    public static class Factory extends AbstractSpanStatsTrackerFactory {

        private static final long serialVersionUID = 6891282563577243942L;

        @Override
        public SpanTracker createTracker(final StatsKey key,
                                         final StatsSessionManager sessionManager) {
            return new PerfTimeDurationTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

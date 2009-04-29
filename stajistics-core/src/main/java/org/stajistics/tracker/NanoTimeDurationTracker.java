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

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * A tracker that tracks time duration with nanosecond precision (but not nanosecond accuracy).
 * The value is stored as a fraction of milliseconds.
 *
 * @see System#nanoTime()
 *
 * @author The Stajistics Project
 */
public class NanoTimeDurationTracker extends AbstractStatsTracker {

    public static final StatsTrackerFactory FACTORY = new Factory();

    private long nanoTime;

    public NanoTimeDurationTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void trackImpl(final long now) {
        nanoTime = System.nanoTime();

        super.trackImpl(now);
    }

    @Override
    protected void commitImpl() {
        value = (System.nanoTime() - nanoTime) / 1000000d;

        session.update(this, -1);
    }

    public static class Factory implements StatsTrackerFactory {
        @Override
        public StatsTracker createTracker(final StatsKey key,
                                          final StatsSessionManager sessionManager) {
            return new NanoTimeDurationTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

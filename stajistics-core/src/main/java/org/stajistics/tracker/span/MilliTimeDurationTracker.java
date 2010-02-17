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

/**
 * A tracker that tracks time duration with millisecond precision 
 * (but not necessarily millisecond accuracy). The value is stored as milliseconds.
 *
 * <p>
 * The advantage of using this less-precise time duration tracker is that 
 * {@link System#currentTimeMillis()} called upon {@link #commit()} is re-used by the 
 * {@link StatsSession}.
 *
 * @see System#currentTimeMillis()
 *
 * @author The Stajistics Project
 */
public class MilliTimeDurationTracker extends AbstractSpanStatsTracker {

    private static final long serialVersionUID = 4156520024679062924L;

    public static final Factory FACTORY = new Factory();

    public MilliTimeDurationTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void startImpl(long now) {
        if (now < 0) {
            now = System.currentTimeMillis();
        }

        value = now - startTime;

        session.track(this, now);
    }

    public static class Factory extends AbstractSpanStatsTrackerFactory {

        private static final long serialVersionUID = -3375825127543236566L;

        @Override
        public SpanTracker createTracker(final StatsKey key,
                                         final StatsSessionManager sessionManager) {
            return new MilliTimeDurationTracker(sessionManager.getOrCreateSession(key));
        }
    }

}

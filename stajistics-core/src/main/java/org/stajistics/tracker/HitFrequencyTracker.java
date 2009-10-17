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
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class HitFrequencyTracker extends AbstractStatsTracker {

    private static final long serialVersionUID = -1521973662156082666L;

    public static final StatsTrackerFactory FACTORY = new Factory();

    private long lastHitStamp;

    public HitFrequencyTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void trackImpl(final long now) {
        lastHitStamp = session.getLastHitStamp();

        super.trackImpl(now);
    }

    @Override
    protected void commitImpl(final long now) {
        if (lastHitStamp != 0) {
            value = timeStamp - lastHitStamp;

            session.update(this, now);
        }
    }

    @Override
    public StatsTracker reset() {
        lastHitStamp = 0;
        return super.reset();
    }

    public static class Factory implements StatsTrackerFactory {

        private static final long serialVersionUID = -8890462896053927987L;

        @Override
        public StatsTracker createTracker(final StatsKey key,
                                          final StatsSessionManager sessionManager) {
            return new HitFrequencyTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

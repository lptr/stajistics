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

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.Tracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class HitFrequencyTracker extends AbstractSpanTracker {

    public static final Factory FACTORY = new Factory();

    private long lastHitStamp = -1;

    public HitFrequencyTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void startImpl(final long now) {
        lastHitStamp = session.getLastHitStamp();

        super.startImpl(now);
    }

    @Override
    protected void stopImpl(final long now) {
        if (lastHitStamp > 0) {
            value = startTime - lastHitStamp;

            session.update(this, now);
        }
    }

    @Override
    public Tracker reset() {
        super.reset();
        lastHitStamp = -1;

        return this;
    }

    public static class Factory extends AbstractSpanTrackerFactory {

        @Override
        public SpanTracker createTracker(final StatsKey key,
                                         final StatsSessionManager sessionManager) {
            return new HitFrequencyTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

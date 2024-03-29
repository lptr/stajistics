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

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ConcurrentAccessTracker extends AbstractSpanTracker {

    public static final Factory FACTORY = new Factory();

    public ConcurrentAccessTracker(final StatsSession statsSession) {
        super(statsSession);
    }

    @Override
    protected void stopImpl(final long now) {
        value = session.getHits() - session.getCommits();

        session.update(this, now);
    }

    public static class Factory extends AbstractSpanTrackerFactory {

        @Override
        public SpanTracker createTracker(final StatsKey key, 
                                         final StatsSessionManager sessionManager) {
            return new ConcurrentAccessTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

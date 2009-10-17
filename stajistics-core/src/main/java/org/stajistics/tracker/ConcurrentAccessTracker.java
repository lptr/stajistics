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
public class ConcurrentAccessTracker extends AbstractStatsTracker {

    private static final long serialVersionUID = -7660432341032497747L;

    public static final StatsTrackerFactory FACTORY = new Factory();

    public ConcurrentAccessTracker(final StatsSession statsSession) {
        super(statsSession);
    }

    @Override
    protected void commitImpl(final long now) {
        value = session.getHits() - session.getCommits();

        session.update(this, now);
    }

    public static class Factory implements StatsTrackerFactory {

        private static final long serialVersionUID = -5572631384426151379L;

        @Override
        public StatsTracker createTracker(final StatsKey key, 
                                          final StatsSessionManager sessionManager) {
            return new ConcurrentAccessTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

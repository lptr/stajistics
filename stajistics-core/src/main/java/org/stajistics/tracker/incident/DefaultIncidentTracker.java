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
package org.stajistics.tracker.incident;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.AbstractStatsTracker;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultIncidentTracker extends AbstractStatsTracker implements IncidentTracker {

    private static final long serialVersionUID = -2931781680123245941L;

    public static final Factory FACTORY = new Factory();

    public DefaultIncidentTracker(final StatsSession session) {
        super(session);
    }

    @Override
    public IncidentTracker incident() {
        value = 1;

        final long now = System.currentTimeMillis();

        session.track(this, now);
        session.update(this, now);

        return this;
    }

    public static class Factory implements StatsTrackerFactory<IncidentTracker> {

        private static final long serialVersionUID = 7771746449128030926L;

        @Override
        public IncidentTracker createTracker(final StatsKey key,
                                                  final StatsSessionManager sessionManager) {
            return new DefaultIncidentTracker(sessionManager.getOrCreateSession(key));
        }
        
        @Override
        public Class<IncidentTracker> getTrackerType() {
            return IncidentTracker.class;
        }
    }
}

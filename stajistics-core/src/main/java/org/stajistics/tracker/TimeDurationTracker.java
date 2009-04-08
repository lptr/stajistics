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

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;



/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class TimeDurationTracker extends AbstractStatsTracker {

    public static final StatsTrackerFactory FACTORY = new Factory();

    public TimeDurationTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void commitImpl() {
        final long now = System.currentTimeMillis(); 
        value = now - this.timeStamp;

        session.update(this, now);
    }

    public static class Factory implements StatsTrackerFactory {
        @Override
        public StatsTracker createTracker(final StatsKey key) {
            return new TimeDurationTracker(Stats.getSessionManager().getOrCreateSession(key));
        }
    }

}

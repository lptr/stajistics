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
public class DefaultManualStatsTracker extends AbstractStatsTracker implements ManualStatsTracker {

    public static final Factory FACTORY = new Factory();

    public DefaultManualStatsTracker(final StatsSession statsSession) {
        super(statsSession);
    }

    @Override
    public StatsTracker update(final double value) {
        this.value += value;
        return this;
    }

    @Override
    public StatsTracker setValue(final double value) {
        this.value = value;
        return this;
    }

    public static class Factory implements StatsTrackerFactory {
        @Override
        public ManualStatsTracker createTracker(final StatsKey key) {
            return new DefaultManualStatsTracker(Stats.getSessionManager().getOrCreateSession(key));
        }
    }
}

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

import org.stajistics.SimpleStatsKey;
import org.stajistics.session.ImmutableStatsSession;
import org.stajistics.session.StatsSession;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public final class NullTracker implements StatsTracker {

    private static final NullTracker instance = new NullTracker();

    private static final StatsSession session = 
        new ImmutableStatsSession(new SimpleStatsKey(NullTracker.class.getName()));

    private NullTracker() {}

    public static NullTracker getInstance() {
        return instance;
    }

    @Override
    public boolean isTracking() {
        return false;
    }

    @Override
    public StatsTracker track() {
        return this;
    }

    @Override
    public StatsTracker commit() {
        return this;
    }

    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public StatsTracker reset() {
        return this;
    }

    @Override
    public StatsSession getSession() {
        return session;
    }

}

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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreadLocalStatsTrackerStore implements StatsTrackerStore {

    private final ConcurrentMap<StatsKey,ThreadLocal<StatsTracker>> trackerMap =
        new ConcurrentHashMap<StatsKey,ThreadLocal<StatsTracker>>();

    private StatsTrackerFactory factory;

    public ThreadLocalStatsTrackerStore(final StatsTrackerFactory factory) {
        setTrackerFactory(factory);
    }

    @Override
    public StatsTracker getTracker(final StatsSession session) {

        final StatsKey key = session.getKey();

        ThreadLocal<StatsTracker> trackerLocal = trackerMap.get(key);
        if (trackerLocal == null) {
            trackerLocal = new ThreadLocal<StatsTracker>() {
                @Override
                protected StatsTracker initialValue() {
                    return factory.createStatsTracker(session,
                                                      key.getTrackerClass());
                }
            };

            ThreadLocal<StatsTracker> existingTrackerLocal = trackerMap.putIfAbsent(key, trackerLocal);
            if (existingTrackerLocal != null) {
                trackerLocal = existingTrackerLocal;
            }
        }

        return trackerLocal.get();
    }

    @Override
    public StatsTrackerFactory getTrackerFactory() {
        return factory;
    }

    @Override
    public void setTrackerFactory(final StatsTrackerFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }

        this.factory = factory;
    }

}

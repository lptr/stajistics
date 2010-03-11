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
package org.stajistics.tracker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.util.Decorator;

/**
 * 
 * @param <T>
 *
 * @author The Stajistics Project
 */
public class SingletonTrackerFactoryDecorator<T extends Tracker>
        implements TrackerFactory<T>,Decorator<TrackerFactory<T>> {

    private final TrackerFactory<T> delegate;

    private final ConcurrentMap<StatsKey,T> singletonTrackerMap = new ConcurrentHashMap<StatsKey,T>();

    public SingletonTrackerFactoryDecorator(final TrackerFactory<T> delegate) {
        if (delegate == null) {
            throw new NullPointerException("null delegate");
        }
        this.delegate = delegate;
    }

    @Override
    public T createTracker(final StatsKey key, 
                           final StatsSessionManager sessionManager) {
        T singletonTracker = singletonTrackerMap.get(key);
        if (singletonTracker == null) {
            singletonTracker = delegate.createTracker(key, sessionManager);
            T existingTracker = singletonTrackerMap.putIfAbsent(key, singletonTracker);
            if (existingTracker != null) {
                singletonTracker = existingTracker;
            }
        }

        return singletonTracker;
    }

    @Override
    public Class<T> getTrackerType() {
        return delegate.getTrackerType();
    }

    @Override
    public TrackerFactory<T> delegate() {
        return delegate;
    }
}

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
public class ThreadLocalTrackerFactoryDecorator<T extends StatsTracker> 
        implements StatsTrackerFactory<T>,Decorator<StatsTrackerFactory<T>> {

    private final StatsTrackerFactory<T> delegate;

    private final ConcurrentMap<StatsKey,ThreadLocal<T>> threadTrackerMap =
        new ConcurrentHashMap<StatsKey,ThreadLocal<T>>();

    public ThreadLocalTrackerFactoryDecorator(final StatsTrackerFactory<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T createTracker(final StatsKey key, 
                           final StatsSessionManager sessionManager) {

        ThreadLocal<T> trackerLocal = threadTrackerMap.get(key);
        if (trackerLocal == null) {
            trackerLocal = new ThreadLocal<T>() {
                @Override
                protected T initialValue() {
                    return delegate.createTracker(key, sessionManager);
                }
            };

            ThreadLocal<T> existingTrackerLocal = threadTrackerMap.putIfAbsent(key, trackerLocal);
            if (existingTrackerLocal != null) {
                trackerLocal = existingTrackerLocal;
            }
        }

        return trackerLocal.get();
    }

    @Override
    public Class<T> getTrackerType() {
        return delegate.getTrackerType();
    }

    @Override
    public StatsTrackerFactory<T> delegate() {
        return delegate;
    }
}

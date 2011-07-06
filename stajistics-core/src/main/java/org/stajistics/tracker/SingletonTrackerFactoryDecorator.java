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

import static org.stajistics.Util.assertNotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.stajistics.StatsKey;
import org.stajistics.event.EventHandler;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.util.Decorator;
import org.stajistics.util.ThreadSafe;

/**
 * A decorator for another TrackerFactory instance that ensures only one Tracker
 * instance per-key is ever created by the delegate factory. One instance of this
 * decorator can be shared by multiple {@link org.stajistics.configuration.StatsConfig}s provided, of course,
 * they wish to share the same delegate {@link TrackerFactory} instance.
 *
 * @param <T> The type of Tracker returned by the factory.
 *
 * @author The Stajistics Project
 */
@ThreadSafe
public class SingletonTrackerFactoryDecorator<T extends Tracker>
        implements TrackerFactory<T>,Decorator<TrackerFactory<T>> {

    private final TrackerFactory<T> delegate;
    private final EventManager eventManager;

    private final ConcurrentMap<StatsKey,T> singletonTrackerMap = new ConcurrentHashMap<StatsKey,T>();

    public SingletonTrackerFactoryDecorator(final TrackerFactory<T> delegate,
                                            final EventManager eventManager) {
        assertNotNull(delegate, "delegate");
        assertNotNull(eventManager, "eventManager");

        this.delegate = delegate;
        this.eventManager = eventManager;
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
            } else {
                eventManager.addEventHandler(key, new EventHandler() {
                    @Override
                    public void handleStatsEvent(final EventType eventType,
                                                 final StatsKey key,
                                                 final Object target) {
                        switch (eventType) {
                            case CONFIG_CHANGED:
                            case CONFIG_DESTROYED:
                            case SESSION_DESTROYED:
                                singletonTrackerMap.remove(key);
                        }
                    }
                });
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

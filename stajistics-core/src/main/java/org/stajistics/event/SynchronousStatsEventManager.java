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
package org.stajistics.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyMatcher;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class SynchronousStatsEventManager implements StatsEventManager {

    private static final long serialVersionUID = -1747663767850867849L;

    private static final Logger logger = LoggerFactory.getLogger(SynchronousStatsEventManager.class);

    private final List<StatsEventHandler> globalEventHandlers = createEventHandlerList();

    private final ConcurrentMap<StatsKey,List<StatsEventHandler>> sessionEventHandlers =
        new ConcurrentHashMap<StatsKey,List<StatsEventHandler>>();

    protected List<StatsEventHandler> createEventHandlerList() {
        return new CopyOnWriteArrayList<StatsEventHandler>();
    }

    @Override
    public Collection<StatsEventHandler> getGlobalEventHandlers() {
        return Collections.unmodifiableCollection(globalEventHandlers);
    }

    @Override
    public Map<StatsKey,Collection<StatsEventHandler>> getEventHandlers() {
        return getEventHandlers(StatsKeyMatcher.all());
    }

    @Override
    public Map<StatsKey,Collection<StatsEventHandler>> getEventHandlers(final StatsKeyMatcher keyMatcher) {
        if (keyMatcher.equals(StatsKeyMatcher.none())) {
            return Collections.emptyMap();
        }

        Map<StatsKey,Collection<StatsEventHandler>> matches = 
            new HashMap<StatsKey,Collection<StatsEventHandler>>(sessionEventHandlers.size());

        for (Map.Entry<StatsKey,List<StatsEventHandler>> entry : sessionEventHandlers.entrySet()) {
            if (keyMatcher.matches(entry.getKey())) {
                matches.put(entry.getKey(), new ArrayList<StatsEventHandler>(entry.getValue()));
            }
        }

        return Collections.unmodifiableMap(matches);
    }

    @Override
    public void addGlobalEventHandler(final StatsEventHandler eventHandler) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler");
        }

        globalEventHandlers.add(eventHandler);
    }

    @Override
    public void addEventHandler(final StatsKey key, 
                                final StatsEventHandler eventHandler) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        if (eventHandler == null) {
            throw new NullPointerException("eventHandler");
        }

        List<StatsEventHandler> eventHandlers = getEventHandlers(key, true);
        eventHandlers.add(eventHandler);
    }

    @Override
    public void removeGlobalEventHandler(StatsEventHandler eventHandler) {
        globalEventHandlers.remove(eventHandler);
    }

    @Override
    public void removeEventHandler(StatsKey key, StatsEventHandler eventHandler) {
        List<StatsEventHandler> eventHandlers = getEventHandlers(key, false);
        if (eventHandlers != null) {
            eventHandlers.remove(eventHandler);
        }
    }

    @Override
    public void clearGlobalEventHandlers() {
        globalEventHandlers.clear();
    }

    @Override
    public void clearEventHandlers() {
        for (Map.Entry<StatsKey,List<StatsEventHandler>> entry : sessionEventHandlers.entrySet()) {
            entry.getValue().clear();
        }

        sessionEventHandlers.clear();
    }

    @Override
    public void clearAllEventHandlers() {
        clearGlobalEventHandlers();
        clearEventHandlers();
    }

    private List<StatsEventHandler> getEventHandlers(final StatsKey key,
                                                     final boolean create) {
        List<StatsEventHandler> eventHandlers = sessionEventHandlers.get(key);
        if (eventHandlers == null && create) {
            eventHandlers = createEventHandlerList();
            List<StatsEventHandler> old = sessionEventHandlers.putIfAbsent(key, eventHandlers);
            if (old != null) {
                eventHandlers = old;
            }
        }

        return eventHandlers;
    }

    @Override
    public void fireEvent(final StatsEventType eventType, 
                          final StatsKey key,
                          final Object target) {
        if (eventType == null) {
            throw new NullPointerException("eventType");
        }
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (target == null) {
            throw new NullPointerException("target");
        }

        logger.trace("Firing event: {}, key: {}", eventType, key);

        List<StatsEventHandler> sessionEventHandlers = getEventHandlers(key, false);
        if (sessionEventHandlers != null) {
            fireEvent(sessionEventHandlers, eventType, key, target);
        }

        fireEvent(globalEventHandlers, eventType, key, target);
    }

    protected void fireEvent(final List<StatsEventHandler> handlers,
                             final StatsEventType eventType, 
                             final StatsKey key,
                             final Object target) {
        for (StatsEventHandler handler : handlers) {
            try {
                handler.handleStatsEvent(eventType, key, target);
            } catch (Exception e) {
                logger.error("Uncaught Exception", e);
            }
        }
    }
}

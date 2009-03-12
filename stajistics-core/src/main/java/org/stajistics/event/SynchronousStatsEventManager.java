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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class SynchronousStatsEventManager implements StatsEventManager {

    private static final Logger logger = LoggerFactory.getLogger(SynchronousStatsEventManager.class);

    private volatile boolean enabled = true;

    private final List<StatsEventHandler> globalEventHandlers = createEventHandlerList();

    private ConcurrentMap<StatsKey,List<StatsEventHandler>> sessionEventHandlers =
        new ConcurrentHashMap<StatsKey,List<StatsEventHandler>>();

    protected List<StatsEventHandler> createEventHandlerList() {
        return new CopyOnWriteArrayList<StatsEventHandler>();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void addGlobalEventHandler(final StatsEventHandler eventHandler) {
        if (eventHandler == null) {
            throw new NullPointerException("eventHandler");
        }

        globalEventHandlers.add(eventHandler);
    }

    @Override
    public void addSessionEventHandler(final StatsKey key, 
                                       final StatsEventHandler eventHandler) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        if (eventHandler == null) {
            throw new NullPointerException("eventHandler");
        }

        List<StatsEventHandler> eventHandlers = getSessionEventHandlers(key, true);
        eventHandlers.add(eventHandler);
    }

    @Override
    public void removeGlobalEventHandler(StatsEventHandler eventHandler) {
        globalEventHandlers.remove(eventHandler);
    }

    @Override
    public void removeSessionEventHandler(StatsKey key, StatsEventHandler eventHandler) {
        List<StatsEventHandler> eventHandlers = getSessionEventHandlers(key, false);
        if (eventHandlers != null) {
            eventHandlers.remove(eventHandler);
        }
    }

    @Override
    public void clearGlobalEventHandlers() {
        globalEventHandlers.clear();
    }

    @Override
    public void clearSessionEventHandlers() {
        //TODO: clear each List
        sessionEventHandlers.clear();
    }

    @Override
    public void clearHandlers() {
        clearGlobalEventHandlers();
        clearSessionEventHandlers();
    }

    private List<StatsEventHandler> getSessionEventHandlers(final StatsKey key,
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
                          final StatsSession session,
                          final StatsTracker tracker) {

        if (!enabled) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Firing event: " + eventType + ", key: " + key);
        }

        List<StatsEventHandler> sessionEventHandlers = getSessionEventHandlers(key, false);
        if (sessionEventHandlers != null) {
            fireEvent(sessionEventHandlers, eventType, key, session, tracker);
        }

        fireEvent(globalEventHandlers, eventType, key, session, tracker);
    }

    protected void fireEvent(final List<StatsEventHandler> handlers,
                             final StatsEventType eventType, 
                             final StatsKey key,
                             final StatsSession session,
                             final StatsTracker tracker) {
        for (StatsEventHandler handler : handlers) {
            try {
                handler.handleStatsEvent(eventType, key, session, tracker);
            } catch (Exception e) {
                logger.error("Uncaught Exception in " + 
                                 StatsEventHandler.class.getSimpleName(), 
                             e);
            }
        }
    }
}

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
package org.stajistics.session;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyMatcher;
import org.stajistics.StatsProperties;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;

/**
 * The default implementation of {@link StatsSessionManager}. All operations are done in a
 * thread safe manner using concurrent utils.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionManager implements StatsSessionManager {

    public static final String PROP_INITIAL_CAPACITY = 
        StatsSessionManager.class.getName() + ".sessionMap.initialCapacity";
    public static final String PROP_LOAD_FACTOR = 
        StatsSessionManager.class.getName() + ".sessionMap.loadFactor";
    public static final String PROP_CONCURRENCY_LEVEL = 
        StatsSessionManager.class.getName() + ".sessionMap.concurrencyLevel";

    private static final long serialVersionUID = 7815229695876668904L;

    private static final Logger logger = LoggerFactory.getLogger(DefaultStatsSessionManager.class);

    protected ConcurrentMap<StatsKey,StatsSession> sessionMap = createSessionMap();

    protected final StatsConfigManager configManager;
    protected final StatsEventManager eventManager;

    public DefaultStatsSessionManager(final StatsConfigManager configManager,
                                      final StatsEventManager eventManager) {
        if (configManager == null) {
            throw new NullPointerException("configManager");
        }
        if (eventManager == null) {
            throw new NullPointerException("eventManager");
        }

        this.configManager = configManager;
        this.eventManager = eventManager;
    }

    /**
     * A factory method for creating the ConcurrentMap that will store {@link StatsSession}s.
     *
     * @return A {@link ConcurrentMap}, never <tt>null</tt>.
     */
    protected ConcurrentMap<StatsKey,StatsSession> createSessionMap() {

        int initialCapacity = StatsProperties.getIntegerProperty(PROP_INITIAL_CAPACITY, 256);
        float loadFactor = StatsProperties.getFloatProperty(PROP_LOAD_FACTOR, 0.6f);
        int concurrencyLevel = StatsProperties.getIntegerProperty(PROP_CONCURRENCY_LEVEL, 64);

        return new ConcurrentHashMap<StatsKey,StatsSession>(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSessionCount() {
        return sessionMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<StatsKey> getKeys() {
        return Collections.unmodifiableSet(sessionMap.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<StatsSession> getSessions() {
        return Collections.unmodifiableCollection(sessionMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<StatsSession> getSessions(final StatsKeyMatcher keyMatcher) {
        return keyMatcher.filterToCollection(sessionMap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSession getSession(final StatsKey key) {
        return sessionMap.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSession getOrCreateSession(final StatsKey key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsSession session = sessionMap.get(key);

        if (session == null) {
            session = createSession(key);

            logger.debug("Created StatsSession for key: {}", key);

            StatsSession existingSession = sessionMap.putIfAbsent(key, session);
            if (existingSession != null) {
                session = existingSession;

            } else {
                eventManager.fireEvent(StatsEventType.SESSION_CREATED, key, session);
            }
        }

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(final StatsSession statsSession) {
        return remove(statsSession.getKey()) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSession remove(final StatsKey key) {
        StatsSession session = sessionMap.remove(key);

        if (session != null) {
            eventManager.fireEvent(StatsEventType.SESSION_DESTROYED, key, session);
        }

        return session; 
    }

    /**
     * A factory method for creating a {@link StatsSession} instance.
     *
     * @param key The key for which to create a {@link StatsSession}.
     * @return A {@link StatsSession} instance, never <tt>null</tt>.
     */
    protected StatsSession createSession(final StatsKey key) {
        StatsConfig config = configManager.getConfig(key);
        return config.getSessionFactory().createSession(key, eventManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        Set<StatsKey> keySet = sessionMap.keySet();
        for (StatsKey key : keySet) {
            remove(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAllSessions() {
        for (StatsSession session : sessionMap.values()) {
            session.clear();
        }
    }
}

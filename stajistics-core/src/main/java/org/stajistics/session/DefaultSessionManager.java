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
import org.stajistics.Stats;
import org.stajistics.StatsConfig;
import org.stajistics.StatsKey;
import org.stajistics.event.StatsEventType;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultSessionManager implements StatsSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSessionManager.class);

    protected ConcurrentMap<StatsKey,StatsSession> sessionMap = 
        new ConcurrentHashMap<StatsKey,StatsSession>(128);

    @Override
    public int getSessionCount() {
        return sessionMap.size();
    }

    @Override
    public Set<StatsKey> getKeys() {
        return Collections.unmodifiableSet(sessionMap.keySet());
    }

    @Override
    public Collection<StatsSession> getSessions() {
        return Collections.unmodifiableCollection(sessionMap.values());
    }

    @Override
    public StatsSession getSession(final StatsKey key) {
        return sessionMap.get(key);
    }

    @Override
    public StatsSession getOrCreateSession(final StatsKey key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsSession session = sessionMap.get(key);

        if (session == null) {
            session = createSession(key);

            if (logger.isDebugEnabled()) {
                logger.debug("Created StatsSession for key: " + key);
            }

            StatsSession existingSession = sessionMap.putIfAbsent(key, session);
            if (existingSession != null) {
                session = existingSession;

            } else {
                Stats.getEventManager()
                     .fireEvent(StatsEventType.SESSION_CREATED, key, session);
            }
        }

        return session;
    }

    @Override
    public boolean remove(final StatsSession statsSession) {
        return remove(statsSession.getKey()) != null;
    }

    @Override
    public StatsSession remove(final StatsKey key) {
        StatsSession session = sessionMap.remove(key);

        if (session != null) {
            Stats.getEventManager()
                 .fireEvent(StatsEventType.SESSION_DESTROYED, key, session);
        }

        return session; 
    }

    protected StatsSession createSession(final StatsKey key) {
        StatsConfig config = Stats.getConfigManager().getConfig(key);
        return config.getSessionFactory().createSession(key);
    }

    @Override
    public void clear() {
        Set<StatsKey> keySet = sessionMap.keySet();
        for (StatsKey key : keySet) {
            remove(key);
        }
    }

    @Override
    public void clearAllSessions() {
        for (StatsSession session : sessionMap.values()) {
            session.clear();
        }
    }
}

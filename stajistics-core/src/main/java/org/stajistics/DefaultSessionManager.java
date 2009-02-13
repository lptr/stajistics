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
package org.stajistics;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.session.DefaultStatsSession;
import org.stajistics.session.StatsSession;

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
        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsSession statsSession = sessionMap.get(key);

        if (statsSession == null) {
            statsSession = createSession(key);

            if (logger.isDebugEnabled()) {
                logger.debug("Created StatsSession for key: " + key);
            }

            StatsSession existingSession = sessionMap.putIfAbsent(key, statsSession);
            if (existingSession != null) {
                statsSession = existingSession;
            }
        }

        return statsSession;
    }

    @Override
    public boolean remove(final StatsSession statsSession) {
        return sessionMap.remove(statsSession.getKey()) != null;
    }

    @Override
    public StatsSession remove(final StatsKey key) {
        return sessionMap.remove(key);
    }

    protected StatsSession createSession(final StatsKey key) {
        return new DefaultStatsSession(key);
    }

    @Override
    public void clear() {
        sessionMap.clear();
    }
}

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
package org.stajistics.management.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

import java.io.IOException;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionManagerMXBean implements StatsSessionManagerMXBean {

    protected static final String SESSION_DUMP_LOGGER_NAME = "stajistics.session.dump";
    private static final Logger sessionLogger = LoggerFactory
            .getLogger(SESSION_DUMP_LOGGER_NAME);

    protected final StatsSessionManager sessionManager;

    public DefaultStatsSessionManagerMXBean(final StatsSessionManager sessionManager) {
        if (sessionManager == null) {
            throw new NullPointerException("sessionManager");
        }

        this.sessionManager = sessionManager;
    }

    @Override
    public String getImplementation() throws IOException {
        return sessionManager.getClass().getName();
    }

    @Override
    public int getSessionCount() throws IOException {
        return sessionManager.getSessions().size();
    }

    @Override
    public void dumpAllSessions() throws IOException {
        if (sessionLogger.isInfoEnabled()) {
            for (StatsSession session : sessionManager.getSessions()) {
                sessionLogger.info(session.toString());
            }
        }
    }

    @Override
    public void clearAllSessions() throws IOException {
        sessionManager.clearAllSessions();
    }

    @Override
    public void destroyAllSessions() throws IOException {
        sessionManager.clear();
    }

}

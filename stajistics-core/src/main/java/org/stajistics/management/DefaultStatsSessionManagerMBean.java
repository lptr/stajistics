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
package org.stajistics.management;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionManagerMBean implements StatsSessionManagerMBean {

    protected static final String SESSION_DUMP_LOGGER_NAME = "stajistics.session.dump";

    protected final StatsSessionManager sessionManager;

    public DefaultStatsSessionManagerMBean(final StatsSessionManager sessionManager) {
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
        Logger logger = Logger.getLogger(SESSION_DUMP_LOGGER_NAME);
        if (logger.isLoggable(Level.INFO)) {
            for (StatsSession session : sessionManager.getSessions()) {
                logger.info(session.toString());
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

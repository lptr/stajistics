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
package org.stajistics.tracker;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreadCPUTimeTracker extends AbstractThreadInfoStatsTracker {

    private static final long serialVersionUID = 321485927262898420L;

    public static final StatsTrackerFactory FACTORY = new Factory();

    private long startCPUTime; // nanos

    public ThreadCPUTimeTracker(final StatsSession session) {
        super(session);

        ensureCPUTimeMonitoringEnabled();
    }

    @Override
    protected void trackImpl(final long now) {
        if (isCPUTimeMonitoringEnabled()) {
            startCPUTime = getThreadMXBean().getCurrentThreadCpuTime();

            super.trackImpl(now);
        }
    }

    @Override
    protected void commitImpl() {
        if (isCPUTimeMonitoringEnabled()) {
            long endCPUTime = getThreadMXBean().getCurrentThreadCpuTime();

            value = (endCPUTime - startCPUTime) / 1000000d; // to millis

            session.update(this, -1);
        }
    }

    @Override
    public StatsTracker reset() {
        super.reset();

        startCPUTime = -1;

        return this;
    }

    public static class Factory implements StatsTrackerFactory {

        private static final long serialVersionUID = 8381879914728970427L;

        @Override
        public StatsTracker createTracker(final StatsKey key,
                                          final StatsSessionManager sessionManager) {
            return new ThreadCPUTimeTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

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
package org.stajistics.tracker.span;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.Tracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreadCPUTimeTracker extends AbstractThreadInfoSpanTracker {

    public static final Factory FACTORY = new Factory();

    private long startCPUTime; // nanos

    public ThreadCPUTimeTracker(final StatsSession session) {
        super(session);

        ensureCPUTimeMonitoringEnabled();
    }

    @Override
    protected void startImpl(final long now) {
        if (isCPUTimeMonitoringEnabled()) {
            startCPUTime = getThreadMXBean().getCurrentThreadCpuTime();

            super.startImpl(now);
        }
    }

    @Override
    protected void stopImpl(final long now) {
        if (isCPUTimeMonitoringEnabled()) {
            long endCPUTime = getThreadMXBean().getCurrentThreadCpuTime();

            value = (endCPUTime - startCPUTime) / 1000000d; // to millis

            session.update(this, now);
        }
    }

    @Override
    public Tracker reset() {
        super.reset();

        startCPUTime = -1;

        return this;
    }

    public static class Factory extends AbstractSpanTrackerFactory {

        @Override
        public SpanTracker createTracker(final StatsKey key,
                                         final StatsSessionManager sessionManager) {
            return new ThreadCPUTimeTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

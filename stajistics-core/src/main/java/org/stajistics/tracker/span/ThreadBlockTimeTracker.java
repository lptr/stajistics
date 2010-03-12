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

import java.lang.management.ThreadInfo;

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
public class ThreadBlockTimeTracker extends AbstractThreadInfoSpanTracker {

    public static final Factory FACTORY = new Factory();

    private long startBlockTime;

    public ThreadBlockTimeTracker(final StatsSession session) {
        super(session);

        ensureContentionMonitoringEnabled();
    }

    @Override
    protected void startImpl(final long now) {
        ThreadInfo threadInfo = getCurrentThreadInfo();
        if (threadInfo != null) {
            startBlockTime = threadInfo.getBlockedTime();
            if (startBlockTime > -1) {
                super.startImpl(now);
            }

        } else {
            startBlockTime = -1;
        }
    }

    @Override
    protected void stopImpl(final long now) {
        ThreadInfo threadInfo = getCurrentThreadInfo();
        if (threadInfo != null && startBlockTime > -1) {
            long endBlockTime = threadInfo.getBlockedTime();
            if (endBlockTime > -1) {
                value = endBlockTime - startBlockTime;
                session.update(this, now);
            }
        }
    }

    @Override
    public Tracker reset() {
        super.reset();

        startBlockTime = -1;

        return this;
    }

    public static class Factory extends AbstractSpanTrackerFactory {

        @Override
        public SpanTracker createTracker(final StatsKey key,
                                         final StatsSessionManager sessionManager) {
            return new ThreadBlockTimeTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * TODO: This tracker currently depends on the ordering of ManagementFactory.getGarbageCollectorMXBeans(). Is that safe?
 * 
 *
 * @author The Stajistics Project
 */
public class GarbageCollectionTimeTracker extends AbstractStatsTracker {

    private static final long serialVersionUID = -1668165910427204823L;

    public static final StatsTrackerFactory FACTORY = new Factory();

    private String[] startCGNames = null;
    private long[] startGCTimes = null;

    public GarbageCollectionTimeTracker(final StatsSession session) {
        super(session);
    }

    @Override
    protected void trackImpl(final long now) {
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        startCGNames = new String[gcMXBeans.size()];
        startGCTimes = new long[gcMXBeans.size()];

        int i = 0;
        for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
            startCGNames[i] = gcMXBean.getName(); 
            startGCTimes[i] = gcMXBean.getCollectionTime();
            i++;
        }

        super.trackImpl(now);
    }

    @Override
    protected void commitImpl(final long now) {
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        if (gcMXBeans.size() == startCGNames.length) {

            boolean commit = true;
            long totalGCTime = 0;

            int i = 0;

            for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
                if (!startCGNames[i].equals(gcMXBean.getName())) {
                    commit = false;
                    break;
                }

                long startGCTime = startGCTimes[i];
                long endGCTime = gcMXBean.getCollectionTime();

                if (startGCTime == -1) {
                    if (endGCTime != -1) {
                        commit = false;
                        break;
                    }
                } else {
                    if (endGCTime == -1) {
                        commit = false;
                        break;
                    }

                    totalGCTime += endGCTime - startGCTime;
                }

                i++;
            }

            if (commit) {
                value = totalGCTime;
                session.update(this, now);
            }
        }
    }

    @Override
    public StatsTracker reset() {
        super.reset();

        startCGNames = null;
        startGCTimes = null;

        return this;
    }

    public static class Factory implements StatsTrackerFactory {

        private static final long serialVersionUID = -4982332914285248619L;

        @Override
        public StatsTracker createTracker(final StatsKey key,
                                          final StatsSessionManager sessionManager) {
            return new GarbageCollectionTimeTracker(sessionManager.getOrCreateSession(key));
        }
    }
}

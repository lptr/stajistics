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
package org.stajistics.tracker.span;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractThreadInfoStatsTracker extends AbstractSpanStatsTracker {

    private static final long serialVersionUID = 5195519555074189812L;

    private static Logger logger = LoggerFactory.getLogger(AbstractThreadInfoStatsTracker.class);

    private static volatile boolean hasSetContentionMonitoringEnabled = false;
    private static volatile boolean hasSetCPUTimeMonitoringEnabled = false;

    private static boolean contentionMonitoringEnabled = false;
    private static boolean cpuTimeMonitoringEnabled = false;

    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public AbstractThreadInfoStatsTracker(final StatsSession session) {
        super(session);
    }

    protected static ThreadMXBean getThreadMXBean() {
        return threadMXBean;
    }

    protected static void ensureContentionMonitoringEnabled() {
        if (!hasSetContentionMonitoringEnabled) {
            hasSetContentionMonitoringEnabled = true;

            if (threadMXBean.isThreadContentionMonitoringSupported()) {
                threadMXBean.setThreadContentionMonitoringEnabled(true);
                contentionMonitoringEnabled = true;

                logger.info("Enabling thread contention monitoring");

            } else {
                logger.warn("Thread contention monitoring is not supported in this JVM; "
                          + "Thread contention related trackers will be silent");
            }
        }
    }

    protected static boolean isContentionMonitoringEnabled() {
        return contentionMonitoringEnabled;
    }

    protected static void ensureCPUTimeMonitoringEnabled() {
        if (!hasSetCPUTimeMonitoringEnabled) {
            hasSetCPUTimeMonitoringEnabled = true;

            if (threadMXBean.isCurrentThreadCpuTimeSupported()) {
                threadMXBean.setThreadCpuTimeEnabled(true);
                cpuTimeMonitoringEnabled = true;

                logger.info("Enabling thread CPU time monitoring");
            } else {
                logger.warn("Thread CPU time monitoring is not supported in this JVM; "
                          + "Thread CPU time related trackers will be silent");
            }
        }
    }

    protected static boolean isCPUTimeMonitoringEnabled() {
        return cpuTimeMonitoringEnabled;
    }

    protected ThreadInfo getCurrentThreadInfo() {
        if (contentionMonitoringEnabled) {
            return threadMXBean.getThreadInfo(Thread.currentThread().getId(), 0);
        }

        return null;
    }
}

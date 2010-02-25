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
package org.stajistics.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsProperties;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author The Stajistics Project
 */
public class TaskServiceThreadFactory implements ThreadFactory {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceThreadFactory.class);

    private static final String PROP_THREAD_PRIORITY = TaskServiceThreadFactory.class.getName() + ".threadPriority";
    private static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY;

    private static final AtomicInteger nextPoolId = new AtomicInteger(0);
    private final AtomicInteger nextThreadId = new AtomicInteger(0);

    private final String namePrefix;
    private final ThreadGroup threadGroup;

    public TaskServiceThreadFactory() {
        namePrefix = TaskService.class.getSimpleName() + "-" + nextPoolId;
        threadGroup = new ThreadGroup(namePrefix);
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    @Override
    public Thread newThread(final Runnable r) {

        final String threadName = namePrefix + "-worker-" + nextThreadId.incrementAndGet();

        Runnable wrapper = new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Thread started: {}",
                                 Thread.currentThread().getName());

                    r.run();
                } finally {
                    logger.debug("Thread destroyed: {}",
                                 Thread.currentThread().getName());
                }
            }
        };

        Thread thread = new Thread(threadGroup, wrapper, threadName);

        int priority = StatsProperties.getIntegerProperty(PROP_THREAD_PRIORITY,
                                                          DEFAULT_THREAD_PRIORITY);
        thread.setPriority(priority);
        thread.setDaemon(true);

        return thread;
    }
}

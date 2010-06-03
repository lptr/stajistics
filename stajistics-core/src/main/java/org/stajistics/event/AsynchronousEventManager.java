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
package org.stajistics.event;

import org.stajistics.StatsKey;
import org.stajistics.task.TaskService;

import java.util.concurrent.Callable;

/**
 *
 * @author The Stajistics Project
 */
public class AsynchronousEventManager extends SynchronousEventManager {

    private final TaskService taskService;

    public AsynchronousEventManager(final TaskService taskService) {
        if (taskService == null) {
            throw new NullPointerException("taskService");
        }

        this.taskService = taskService;
    }

    @Override
    public void fireEvent(final EventType eventType,
                          final StatsKey key,
                          final Object target) {
        taskService.submit(getClass(), new EventCallable(eventType, key, target));
    }

    /* INNER CLASSES */

    private class EventCallable implements Callable<Void> {

        private final EventType eventType;
        private final StatsKey key;
        private final Object target;

        EventCallable(final EventType eventType,
                      final StatsKey key,
                      final Object target) {
            if (eventType == null) {
                throw new NullPointerException("eventType");
            }
            if (key == null) {
                throw new NullPointerException("key");
            }
            if (target == null) {
                throw new NullPointerException("target");
            }

            this.eventType = eventType;
            this.key = key;
            this.target = target;
        }

        @Override
        public Void call() throws Exception {
            AsynchronousEventManager.super.fireEvent(eventType, key, target);
            return null;
        }
    }
}

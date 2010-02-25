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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author The Stajistics Project
 */
public class SimpleTaskService implements TaskService {

    @Override
    public void execute(final Class<?> source,
                        final Runnable task) {
        task.run();
    }

    @Override
    public <T> Future<T> submit(final Class<?> source,
                                final Callable<T> task) {
        try {
            T result = task.call();
            return new FinishedFuture<T>(result, null);
        } catch (Throwable t) {
            return new FinishedFuture<T>(null, t);
        }
    }

    @Override
    public void shutdown() {
    }

    private final static class FinishedFuture<T> implements Future<T> {

        private final T result;
        private final Throwable error;

        public FinishedFuture(final T result,
                              final Throwable error) {
            this.result = result;
            this.error = error;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            if (error != null) {
                throw new ExecutionException(error);
            }
            return result;
        }

        @Override
        public T get(final long timeout, 
                     final TimeUnit unit) throws InterruptedException, ExecutionException,
                TimeoutException {
            return get();
        }

        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }
    }
}

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
package org.stajistics.aop;

import static org.stajistics.Util.assertNotNull;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class StatsDecorators {

    private final StatsFactory factory;

    public StatsDecorators() {
        this(null);
    }

    public StatsDecorators(final StatsFactory factory) {
        if (factory == null) {
            this.factory = StatsFactory.forNamespace(StatsConstants.DEFAULT_NAMESPACE);
        } else {
            this.factory = factory;
        }
    }

    private void rethrow(final Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }

        throw new RuntimeException(t);
    }

    public Runnable wrap(final Runnable r,
                         final StatsKey key) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    SpanTracker tracker = factory.track(key);
                    try {
                        r.run();
                    } finally {
                        tracker.commit();
                    }

                } catch (Throwable t) {
                    factory.failure(t, key);
                    rethrow(t);
                }
            }
        };
    }

    public <T> Callable<T> wrap(final Callable<T> c,
                                final StatsKey key) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    SpanTracker tracker = factory.track(key);
                    try {
                        return c.call();
                    } finally {
                        tracker.commit();
                    }

                } catch (Throwable t) {
                    factory.failure(t, key);

                    if (t instanceof Exception) {
                        throw (Exception)t;
                    }
                    if (t instanceof Error) {
                        throw (Error)t;
                    }
                    throw new RuntimeException(t);
                }
            }
        };
    }

    public Observer wrap(final Observer observer,
                         final StatsKey key) {
        return new Observer() {
            @Override
            public void update(final Observable o,
                               final Object arg) {
                try {
                    SpanTracker tracker = factory.track(key);
                    try {
                        observer.update(o, arg);
                    } finally {
                        tracker.commit();
                    }

                } catch (Throwable t) {
                    factory.failure(t, key);
                    rethrow(t);
                }
            }
        };
    }

    public ThreadFactory wrap(final ThreadFactory threadFactory,
                              final StatsKey key) {
        return new ThreadFactoryWrapper(threadFactory, key);
    }

    public Executor wrap(final Executor executor,
                         final StatsKey executorKey,
                         final StatsKey commandKey) {
        return new ExecutorWrapper(executor, executorKey, commandKey);
    }

    /* NESTED CLASSES */

    protected class ThreadFactoryWrapper implements ThreadFactory {

        protected final ThreadFactory threadFactory;
        protected final StatsKey key;

        public ThreadFactoryWrapper(final ThreadFactory threadFactory,
                                    final StatsKey key) {
            assertNotNull(threadFactory, "threadFactory");

            this.threadFactory = threadFactory;
            this.key = key;
        }

        @Override
        public Thread newThread(final Runnable r) {
            return threadFactory.newThread(wrap(r, key));
        }
    }

    protected class ExecutorWrapper implements Executor {

        protected final Executor executor;
        protected final StatsKey executorKey;
        protected final StatsKey commandKey;

        public ExecutorWrapper(final Executor executor,
                               final StatsKey executorKey,
                               final StatsKey commandKey) {
            assertNotNull(executor, "executor");
            assertNotNull(executorKey, "executorKey");
            assertNotNull(commandKey, "commandKey");

            this.executor = executor;
            this.executorKey = executorKey;
            this.commandKey = commandKey;
        }

        @Override
        public void execute(final Runnable command) {
            try {
                factory.incident(executorKey);

                executor.execute(wrap(command, commandKey));

            } catch (Throwable t) {
                factory.failure(t, executorKey);
                rethrow(t);
            }
        }
    }

}

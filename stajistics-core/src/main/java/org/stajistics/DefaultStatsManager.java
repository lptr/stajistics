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
package org.stajistics;

import static org.stajistics.Util.assertNotNull;

import java.util.concurrent.Callable;

import org.stajistics.configuration.DefaultStatsConfigBuilderFactory;
import org.stajistics.configuration.DefaultStatsConfigManager;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.event.SynchronousEventManager;
import org.stajistics.session.DefaultSessionManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.task.TaskService;
import org.stajistics.task.ThreadPoolTaskService;
import org.stajistics.tracker.DefaultTrackerLocator;
import org.stajistics.tracker.NullTrackerLocator;
import org.stajistics.tracker.TrackerLocator;
import org.stajistics.util.ServiceLifeCycle;

/**
 * The default implementation of {@link StatsManager}. Clients typically do not
 * instantiate this class directly. Instead use {@link Stats#getManager()}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsManager implements StatsManager {

    private volatile boolean enabled = true;
    private volatile UncaughtExceptionHandler uncaughtExceptionHandler = NullUncaughtExceptionHandler.getInstance();

    protected String namespace;
    protected final StatsConfigManager configManager;
    protected final StatsSessionManager sessionManager;
    protected final EventManager eventManager;
    protected final TrackerLocator trackerLocator;
    protected final StatsKeyFactory keyFactory;
    protected final StatsConfigBuilderFactory configBuilderFactory;
    protected final TaskService taskService;

    private final ServiceLifeCycle.Support lifeCycleSupport = new Support();

    /**
     * Construct a DefaultStatsManager using the given set of managers.
     *
     * @param configManager The {@link StatsConfigManager} to use. Must not be <tt>null</tt>.
     * @param sessionManager The {@link StatsSessionManager} to use. Must not be <tt>null</tt>.
     * @param eventManager The {@link EventManager} to use. Must not be <tt>null</tt>.
     * @param trackerLocator The {@link TrackerLocator} to use. Must not be <tt>null</tt>.
     * @param keyFactory The {@link StatsKeyFactory} to use. Must not be <tt>null</tt>.
     * @param configBuilderFactory The {@link org.stajistics.configuration.StatsConfigBuilderFactory} to use. Must not be <tt>null</tt>.
     * @param taskService The {@link TaskService} to use. Must not be <tt>null</tt>.
     * @throws NullPointerException If any parameter is <tt>null</tt>.
     */
    public DefaultStatsManager(final String namespace,
                               final StatsConfigManager configManager,
                               final StatsSessionManager sessionManager,
                               final EventManager eventManager,
                               final TrackerLocator trackerLocator,
                               final StatsKeyFactory keyFactory,
                               final StatsConfigBuilderFactory configBuilderFactory,
                               final TaskService taskService) {
        assertNotNull(configManager, "configManager");
        assertNotNull(sessionManager, "sessionManager");
        assertNotNull(eventManager, "eventManager");
        assertNotNull(trackerLocator, "trackerLocator");
        assertNotNull(keyFactory, "keyFactory");
        assertNotNull(configBuilderFactory, "configBuilderFactory");
        assertNotNull(taskService, "taskService");

        if (namespace != null && namespace.isEmpty()) {
            this.namespace = null;
        } else {
            this.namespace = namespace;
        }

        this.keyFactory = keyFactory;
        this.configManager = configManager;
        this.sessionManager = sessionManager;
        this.trackerLocator = trackerLocator;
        this.eventManager = eventManager;
        this.configBuilderFactory = configBuilderFactory;
        this.taskService = taskService;
    }

    @Override
    public void initialize() {
        lifeCycleSupport.initialize(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (namespace == null) {
                    if (StatsManagerRegistry.getInstance().getStatsManagerCount() == 0) {
                        namespace = StatsConstants.DEFAULT_NAMESPACE;
                    } else {
                        namespace = Integer.toHexString(System.identityHashCode(this));
                    }
                }

                keyFactory.setNamespace(namespace);

                StatsManagerRegistry.getInstance().registerStatsManager(DefaultStatsManager.this);

                eventManager.initialize();
                taskService.initialize();
                configManager.initialize();
                sessionManager.initialize();

                eventManager.fireEvent(EventType.STATS_MANAGER_INITIALIZED, null, DefaultStatsManager.this);

                return null;
            }
        });
    }

    @Override
    public boolean isRunning() {
        return lifeCycleSupport.isRunning();
    }

    @Override
    public void shutdown() {
        lifeCycleSupport.shutdown(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                eventManager.fireEvent(EventType.STATS_MANAGER_SHUTTING_DOWN, null, DefaultStatsManager.this);

                setEnabled(false);

                sessionManager.shutdown();
                configManager.shutdown();
                taskService.shutdown();
                eventManager.shutdown();

                StatsManagerRegistry.getInstance().removeStatsManager(DefaultStatsManager.this);

                return null;
            }
        });
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public StatsConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public StatsSessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public TrackerLocator getTrackerLocator() {
        if (!enabled) {
            return NullTrackerLocator.getInstance();
        }

        return trackerLocator;
    }

    @Override
    public StatsKeyFactory getKeyFactory() {
        return keyFactory;
    }

    @Override
    public StatsConfigBuilderFactory getConfigBuilderFactory() {
        return configBuilderFactory;
    }

    @Override
    public TaskService getTaskService() {
        return taskService;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    @Override
    public void setUncaughtExceptionHandler(final UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (uncaughtExceptionHandler == null) {
            this.uncaughtExceptionHandler = NullUncaughtExceptionHandler.getInstance();
        } else {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }
    }

    /* NESTED CLASSES */

    public static class Builder {

        protected String namespace = null;
        protected StatsConfigManager configManager = null;
        protected StatsSessionManager sessionManager = null;
        protected EventManager eventManager = null;
        protected TrackerLocator trackerLocator = null;
        protected StatsKeyFactory keyFactory = null;
        protected StatsConfigBuilderFactory configBuilderFactory = null;
        protected TaskService taskService = null;
        protected boolean enabled = true;
        protected UncaughtExceptionHandler uncaughtExceptionHandler = null;

        public Builder withNamespace(final String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder withConfigManager(final StatsConfigManager configManager) {
            assertNotNull(configManager, "configManager");
            this.configManager = configManager;
            return this;
        }

        public Builder withSessionManager(final StatsSessionManager sessionManager) {
            assertNotNull(sessionManager, "sessionManager");
            this.sessionManager = sessionManager;
            return this;
        }

        public Builder withEventManager(final EventManager eventManager) {
            assertNotNull(eventManager, "eventManager");
            this.eventManager = eventManager;
            return this;
        }

        public Builder withTrackerLocator(final TrackerLocator trackerLocator) {
            assertNotNull(trackerLocator, "trackerLocator");
            this.trackerLocator = trackerLocator;
            return this;
        }

        public Builder withKeyFactory(final StatsKeyFactory keyFactory) {
            assertNotNull(keyFactory, "keyFactory");
            this.keyFactory = keyFactory;
            return this;
        }

        public Builder withConfigFactory(final StatsConfigBuilderFactory configBuilderFactory) {
            assertNotNull(configBuilderFactory, "configBuilderFactory");
            this.configBuilderFactory = configBuilderFactory;
            return this;
        }

        public Builder withTaskService(final TaskService taskService) {
            assertNotNull(taskService, "taskService");
            this.taskService = taskService;
            return this;
        }

        public Builder withEnabled(final boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withUncaughtExceptionHandler(final UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
            return this;
        }

        public DefaultStatsManager newManager() {

            StatsKeyFactory keyFactory = this.keyFactory;

            EventManager eventManager = this.eventManager;
            StatsConfigManager configManager = this.configManager;
            StatsSessionManager sessionManager = this.sessionManager;
            TrackerLocator trackerLocator = this.trackerLocator;
            StatsConfigBuilderFactory configBuilderFactory = this.configBuilderFactory;
            TaskService taskService = this.taskService;

            if (keyFactory == null) {
                keyFactory = new DefaultStatsKeyFactory();
            }

            if (eventManager == null) {
                eventManager = new SynchronousEventManager();
            }

            if (configManager == null) {
                configManager = new DefaultStatsConfigManager(eventManager, keyFactory);
            }
            if (sessionManager == null) {
                sessionManager = new DefaultSessionManager(configManager, eventManager);
            }

            if (trackerLocator == null) {
                trackerLocator = new DefaultTrackerLocator(configManager, sessionManager);
            }

            if (configBuilderFactory == null) {
                configBuilderFactory = new DefaultStatsConfigBuilderFactory(configManager);
            }

            if (taskService == null) {
                taskService = new ThreadPoolTaskService(eventManager);
            }

            DefaultStatsManager manager = new DefaultStatsManager(namespace,
                                                                  configManager,
                                                                  sessionManager,
                                                                  eventManager,
                                                                  trackerLocator,
                                                                  keyFactory,
                                                                  configBuilderFactory,
                                                                  taskService);

            manager.setEnabled(enabled);
            if (uncaughtExceptionHandler != null) {
                manager.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }

            return manager;
        }
    }
}

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
package org.stajistics;

import org.stajistics.event.StatsEventManager;
import org.stajistics.event.SynchronousStatsEventManager;
import org.stajistics.management.DefaultStatsManagement;
import org.stajistics.management.StatsManagement;
import org.stajistics.management.StatsManagementEventHandler;
import org.stajistics.session.DefaultStatsSessionManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.CompositeStatsTracker;
import org.stajistics.tracker.DefaultManualStatsTracker;
import org.stajistics.tracker.ManualStatsTracker;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;

/**
 * The default implementation of {@link StatsManager}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsManager implements StatsManager {

    private static final long serialVersionUID = 6464098983922060895L;

    private static final String SYS_PROP_MANAGEMENT_ENABLED = StatsManagement.class.getName() + ".enabled";

    private volatile boolean enabled = true;

    protected final StatsConfigManager configManager;
    protected final StatsSessionManager sessionManager;
    protected final StatsEventManager eventManager;
    protected final StatsKeyFactory keyFactory;
    protected final StatsConfigFactory configFactory;

    /**
     * Construct a DefaultStatsManager using the given set of managers.
     *
     * @param configManager The {@link StatsConfigManager} to use.
     * @param sessionManager The {@link StatsSessionManager} to use.
     * @param eventManager The {@link StatsEventManager} to use.
     * @param keyFactory The {@link StatsKeyFactory} to use.
     */
    public DefaultStatsManager(final StatsConfigManager configManager,
                               final StatsSessionManager sessionManager,
                               final StatsEventManager eventManager,
                               final StatsKeyFactory keyFactory,
                               final StatsConfigFactory configFactory) {

        if (configManager == null) {
            throw new NullPointerException("configManager");
        }
        if (sessionManager == null) {
            throw new NullPointerException("sessionManager");
        }
        if (eventManager == null) {
            throw new NullPointerException("eventManager");
        }
        if (keyFactory == null) {
            throw new NullPointerException("keyFactory");
        }
        if (configFactory == null) {
            throw new NullPointerException("configFactory");
        }

        this.keyFactory = keyFactory;
        this.configManager = configManager;
        this.sessionManager = sessionManager;
        this.eventManager = eventManager;
        this.configFactory = configFactory;
    }

    /**
     * Create an instance of DefaultStatsManager supplying the default manager implementations.
     * Initializes a {@link DefaultStatsManagement} and configures it in the default manner.
     *
     * @return A DefaultStatsManager instance, never <tt>null</tt>.
     */
    public static DefaultStatsManager createWithDefaults() {

        StatsKeyFactory keyFactory = new DefaultStatsKeyFactory();

        StatsEventManager eventManager = new SynchronousStatsEventManager();
        StatsConfigManager configManager = new DefaultStatsConfigManager(eventManager, keyFactory);
        StatsSessionManager sessionManager = new DefaultStatsSessionManager(configManager, eventManager);

        StatsConfigFactory configFactory = new DefaultStatsConfigFactory(configManager);

        DefaultStatsManager manager = new DefaultStatsManager(configManager,
                                                              sessionManager,
                                                              eventManager,
                                                              keyFactory,
                                                              configFactory);

        if (Boolean.parseBoolean(System.getProperty(SYS_PROP_MANAGEMENT_ENABLED, "true"))) {
            StatsManagement management = new DefaultStatsManagement();
            management.registerConfigManagerMBean(manager, configManager);
            management.registerSessionManagerMBean(manager, sessionManager);

            StatsManagementEventHandler eventHandler = new StatsManagementEventHandler(manager, management);
            eventManager.addGlobalEventHandler(eventHandler);
        }

        return manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSessionManager getSessionManager() {
        return sessionManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsEventManager getEventManager() {
        return eventManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyFactory getKeyFactory() {
        return keyFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsConfigFactory getConfigFactory() {
        return configFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsTracker getTracker(final StatsKey key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsTracker tracker = null;

        if (enabled) {
            StatsConfig config = configManager.getOrCreateConfig(key);
            if (config.isEnabled()) {
                tracker = config.getTrackerFactory().createTracker(key, sessionManager);
            }
        }

        if (tracker == null) {
            tracker = NullTracker.getInstance();
        }

        return tracker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsTracker getTracker(final StatsKey... keys) {

        if (keys == null) {
            throw new NullPointerException("keys");
        }

        if (!enabled) {
            return NullTracker.getInstance();
        }

        if (keys.length == 1) {
            return getTracker(keys[0]);
        }

        final StatsTracker[] trackers = new StatsTracker[keys.length];

        for (int i = 0; i < keys.length; i++) {
            trackers[i] = getTracker(keys[i]);
        }

        return new CompositeStatsTracker(trackers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManualStatsTracker getManualTracker(final StatsKey key) {
        StatsTracker tracker = getTracker(key);

        if (tracker instanceof ManualStatsTracker) {
            return (ManualStatsTracker)tracker;
        }

        return DefaultManualStatsTracker.FACTORY.createTracker(key, sessionManager);
    }
}

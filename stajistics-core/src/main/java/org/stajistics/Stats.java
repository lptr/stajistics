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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.SynchronousStatsEventManager;
import org.stajistics.management.StatsManagement;
import org.stajistics.session.DefaultSessionManager;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.CompositeTracker;
import org.stajistics.tracker.DefaultStatsTrackerFactory;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class Stats {

    protected static final Logger logger = LoggerFactory.getLogger(Stats.class);

    private static volatile Stats instance = null;

    private static volatile boolean enabled = true;

    protected StatsConfigManager configManager;
    protected StatsSessionManager sessionManager;
    protected StatsEventManager eventManager;

    public static void loadInstance(final Stats instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }

        if (Stats.instance != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("A Stats instance has already been loaded. Replacing existing: " + 
                             Stats.instance);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded Stats: " + instance);
        }

        Stats.instance = instance;
    }

    protected static final Stats getInstance() {
        if (instance == null) {
            loadInstance(new DefaultStats());
        }

        return instance;
    }

    public static StatsSessionManager getSessionManager() {
        return getInstance().sessionManager;
    }

    public static StatsEventManager getEventManager() {
        return getInstance().eventManager;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(final boolean enabled) {
        Stats.enabled = enabled;
    }

    public static StatsConfig getConfig(final StatsKey key) {
        return getInstance().getConfigManager().getConfig(key);
    }

    public static StatsTracker getTracker(final String name) {
        return getTracker(newKey(name));
    }

    public static StatsTracker getTracker(final StatsKey key) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsTracker tracker = null;

        if (enabled) {
            tracker = getInstance().getTrackerImpl(key);
        }

        if (tracker == null) {
            tracker = NullTracker.getInstance();
        }

        return tracker;
    }

    public static StatsTracker getTracker(final StatsKey firstKey, 
                                          final StatsKey secondKey,
                                          final StatsKey... otherKeys) {

        final StatsTracker[] trackers = new StatsTracker[2 + otherKeys.length];

        trackers[0] = getTracker(firstKey);
        trackers[1] = getTracker(secondKey);

        for (int i = 0; i < otherKeys.length; i++) {
            trackers[i + 2] = getTracker(otherKeys[i]);
        }

        return new CompositeTracker(trackers);
    }

    public static StatsTracker track(final String name) {
        return getTracker(newKey(name)).track();
    }

    public static StatsTracker track(final StatsKey key) {
        return getTracker(key).track();
    }

    public static StatsTracker track(final StatsKey firstKey,
                                     final StatsKey secondKey,
                                     final StatsKey... otherKeys) {
        return getTracker(firstKey, secondKey, otherKeys).track();
    }

    public static StatsKey newKey(final String name) {
        StatsKey key = new SimpleStatsKey(name);

        StatsConfigManager configManager = getInstance().getConfigManager();
        StatsConfig config = configManager.getConfig(key);
        if (config == null) {
            config = DefaultStatsConfig.createDefaultConfig();
            configManager.putConfigIfAbsent(key, config);
        }

        return key;
    }

    public static StatsConfigBuilder buildConfig(final String name) {
        return getInstance().createConfigBuilder(name);
    }

    protected abstract StatsConfigBuilder createConfigBuilder(String name);

    protected abstract StatsConfigBuilder createConfigBuilder(StatsKey template);

    protected abstract StatsTracker getTrackerImpl(StatsKey key);

    protected StatsConfigManager getConfigManager() {
        return configManager;
    }

    /* NESTED CLASSES */

    protected static class DefaultStats extends Stats {

        //protected StatsTrackerStore trackerStore;
        protected StatsTrackerFactory trackerFactory;

        protected DefaultStats() {
            super();

            configManager = createConfigManager();
            sessionManager = createSessionManager();
            eventManager = createEventManager();
            //trackerStore = createTrackerStore();
            trackerFactory = createTrackerFactory();

            //TODO: better spot for this?
            StatsManagement.getInstance().registerSessionManagerMBean();
        }

        protected StatsConfigManager createConfigManager() {
            return new DefaultStatsConfigManager();
        }

        protected StatsSessionManager createSessionManager() {
            return new DefaultSessionManager();
        }

        protected StatsEventManager createEventManager() {
            return new SynchronousStatsEventManager();
        }
        
        /*
        protected StatsTrackerStore createTrackerStore() {
            return new ThreadLocalStatsTrackerStore(new DefaultStatsTrackerFactory());
        }
        */

        protected StatsTrackerFactory createTrackerFactory() {
            return new DefaultStatsTrackerFactory();
        }

        @Override
        protected StatsConfigBuilder createConfigBuilder(final String name) {
            return new DefaultStatsConfigBuilder(name);
        }

        @Override
        protected StatsConfigBuilder createConfigBuilder(final StatsKey template) {
            return new DefaultStatsConfigBuilder(template);
        }

        @Override
        protected StatsTracker getTrackerImpl(final StatsKey key) {

            StatsConfig config = configManager.getConfig(key);

            StatsSession session = sessionManager.getSession(key);

            /* Reusing StatsTrackers needs a lot more thought.
             * Currently it is too complicated to bother with, so just
             * create a new tracker every time.
             */
            //StatsTracker tracker = trackerStore.getTracker(statsSession);

            StatsTracker tracker = null;

            if (config.isEnabled()) {
                tracker = trackerFactory.createStatsTracker(session, 
                                                            config.getTrackerClass());
            }

            return tracker;
        }

    }
}

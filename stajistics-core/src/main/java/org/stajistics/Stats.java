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
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.CompositeStatsTracker;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;

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

    static {
        //TODO: this doesn't belong here
        new StatsManagement().initializeManagement();
    }

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

    public static StatsConfigManager getConfigManager() {
        return getInstance().configManager;
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

    public static StatsTracker getTracker(final StatsKey... keys) {
        if (keys.length == 1) {
            return getTracker(keys[0]);
        }

        final StatsTracker[] trackers = new StatsTracker[keys.length];

        for (int i = 0; i < keys.length; i++) {
            trackers[i] = getTracker(keys[i]);
        }

        return new CompositeStatsTracker(trackers);
    }

    public static StatsTracker track(final String name) {
        return getTracker(newKey(name)).track();
    }

    public static StatsTracker track(final StatsKey key) {
        return getTracker(key).track();
    }

    public static StatsTracker track(final StatsKey... keys) {
        return getTracker(keys).track();
    }

    public static void incident(final String name) {
        getTracker(newKey(name)).track().commit();
    }

    public static void incident(final StatsKey key) {
        getTracker(key).track().commit();
    }

    public static void incident(final StatsKey... keys) {
        getTracker(keys).track().commit();
    }

    public static StatsKey newKey(final String name) {
        return getInstance().createKey(name);
    }

    public static StatsKeyBuilder buildKey(final String name) {
        return getInstance().createKeyBuilder(name);
    }

    public static StatsConfigBuilder buildConfig(final String name) {
        return getInstance().createConfigBuilder(name);
    }

    protected abstract StatsKey createKey(String name);

    protected abstract StatsKeyBuilder createKeyBuilder(String name);

    protected abstract StatsKeyBuilder createKeyBuilder(StatsKey template);

    protected abstract StatsConfigBuilder createConfigBuilder(String name);

    protected abstract StatsConfigBuilder createConfigBuilder(StatsKey template);

    protected abstract StatsTracker getTrackerImpl(StatsKey key);

    /* NESTED CLASSES */

    public static class DefaultStats extends Stats {

        public DefaultStats() {
            this(new DefaultStatsConfigManager(),
                 new DefaultSessionManager(),
                 new SynchronousStatsEventManager());
        }

        public DefaultStats(final StatsConfigManager configManager,
                            final StatsSessionManager sessionManager,
                            final StatsEventManager eventManager) {
            if (configManager == null) {
                throw new NullPointerException("configManager");
            }
            if (sessionManager == null) {
                throw new NullPointerException("sessionManager");
            }
            if (eventManager == null) {
                throw new NullPointerException("eventManager");
            }

            this.configManager = configManager;
            this.sessionManager = sessionManager;
            this.eventManager = eventManager;
        }

        @Override
        protected StatsKey createKey(final String name) {
            return new SimpleStatsKey(name);
        }

        @Override
        protected StatsKeyBuilder createKeyBuilder(final String name) {
            return new DefaultStatsKeyBuilder(name);
        }

        @Override
        protected StatsKeyBuilder createKeyBuilder(final StatsKey template) {
            return new DefaultStatsKeyBuilder(template);
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

            StatsTracker tracker = null;

            StatsConfig config = configManager.getConfig(key);
            if (config.isEnabled()) {
                tracker = config.getTrackerFactory().createTracker(key);
            }

            return tracker;
        }

    }
}

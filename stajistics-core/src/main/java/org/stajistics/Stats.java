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
import org.stajistics.session.DefaultSessionManager;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.DefaultStatsTrackerFactory;
import org.stajistics.tracker.DefaultStatsTrackerStore;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.StatsTrackerStore;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class Stats {

    protected static final Logger logger = LoggerFactory.getLogger(Stats.class);

    private static Stats instance = null;

    protected volatile boolean enabled = true;

    protected StatsEventManager eventManager;


    public static synchronized void loadInstance(final Stats instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }

        if (Stats.instance != null) {
            if (logger.isWarnEnabled()) {
                logger.warn("A Stats instance has already been loaded. Replacing existing: " + Stats.instance);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded Stats: " + instance);
        }

        Stats.instance = instance;
    }

    protected static final Stats getInstance() {
        if (instance == null) { // soft check
            synchronized (Stats.class) {
                if (instance == null) { // hard check
                    loadInstance(new DefaultStatsManager());
                }
            }
        }

        return instance;
    }

    public static StatsEventManager getEventManager() {
        return getInstance().eventManager;
    }

    public static boolean isEnabled() {
        return getInstance().enabled;
    }

    public static void setEnabled(final boolean enabled) {
        getInstance().enabled = enabled;
    }

    public static StatsTracker getTracker(final String key) {
        return getTracker(new SimpleStatsKey(key));
    }

    public static StatsTracker getTracker(final StatsKey key) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsTracker tracker = null;

        if (isEnabled()) {
            tracker = getInstance().getTrackerImpl(key);
        }

        if (tracker == null) {
            tracker = NullTracker.getInstance();
        }

        return tracker;
    }

    public static StatsTracker track(final String key) {
        return getTracker(new SimpleStatsKey(key)).track();
    }

    public static StatsTracker track(final StatsKey key) {
        return getTracker(key).track();
    }

    public static StatsKey newKey(final String name) {
        return new SimpleStatsKey(name);
    }

    public static StatsKeyBuilder buildKey(final String name) {
        StatsKeyBuilder builder = getInstance().createKeyBuilder();
        builder.withName(name);
        return builder;
    }

    protected abstract StatsKeyBuilder createKeyBuilder();

    protected abstract StatsKeyBuilder createKeyBuilder(StatsKey template);

    protected abstract StatsTracker getTrackerImpl(StatsKey key);

    /* NESTED CLASSES */

    protected static class DefaultStatsManager extends Stats {

        protected StatsSessionManager sessionManager;
        protected StatsTrackerStore trackerStore;

        protected DefaultStatsManager() {
            super();

            eventManager = new SynchronousStatsEventManager();
            sessionManager = new DefaultSessionManager();
            trackerStore = new DefaultStatsTrackerStore(new DefaultStatsTrackerFactory());
        }

        @Override
        protected StatsKeyBuilder createKeyBuilder() {
            return new DefaultStatsKeyBuilder();
        }

        @Override
        protected StatsKeyBuilder createKeyBuilder(final StatsKey template) {
            return new DefaultStatsKeyBuilder(template);
        }

        @Override
        protected StatsTracker getTrackerImpl(final StatsKey key) {
            StatsSession statsSession = sessionManager.getSession(key);
            StatsTracker tracker = trackerStore.getStatsTracker(statsSession);

            return tracker;
        }

    }
}

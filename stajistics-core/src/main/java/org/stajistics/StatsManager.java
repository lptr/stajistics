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

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.SynchronousStatsEventManager;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class StatsManager {

    protected static final Logger logger = LoggerFactory.getLogger(StatsManager.class);

    private static StatsManager instance = null;

    protected final AtomicBoolean enabled = new AtomicBoolean(true);

    protected StatsEventManager eventManager = new SynchronousStatsEventManager();

    protected StatsManager() {}

    public static synchronized void loadInstance(final StatsManager instance) {
        if (instance == null) {
            throw new NullPointerException("instance");
        }

        if (StatsManager.instance != null) {
            if (logger.isWarnEnabled()) {
                logger.warn("A StatsManager has already been loaded. Replacing existing: " + StatsManager.instance);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded StatsManager: " + instance);
        }

        StatsManager.instance = instance;
    }

    protected static final StatsManager getInstance() {
        if (instance == null) { // soft check
            synchronized (StatsManager.class) {
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
        return getInstance().enabled.get();
    }

    public static StatsTracker get(final String key) {
        return get(StatsKey.create(key));
    }

    public static StatsTracker get(final StatsKey key) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        StatsTracker tracker = null;

        if (isEnabled()) {
            tracker = getInstance().getImpl(key);
        }

        if (tracker == null) {
            tracker = NullTracker.getInstance();
        }

        return tracker;
    }

    public static StatsTracker open(final String key) {
        return get(key).open();
    }

    public static StatsTracker open(final StatsKey key) {
        return get(key).open();
    }

    protected abstract StatsTracker getImpl(StatsKey key);

    /* INNER CLASSES */

    protected static class DefaultStatsManager extends StatsManager {

        protected StatsSessionManager sessionManager;
        protected StatsTrackerStore trackerStore;

        protected DefaultStatsManager() {
            sessionManager = new DefaultSessionManager();
            trackerStore = new DefaultStatsTrackerStore(new DefaultStatsTrackerFactory());
        }

        @Override
        protected StatsTracker getImpl(final StatsKey key) {
            StatsSession statsSession = sessionManager.getSession(key);
            StatsTracker tracker = trackerStore.getStatsTracker(statsSession);

            return tracker;
        }

    }
}

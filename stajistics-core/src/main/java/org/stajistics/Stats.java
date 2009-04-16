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
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public final class Stats {

    private static final Logger logger = LoggerFactory.getLogger(Stats.class);

    private static StatsManager manager;

    /**
     * Specify the sole {@link StatsManager} instance, replacing any existing instance.
     *
     * @param manager The {@link StatsManager} instance to use.
     * @see #getManager()
     */
    public static void loadManager(final StatsManager manager) {
        if (manager == null) {
            throw new NullPointerException("manager");
        }

        if (Stats.manager != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("A Stats manager has already been loaded. Replacing existing: " + 
                             Stats.manager);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded: " + StatsManager.class.getSimpleName() + ": " + manager);
        }

        Stats.manager = manager;
    }

    /**
     * Obtain the sole {@link StatsManager} instance.
     * The sole StatsManager instance can be specified using the {@link #loadManager(Stats)} method.
     * If an instance has not previously been loaded, a call to this method will
     * instantiate and load a {@link DefaultStatsManager}.
     *
     * @return A StatsManager instance. Never <tt>null</tt>. 
     * @see #loadManager(Stats)
     */
    public static StatsManager getManager() {
        if (manager == null) {
            loadManager(loadDefaultStatsManager());
        }

        return manager;
    }

    private static StatsManager loadDefaultStatsManager() {

        StatsManager manager = null;

        String managerClassName = System.getProperty(StatsManager.class.getName());
        if (managerClassName != null) {
            try {
                @SuppressWarnings("unchecked")
                Class<StatsManager> managerClass = (Class<StatsManager>)Class.forName(managerClassName);

                manager = managerClass.newInstance();

            } catch (Exception e) {
                logger.error("Failed to load " + StatsManager.class.getSimpleName() + 
                             ": " + managerClassName, e);
            }
        }

        if (manager == null) {
            manager = new DefaultStatsManager();
        }

        return manager;
    }

    /**
     * Get the {@link StatsConfigManager}.
     *
     * @return The {@link StatsConfigManager}. Never <tt>null</tt>.
     */
    public static StatsConfigManager getConfigManager() {
        return getManager().getConfigManager();
    }

    /**
     * Get the {@link StatsSessionManager}.
     *
     * @return The @link StatsSessionManager}. Never <tt>null</tt>.
     */
    public static StatsSessionManager getSessionManager() {
        return getManager().getSessionManager();
    }

    /**
     * Get the {@link StatsEventManager}.
     *
     * @return The {@link StatsEventManager}. Never <tt>null</tt>.
     */
    public static StatsEventManager getEventManager() {
        return getManager().getEventManager();
    }

    /**
     * Determine if statistics collection is enabled.
     *
     * @return <tt>true</tt> if statistics collection is enabled, <tt>false</tt> otherwise.
     */
    public static boolean isEnabled() {
        return getManager().isEnabled();
    }

    public static StatsTracker getTracker(final String name) {
        return getManager().getTracker(newKey(name));
    }

    public static StatsTracker getTracker(final StatsKey key) {
        return getManager().getTracker(key);
    }

    public static StatsTracker getTracker(final StatsKey... keys) {
        return getManager().getTracker(keys);
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
        return getManager().createKey(name);
    }

    public static StatsKeyBuilder buildKey(final String name) {
        return getManager().createKeyBuilder(name);
    }

    public static StatsConfigBuilder buildConfig(final StatsKey key) {
        return getManager().createConfigBuilder(key);
    }

}

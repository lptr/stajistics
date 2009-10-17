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
import org.stajistics.tracker.ManualStatsTracker;
import org.stajistics.tracker.StatsTracker;

/**
 * A facade to the Stajistics core API. Maintains a singleton default instance of a {@link StatsManager}.
 *
 *
 * @author The Stajistics Project
 */
public final class Stats {

    private static final Logger logger = LoggerFactory.getLogger(Stats.class);

    private static StatsManager manager;

    /**
     * Specify the sole default {@link StatsManager} instance, replacing any existing instance.
     *
     * @param manager The {@link StatsManager} instance to use.
     * @see #getManager()
     */
    public static void loadManager(final StatsManager manager) {
        if (manager == null) {
            throw new NullPointerException("manager");
        }

        if (Stats.manager != null) {
            logger.debug("A Stats manager has already been loaded. Replacing existing: {}",
                         Stats.manager);
        }

        logger.debug("Loaded: {}: {}", StatsManager.class.getSimpleName(), manager);

        Stats.manager = manager;
    }

    /**
     * Obtain the sole {@link StatsManager} instance.
     * The sole StatsManager instance can be specified using the {@link #loadManager(Stats)} method.
     * If an instance has not previously been loaded, a call to this method will
     * instantiate and load a {@link DefaultStatsManager}.
     *
     * @return A StatsManager instance, never <tt>null</tt>.
     * @see #loadManager(StatsManager)
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
                               ": " + managerClassName, 
                           e);
            }
        }

        if (manager == null) {
            manager = DefaultStatsManager.createWithDefaults();
        }

        return manager;
    }

    /**
     * Get the {@link StatsConfigManager}.
     *
     * @return The {@link StatsConfigManager}, never <tt>null</tt>.
     *
     * @see StatsManager#getConfigManager()
     */
    public static StatsConfigManager getConfigManager() {
        return getManager().getConfigManager();
    }

    /**
     * Get the {@link StatsSessionManager}.
     *
     * @return The @link StatsSessionManager}, never <tt>null</tt>.
     *
     * @see StatsManager#getSessionManager()
     */
    public static StatsSessionManager getSessionManager() {
        return getManager().getSessionManager();
    }

    /**
     * Get the {@link StatsEventManager}.
     *
     * @return The {@link StatsEventManager}, never <tt>null</tt>.
     *
     * @see StatsManager#getEventManager()
     */
    public static StatsEventManager getEventManager() {
        return getManager().getEventManager();
    }

    /**
     * Determine if statistics collection is enabled.
     *
     * @return <tt>true</tt> if statistics collection is enabled, <tt>false</tt> otherwise.
     *
     * @see StatsManager#isEnabled()
     */
    public static boolean isEnabled() {
        return getManager().isEnabled();
    }

    /**
     * Obtain a {@link StatsTracker} for the given key <tt>name</tt> that can be
     * used to collect statistics. Equivalent to calling
     * <tt>Stats.getTracker(Stats.newKey(name))</tt>.
     *
     * @param keyName The key name for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     */
    public static StatsTracker getTracker(final String keyName) {
        return getManager().getTracker(newKey(keyName));
    }

    /**
     * Obtain a {@link StatsTracker} for the given <tt>key</tt> that can be
     * used to collect statistics.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getTracker(StatsKey)
     */
    public static StatsTracker getTracker(final StatsKey key) {
        return getManager().getTracker(key);
    }

    /**
     * Obtain a {@link StatsTracker} for the given set of <tt>keys</tt> that can be used to
     * collect statistics.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getTracker(StatsKey...)
     */
    public static StatsTracker getTracker(final StatsKey... keys) {
        return getManager().getTracker(keys);
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getTracker(Stats.newKey(name)).track()</tt>.
     *
     * @param keyName The key name for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getTracker(StatsKey)
     * @see StatsTracker#track()
     */
    public static StatsTracker track(final String keyName) {
        return getTracker(newKey(keyName)).track();
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getTracker(key).track()</tt>
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getTracker(StatsKey)
     * @see StatsTracker#track()
     */
    public static StatsTracker track(final StatsKey key) {
        return getTracker(key).track();
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getTracker(keys).track()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     */
    public static StatsTracker track(final StatsKey... keys) {
        return getTracker(keys).track();
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getTracker(Stats.newKey(name)).incident()</tt>.
     *
     * @param keyName The key name for which to report an incident.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     * @see StatsTracker#commit()
     */
    public static void incident(final String keyName) {
        getTracker(newKey(keyName)).incident();
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getTracker(key).incident()</tt>.
     *
     * @param key The {@link StatsKey} for which to report an incident.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     * @see StatsTracker#commit()
     */
    public static void incident(final StatsKey key) {
        getTracker(key).incident();
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getTracker(keys).incident()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to report an incident.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     * @see StatsTracker#commit()
     */
    public static void incident(final StatsKey... keys) {
        getTracker(keys).incident();
    }

    /**
     * Report a failure.
     *
     * @param failure The Throwable that represents the failure.
     * @param keyName The key name for which to report an incident.
     */
    public static void failure(final Throwable failure,
                               final String keyName) {
        getTracker(StatsKeyUtils.keyForFailure(newKey(keyName), failure)).incident();
    }

    /**
     * Report a failure.
     *
     * @param failure The Throwable that represents the failure.
     * @param key The {@link StatsKey} for which to report a failure.
     */
    public static void failure(final Throwable failure,
                               final StatsKey key) {
        getTracker(StatsKeyUtils.keyForFailure(key, failure)).incident();
    }

    /**
     * Report a failure.
     *
     * @param keys The {@link StatsKey}s for which to report a failure.
     * @param failure The Throwable that represents the failure.
     */
    public static void failure(final Throwable failure,
                               final StatsKey... keys) {
        if (keys.length == 0) {
            throw new IllegalArgumentException("must supply at least one key");
        }

        for (StatsKey key : keys) {
            getTracker(StatsKeyUtils.keyForFailure(key, failure)).incident();
        }
    }

    /**
     * Obtain a {@link ManualStatsTracker} for the given key <tt>name</tt> that can be
     * used to report manually collected statistics.
     *
     * @param keyName The key name for which to return a manual tracker.
     * @return A {@link ManualStatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getManualTracker(StatsKey)
     */
    public static ManualStatsTracker manual(final String keyName) {
        return getManager().getManualTracker(newKey(keyName));
    }

    /**
     * Obtain a {@link ManualStatsTracker} for the given <tt>key</tt> that can be
     * used to report manually collected statistics.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link ManualStatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getManualTracker(StatsKey)
     */
    public static ManualStatsTracker manual(final StatsKey key) {
        return getManager().getManualTracker(key);
    }

    /**
     * Create a new {@link StatsKey} from the given <tt>name</tt>.
     *
     * @param name The name of the key to create.
     * @return A new {@link StatsKey}, never <tt>null</tt>.
     *
     * @see StatsManager#createKey(String)
     */
    public static StatsKey newKey(final String name) {
        return getManager().getKeyFactory()
                           .createKey(name);
    }

    /**
     * Create a new {@link StatsKeyBuilder} which can create a new {@link StatsKey}
     * for the given <tt>name</tt>.
     *
     * @param name The name of the key that the builder will create.
     * @return A {@link StatsKeyBuilder} which can be used to define key attributes, never <tt>null</tt>.
     *
     * @see StatsManager#createKeyBuilder(StatsKey)
     */
    public static StatsKeyBuilder buildKey(final String name) {
        return getManager().getKeyFactory()
                           .createKeyBuilder(name);
    }

    /**
     * Create a new {@link StatsConfigBuilder} which can assemble various configurations.
     *
     * @return A {@link StatsKeyBuilder} which can be used to specify configuration, never <tt>null</tt>.
     *
     * @see StatsManager#createConfigBuilder()
     */
    public static StatsConfigBuilder buildConfig() {
        return getManager().getConfigFactory()
                           .createConfigBuilder();
    }

}

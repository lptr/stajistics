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
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.StatsTrackerLocator;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * A facade to the Stajistics core API. Maintains a singleton default instance of a {@link StatsManager}.
 * Provides static convenience methods for manipulating the underlying API.
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
    public static synchronized void loadManager(final StatsManager manager) {
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
            synchronized (Stats.class) {
                if (manager == null) {
                    loadManager(loadDefaultStatsManager());
                }
            }
        }

        return manager;
    }

    protected static StatsManager loadDefaultStatsManager() {

        StatsManager manager = null;

        try {
            manager = loadStatsManagerFromSystemProperties();

        } catch (Exception e) {
            logger.error("Failed to load " + StatsManager.class.getSimpleName() + 
                           ": " + e.toString(), 
                         e);
        }

        if (manager == null) {
            manager = DefaultStatsManager.createWithDefaults();
        }

        return manager;
    }

    protected static StatsManager loadStatsManagerFromSystemProperties() throws Exception {
        StatsManager manager = null;

        String managerClassName = System.getProperty(StatsManager.class.getName());
        if (managerClassName != null) {
            @SuppressWarnings("unchecked")
            Class<StatsManager> managerClass = (Class<StatsManager>)Class.forName(managerClassName);

            manager = managerClass.newInstance();
        }

        return manager;
    }

    private static StatsTrackerLocator getTrackerLocator() {
        return getManager().getTrackerLocator();
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
        try {
            return getTrackerLocator().getTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
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
        try {
            return getTrackerLocator().getTracker(key);
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    public static SpanTracker getSpanTracker(final String keyName) {
        try {
            return getTrackerLocator().getSpanTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    public static SpanTracker getSpanTracker(final StatsKey key) {
        try {
            return getTrackerLocator().getSpanTracker(key);
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    public static SpanTracker getSpanTracker(final StatsKey... keys) {
        try {
            return getTrackerLocator().getSpanTracker(keys);
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
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
    public static SpanTracker start(final String keyName) {
        try {
            return getTrackerLocator().getSpanTracker(newKey(keyName))
                                      .start();
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(key).start()</tt>
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getSpanTracker(StatsKey)
     * @see StatsTracker#track()
     */
    public static SpanTracker start(final StatsKey key) {
        try {
            return getTrackerLocator().getSpanTracker(key)
                                      .start();
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getTracker(keys).start()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     */
    public static SpanTracker start(final StatsKey... keys) {
        try {
            return getTrackerLocator().getSpanTracker(keys)
                                      .start();
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(Stats.newKey(name)).incident()</tt>.
     *
     * @param keyName The key name for which to report an incident.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     * @see StatsTracker#commit()
     */
    public static void incident(final String keyName) {
        try {
            getTrackerLocator().getIncidentTracker(newKey(keyName))
                               .incident();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(key).incident()</tt>.
     *
     * @param key The {@link StatsKey} for which to report an incident.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     * @see StatsTracker#commit()
     */
    public static void incident(final StatsKey key) {
        try {
            getTrackerLocator().getIncidentTracker(key)
                               .incident();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(keys).incident()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to report an incident.
     *
     * @see StatsManager#getTracker(StatsKey...)
     * @see StatsTracker#track()
     * @see StatsTracker#commit()
     */
    public static void incident(final StatsKey... keys) {
        try {
            getTrackerLocator().getIncidentTracker(keys)
                               .incident();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Report a failure.
     *
     * @param failure The Throwable that represents the failure.
     * @param keyName The key name for which to report an incident.
     */
    public static void failure(final Throwable failure,
                               final String keyName) {
        try {
            getTrackerLocator().getIncidentTracker(StatsKeyUtils.keyForFailure(newKey(keyName), 
                                                                               failure))
                               .incident();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Report a failure.
     *
     * @param failure The Throwable that represents the failure.
     * @param key The {@link StatsKey} for which to report a failure.
     */
    public static void failure(final Throwable failure,
                               final StatsKey key) {
        try {
            getTrackerLocator().getIncidentTracker(StatsKeyUtils.keyForFailure(key, failure))
                               .incident();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Report a failure.
     *
     * @param keys The {@link StatsKey}s for which to report a failure.
     * @param failure The Throwable that represents the failure.
     */
    public static void failure(final Throwable failure,
                               final StatsKey... keys) {
        try {
            if (keys.length == 0) {
                throw new IllegalArgumentException("must supply at least one key");
            }

            StatsTrackerLocator trackerLocator = getTrackerLocator();

            for (StatsKey key : keys) {
                trackerLocator.getIncidentTracker(StatsKeyUtils.keyForFailure(key, failure))
                              .incident();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * Obtain an {@link IncidentTracker} for the given key <tt>name</tt> that can be
     * used to report incidents of events.
     *
     * @param keyName The key name for which to return an incident tracker.
     * @return An {@link IncidentTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getIncidentTracker(StatsKey)
     */
    public static IncidentTracker getIncidentTracker(final String keyName) {
        try {
            return getTrackerLocator().getIncidentTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain an {@link IncidentTracker} for the given <tt>key</tt> that can be
     * used to report incidents of events.
     *
     * @param key The {@link StatsKey} for which to return an incident tracker.
     * @return An {@link IncidentTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getIncidentTracker(StatsKey)
     */
    public static IncidentTracker getIncidentTracker(final StatsKey key) {
        try {
            return getTrackerLocator().getIncidentTracker(key);
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain a {@link ManualTracker} for the given key <tt>name</tt> that can be
     * used to report manually collected statistics.
     *
     * @param keyName The key name for which to return a manual tracker.
     * @return A {@link ManualTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getManualTracker(StatsKey)
     */
    public static ManualTracker getManualTracker(final String keyName) {
        try {
            return getTrackerLocator().getManualTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain a {@link ManualTracker} for the given <tt>key</tt> that can be
     * used to report manually collected statistics.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link ManualTracker}, never <tt>null</tt>.
     *
     * @see StatsManager#getManualTracker(StatsKey)
     */
    public static ManualTracker getManualTracker(final StatsKey key) {
        try {
            return getTrackerLocator().getManualTracker(key);
        } catch (Exception e) {
            logger.error("", e);
            return NullTracker.getInstance();
        }
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
        try {
            return getManager().getKeyFactory()
                               .createKey(name);
        } catch (Exception e) {
            logger.error("", e);
            return NullStatsKey.getInstance();
        }
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
        try {
            return getManager().getKeyFactory()
                               .createKeyBuilder(name);
        } catch (Exception e) {
            logger.error("", e);
            return NullStatsKeyBuilder.getInstance();
        }
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

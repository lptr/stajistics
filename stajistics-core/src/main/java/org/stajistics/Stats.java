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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.event.EventManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.Tracker;
import org.stajistics.tracker.TrackerLocator;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * <p>A facade to the Stajistics core API. Maintains a singleton default instance of a
 * {@link StatsManager}. Provides static convenience methods for manipulating the
 * underlying API.</p>
 *
 * <p>The methods in this class that return {@link Tracker} instances do not throw
 * Exceptions, checked nor unchecked. Rather, these methods catch and log Exceptions and return
 * a no-operation {@link Tracker} instance. The necessity in this design is to shield a
 * client application from any problems related to invoking statistics collection, possibly caused
 * by misconfiguration. To bypass this Exception-swallowing behaviour, the use of this facade
 * can be discarded and the underlying API can be accessed directly.</p>
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
     * The sole StatsManager instance can be specified using the {@link #loadManager(StatsManager)} method.
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
            manager = loadStatsManagerFromProperties();

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

    protected static StatsManager loadStatsManagerFromProperties() throws Exception {
        StatsManager manager = null;

        String managerClassName = StatsProperties.getProperty(StatsManager.class.getName());
        System.out.println("Loading StatsManager: " + managerClassName);
        if (managerClassName != null) {
            @SuppressWarnings("unchecked")
            Class<StatsManager> managerClass = (Class<StatsManager>)Class.forName(managerClassName);

            manager = managerClass.newInstance();
        }

        return manager;
    }

    private static TrackerLocator getTrackerLocator() {
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
     * Get the {@link EventManager}.
     *
     * @return The {@link EventManager}, never <tt>null</tt>.
     *
     * @see StatsManager#getEventManager()
     */
    public static EventManager getEventManager() {
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
     * Obtain a {@link SpanTracker} for the given <tt>keyName</tt> that can be used
     * to collect statistics related to some span. Equivalent to calling
     * <tt>Stats.getSpanTracker(Stats.newKey(name))</tt>.
     *
     * @param keyName The key name for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    public static SpanTracker getSpanTracker(final String keyName) {
        try {
            return getTrackerLocator().getSpanTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("Failed to obtain a " + SpanTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain a {@link SpanTracker} for the given <tt>key</tt> that can be used
     * to collect statistics related to some span.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    public static SpanTracker getSpanTracker(final StatsKey key) {
        try {
            return getTrackerLocator().getSpanTracker(key);
        } catch (Exception e) {
            logger.error("Failed to obtain a " + SpanTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain a {@link SpanTracker} for the given <tt>keys</tt> that can be used
     * to collect statistics related to some span.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    public static SpanTracker getSpanTracker(final StatsKey... keys) {
        try {
            return getTrackerLocator().getSpanTracker(keys);
        } catch (Exception e) {
            logger.error("Failed to obtain a " + SpanTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(Stats.newKey(keyName)).track()</tt>.
     *
     * @param keyName The key name for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getSpanTracker(StatsKey)
     * @see SpanTracker#track()
     */
    public static SpanTracker track(final String keyName) {
        try {
            return getTrackerLocator().getSpanTracker(newKey(keyName))
                                      .track();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke track on " + SpanTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(key).track()</tt>
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getSpanTracker(StatsKey)
     * @see SpanTracker#track()
     */
    public static SpanTracker track(final StatsKey key) {
        try {
            return getTrackerLocator().getSpanTracker(key)
                                      .track();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke track on " + SpanTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(keys).track()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getSpanTracker(StatsKey...)
     * @see SpanTracker#track()
     */
    public static SpanTracker track(final StatsKey... keys) {
        try {
            return getTrackerLocator().getSpanTracker(keys)
                                      .track();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke track on " + SpanTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain an {@link IncidentTracker} for the given <tt>keyName</tt> that can be
     * used to report incidents of events.
     *
     * @param keyName The key name for which to return an incident tracker.
     * @return An {@link IncidentTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getIncidentTracker(StatsKey)
     */
    public static IncidentTracker getIncidentTracker(final String keyName) {
        try {
            return getTrackerLocator().getIncidentTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("Failed to obtain an " + IncidentTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain an {@link IncidentTracker} for the given <tt>key</tt> that can be
     * used to report incidents of events.
     *
     * @param key The {@link StatsKey} for which to return an incident tracker.
     * @return An {@link IncidentTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getIncidentTracker(StatsKey)
     */
    public static IncidentTracker getIncidentTracker(final StatsKey key) {
        try {
            return getTrackerLocator().getIncidentTracker(key);
        } catch (Exception e) {
            logger.error("Failed to obtain an " + IncidentTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(Stats.newKey(keyName)).incident()</tt>.
     *
     * @param keyName The key name for which to report an incident.
     *
     * @see TrackerLocator#getIncidentTracker(StatsKey...)
     * @see IncidentTracker#incident()
     */
    public static void incident(final String keyName) {
        try {
            getTrackerLocator().getIncidentTracker(newKey(keyName))
                               .incident();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke an " + IncidentTracker.class.getSimpleName(), e);
        }
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(key).incident()</tt>.
     *
     * @param key The {@link StatsKey} for which to report an incident.
     *
     * @see TrackerLocator#getIncidentTracker(StatsKey...)
     * @see IncidentTracker#incident()
     */
    public static void incident(final StatsKey key) {
        try {
            getTrackerLocator().getIncidentTracker(key)
                               .incident();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke an " + IncidentTracker.class.getSimpleName(), e);
        }
    }

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(keys).incident()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to report an incident.
     *
     * @see TrackerLocator#getIncidentTracker(StatsKey...)
     * @see IncidentTracker#incident()
     */
    public static void incident(final StatsKey... keys) {
        try {
            getTrackerLocator().getIncidentTracker(keys)
                               .incident();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke an " + IncidentTracker.class.getSimpleName(), e);
        }
    }

    /**
     * Report a failure that is represented by a Throwable.
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
            logger.error("Failed to report a failure", e);
        }
    }

    /**
     * Report a failure that is represented by a Throwable.
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
            logger.error("Failed to report a failure", e);
        }
    }

    /**
     * Report a failure that is represented by a Throwable.
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

            TrackerLocator trackerLocator = getTrackerLocator();

            for (StatsKey key : keys) {
                trackerLocator.getIncidentTracker(StatsKeyUtils.keyForFailure(key, failure))
                              .incident();
            }
        } catch (Exception e) {
            logger.error("Failed to report a failure", e);
        }
    }

    /**
     * Obtain a {@link ManualTracker} for the given key <tt>keyName</tt> that can be
     * used to report manually collected statistics.
     *
     * @param keyName The key name for which to return a manual tracker.
     * @return A {@link Tracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getManualTracker(StatsKey)
     */
    public static ManualTracker getManualTracker(final String keyName) {
        try {
            return getTrackerLocator().getManualTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("Failed to obtain a " + ManualTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Obtain a {@link ManualTracker} for the given <tt>key</tt> that can be
     * used to report manually collected statistics.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link Tracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see TrackerLocator#getManualTracker(StatsKey)
     */
    public static ManualTracker getManualTracker(final StatsKey key) {
        try {
            return getTrackerLocator().getManualTracker(key);
        } catch (Exception e) {
            logger.error("Failed to obtain a " + ManualTracker.class.getSimpleName(), e);
            return NullTracker.getInstance();
        }
    }

    /**
     * Create a new {@link StatsKey} from the given <tt>name</tt>.
     *
     * @param name The name of the key to create.
     * @return A new {@link StatsKey} instance or a {@link NullStatsKey}
     *         if an Exception occurred, never <tt>null</tt>.
     *
     * @see StatsKeyFactory#createKey(String)
     */
    public static StatsKey newKey(final String name) {
        try {
            return getManager().getKeyFactory()
                               .createKey(name);
        } catch (Exception e) {
            logger.error("Failed to create a " + StatsKey.class.getSimpleName(), e);
            return NullStatsKey.getInstance();
        }
    }

    /**
     * Create a new {@link StatsKeyBuilder} which can create a new {@link StatsKey}
     * for the given <tt>name</tt>.
     *
     * @param name The name of the key that the builder will create.
     * @return A {@link StatsKeyBuilder} which can be used to define key attributes,
     *         or a {@link NullStatsKeyBuilder} if an Exception occurred, never <tt>null</tt>.
     *
     * @see StatsKeyFactory#createKeyBuilder(StatsKey)
     */
    public static StatsKeyBuilder buildKey(final String name) {
        try {
            return getManager().getKeyFactory()
                               .createKeyBuilder(name);
        } catch (Exception e) {
            logger.error("Failed to create a " + StatsKeyBuilder.class.getSimpleName(), e);
            return NullStatsKeyBuilder.getInstance();
        }
    }

    /**
     * Create a new {@link StatsConfigBuilder} which can assemble various configurations.
     *
     * @return A {@link StatsKeyBuilder} which can be used to specify configuration, never <tt>null</tt>.
     *
     * @see StatsConfigFactory#createConfigBuilder()
     */
    public static StatsConfigBuilder buildConfig() {
        return getManager().getConfigFactory()
                           .createConfigBuilder();
    }

}

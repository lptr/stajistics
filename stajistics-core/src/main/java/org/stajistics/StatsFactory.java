package org.stajistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.configuration.StatsConfigBuilder;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.Tracker;
import org.stajistics.tracker.TrackerLocator;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsFactory {

    private static final Logger logger = LoggerFactory.getLogger(StatsFactory.class);

    private final StatsManager statsManager;

    public StatsFactory(final StatsManager statsManager) {
        if (statsManager == null) {
            throw new NullPointerException("statsManager");
        }
        this.statsManager = statsManager;
    }

    public static StatsFactory forClass(final Class<?> aClass) {
        if (aClass == null) {
            throw new NullPointerException("aClass");
        }

        Class<?> cls = aClass;
        while (cls != Object.class) {
            String namespace = cls.getPackage().getName();
            if (StatsManagerRegistry.isStatsManagerDefined(namespace)) {
                StatsManager statsManager = StatsManagerRegistry.getStatsManager(namespace);
                if (statsManager != null) {
                    return new StatsFactory(statsManager);
                }
            }

            cls = cls.getSuperclass();
        }

        throw new StatsNamespaceNotFoundException("No namespaces found for class: " + aClass.getName());
    }

    public static StatsFactory forNamespace(final String namespace) {
        if (namespace == null) {
            throw new NullPointerException("namespace");
        }

        if (StatsManagerRegistry.isStatsManagerDefined(namespace)) {
            StatsManager statsManager = StatsManagerRegistry.getStatsManager(namespace);
            if (statsManager != null) {
                return new StatsFactory(statsManager);
            }
        }

        throw new StatsNamespaceNotFoundException(namespace);
    }

    public StatsManager getManager() {
        return statsManager;
    }

    /**
     * Determine if statistics collection is enabled.
     *
     * @return <tt>true</tt> if statistics collection is enabled, <tt>false</tt> otherwise.
     *
     * @see StatsManager#isEnabled()
     */
    public boolean isEnabled() {
        return statsManager.isEnabled();
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
    public SpanTracker getSpanTracker(final String keyName) {
        try {
            return statsManager.getTrackerLocator()
                               .getSpanTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("Failed to obtain a " + SpanTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public SpanTracker getSpanTracker(final StatsKey key) {
        try {
            return statsManager.getTrackerLocator()
                               .getSpanTracker(key);
        } catch (Exception e) {
            logger.error("Failed to obtain a " + SpanTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
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
    public SpanTracker getSpanTracker(final StatsKey... keys) {
        try {
            return statsManager.getTrackerLocator()
                               .getSpanTracker(keys);
        } catch (Exception e) {
            logger.error("Failed to obtain a " + SpanTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public SpanTracker track(final String keyName) {
        try {
            return statsManager.getTrackerLocator()
                               .getSpanTracker(newKey(keyName))
                               .track();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke track on " + SpanTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public SpanTracker track(final StatsKey key) {
        try {
            return statsManager.getTrackerLocator()
                               .getSpanTracker(key)
                               .track();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke track on " + SpanTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
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
    public SpanTracker track(final StatsKey... keys) {
        try {
            return statsManager.getTrackerLocator()
                               .getSpanTracker(keys)
                               .track();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke track on " + SpanTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public IncidentTracker getIncidentTracker(final String keyName) {
        try {
            return statsManager.getTrackerLocator()
                               .getIncidentTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("Failed to obtain an " + IncidentTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public IncidentTracker getIncidentTracker(final StatsKey key) {
        try {
            return statsManager.getTrackerLocator()
                               .getIncidentTracker(key);
        } catch (Exception e) {
            logger.error("Failed to obtain an " + IncidentTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
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
    public void incident(final String keyName) {
        try {
            statsManager.getTrackerLocator()
                        .getIncidentTracker(newKey(keyName))
                        .incident();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke an " + IncidentTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public void incident(final StatsKey key) {
        try {
            statsManager.getTrackerLocator()
                        .getIncidentTracker(key)
                        .incident();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke an " + IncidentTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
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
    public void incident(final StatsKey... keys) {
        try {
            statsManager.getTrackerLocator()
                        .getIncidentTracker(keys)
                        .incident();
        } catch (Exception e) {
            logger.error("Failed to obtain and invoke an " + IncidentTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
        }
    }

    /**
     * Report a failure that is represented by a Throwable.
     *
     * @param failure The Throwable that represents the failure.
     * @param keyName The key name for which to report an incident.
     */
    public void failure(final Throwable failure,
                        final String keyName) {
        try {
            statsManager.getTrackerLocator()
                        .getIncidentTracker(StatsKeyUtil.keyForFailure(newKey(keyName), failure))
                        .incident();
        } catch (Exception e) {
            logger.error("Failed to report a failure", e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
        }
    }

    /**
     * Report a failure that is represented by a Throwable.
     *
     * @param failure The Throwable that represents the failure.
     * @param key The {@link StatsKey} for which to report a failure.
     */
    public void failure(final Throwable failure,
                        final StatsKey key) {
        try {
            statsManager.getTrackerLocator()
                        .getIncidentTracker(StatsKeyUtil.keyForFailure(key, failure))
                        .incident();
        } catch (Exception e) {
            logger.error("Failed to report a failure", e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
        }
    }

    /**
     * Report a failure that is represented by a Throwable.
     *
     * @param keys The {@link StatsKey}s for which to report a failure.
     * @param failure The Throwable that represents the failure.
     */
    public void failure(final Throwable failure,
                        final StatsKey... keys) {
        try {
            if (keys.length == 0) {
                throw new IllegalArgumentException("must supply at least one key");
            }

            final TrackerLocator trackerLocator = statsManager.getTrackerLocator();

            for (StatsKey key : keys) {
                trackerLocator.getIncidentTracker(StatsKeyUtil.keyForFailure(key, failure))
                              .incident();
            }
        } catch (Exception e) {
            logger.error("Failed to report a failure", e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public ManualTracker getManualTracker(final String keyName) {
        try {
            return statsManager.getTrackerLocator()
                               .getManualTracker(newKey(keyName));
        } catch (Exception e) {
            logger.error("Failed to obtain a " + ManualTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public ManualTracker getManualTracker(final StatsKey key) {
        try {
            return statsManager.getTrackerLocator()
                               .getManualTracker(key);
        } catch (Exception e) {
            logger.error("Failed to obtain a " + ManualTracker.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
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
    public StatsKey newKey(final String name) {
        try {
            return statsManager.getKeyFactory()
                               .createKey(name);
        } catch (Exception e) {
            logger.error("Failed to create a " + StatsKey.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
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
    public StatsKeyBuilder buildKey(final String name) {
        try {
            return statsManager.getKeyFactory()
                               .createKeyBuilder(name);
        } catch (Exception e) {
            logger.error("Failed to create a " + StatsKeyBuilder.class.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
            return NullStatsKeyBuilder.getInstance();
        }
    }

    /**
     * Create a new {@link StatsConfigBuilder} which can assemble various configurations.
     *
     * @return A {@link StatsKeyBuilder} which can be used to specify configuration, never <tt>null</tt>.
     *
     * @see org.stajistics.configuration.StatsConfigBuilderFactory#createConfigBuilder()
     */
    public StatsConfigBuilder buildConfig() {
        return statsManager.getConfigBuilderFactory()
                           .createConfigBuilder();
    }

}

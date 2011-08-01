package org.stajistics;

import static org.stajistics.Util.assertNotEmpty;
import static org.stajistics.Util.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigBuilder;
import org.stajistics.tracker.NullTracker;
import org.stajistics.tracker.Tracker;
import org.stajistics.tracker.incident.CompositeIncidentTracker;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.CompositeSpanTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * <p>A facade to the Stajistics core API. Maintains an instance of a
 * {@link StatsManager}. Provides convenience methods for manipulating the
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
public interface StatsFactory {

    StatsManager getManager();

    /**
     * Determine if statistics collection is enabled.
     *
     * @return <tt>true</tt> if statistics collection is enabled, <tt>false</tt> otherwise.
     *
     * @see StatsManager#isEnabled()
     */
    boolean isEnabled();


    /**
     * Obtain a {@link SpanTracker} for the given <tt>keyName</tt> that can be used
     * to collect statistics related to some span. Equivalent to calling
     * <tt>Stats.getSpanTracker(Stats.newKey(name))</tt>.
     *
     * @param keyName The key name for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    SpanTracker getSpanTracker(String keyName);

    /**
     * Obtain a {@link SpanTracker} for the given <tt>key</tt> that can be used
     * to collect statistics related to some span.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    SpanTracker getSpanTracker(StatsKey key);

    /**
     * Obtain a {@link SpanTracker} for the given <tt>keys</tt> that can be used
     * to collect statistics related to some span.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    SpanTracker getSpanTracker(StatsKey... keys);

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(Stats.newKey(keyName)).track()</tt>.
     *
     * @param keyName The key name for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see SpanTracker#track()
     */
    SpanTracker track(String keyName);

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(key).track()</tt>
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see SpanTracker#track()
     */
    SpanTracker track(StatsKey key);

    /**
     * A convenience method equivalent to calling:
     * <tt>Stats.getSpanTracker(keys).track()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link SpanTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     * @see SpanTracker#track()
     */
    SpanTracker track(StatsKey... keys);

    /**
     * Obtain an {@link IncidentTracker} for the given <tt>keyName</tt> that can be
     * used to report incidents of events.
     *
     * @param keyName The key name for which to return an incident tracker.
     * @return An {@link IncidentTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     *
     */
    IncidentTracker getIncidentTracker(String keyName);

    /**
     * Obtain an {@link IncidentTracker} for the given <tt>key</tt> that can be
     * used to report incidents of events.
     *
     * @param key The {@link StatsKey} for which to return an incident tracker.
     * @return An {@link IncidentTracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    IncidentTracker getIncidentTracker(StatsKey key);

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(Stats.newKey(keyName)).incident()</tt>.
     *
     * @param keyName The key name for which to report an incident.
     *
     * @see IncidentTracker#incident()
     */
    void incident(String keyName);

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(key).incident()</tt>.
     *
     * @param key The {@link StatsKey} for which to report an incident.
     *
     * @see IncidentTracker#incident()
     */
    void incident(final StatsKey key);

    /**
     * Report an incident. Equivalent to calling:
     * <tt>Stats.getIncidentTracker(keys).incident()</tt>.
     *
     * @param keys The {@link StatsKey}s for which to report an incident.
     *
     * @see IncidentTracker#incident()
     */
    void incident(final StatsKey... keys);

    /**
     * Report a failure that is represented by a Throwable.
     *
     * @param failure The Throwable that represents the failure.
     * @param keyName The key name for which to report an incident.
     */
    void failure(final Throwable failure,
                        final String keyName);

    /**
     * Report a failure that is represented by a Throwable.
     *
     * @param failure The Throwable that represents the failure.
     * @param key The {@link StatsKey} for which to report a failure.
     */
    void failure(Throwable failure, StatsKey key);

    /**
     * Report a failure that is represented by a Throwable.
     *
     * @param keys The {@link StatsKey}s for which to report a failure.
     * @param failure The Throwable that represents the failure.
     */
    void failure(Throwable failure, StatsKey... keys);

    /**
     * Obtain a {@link ManualTracker} for the given key <tt>keyName</tt> that can be
     * used to report manually collected statistics.
     *
     * @param keyName The key name for which to return a manual tracker.
     * @return A {@link Tracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    ManualTracker getManualTracker(String keyName);

    /**
     * Obtain a {@link ManualTracker} for the given <tt>key</tt> that can be
     * used to report manually collected statistics.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link Tracker} instance,
     *         or a {@link NullTracker} if an Exception occurred, never <tt>null</tt>.
     */
    ManualTracker getManualTracker(StatsKey key);

    /**
     * Create a new {@link StatsKey} from the given <tt>name</tt>.
     *
     * @param name The name of the key to create.
     * @return A new {@link StatsKey} instance or a {@link NullStatsKey}
     *         if an Exception occurred, never <tt>null</tt>.
     *
     * @see StatsKeyFactory#createKey(String)
     */
    StatsKey newKey(String name);

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
    StatsKeyBuilder buildKey(String name);

    /**
     * Create a new {@link StatsConfigBuilder} which can assemble various configurations.
     *
     * @return A {@link StatsKeyBuilder} which can be used to specify configuration, never <tt>null</tt>.
     *
     * @see org.stajistics.configuration.StatsConfigBuilderFactory#createConfigBuilder()
     */
    StatsConfigBuilder buildConfig();

}

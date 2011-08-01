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
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsFactory implements StatsFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStatsFactory.class);

    protected final StatsManager statsManager;

    public DefaultStatsFactory(final StatsManager statsManager) {
        assertNotNull(statsManager, "statsManager");
        this.statsManager = statsManager;
    }

    @Override
    public StatsManager getManager() {
        return statsManager;
    }

    @Override
    public boolean isEnabled() {
        return statsManager.isEnabled();
    }

    @SuppressWarnings("unchecked")
    protected <T extends Tracker> T getTracker(final Class<T> expectedTrackerType, final StatsKey key) {
        Tracker result = null;

        try {
            assertNotNull(key, "key");

            if (statsManager.isEnabled()) {
                StatsConfig config = statsManager.getConfigManager().getOrCreateConfig(key/*, expectedTrackerType*/);
                if (config.isEnabled()) {
                    result = config.getTrackerFactory()
                                    .createTracker(key, statsManager.getSessionManager());
                }
            }

        } catch (Exception e) {
            logger.error("Failed to obtain a " + expectedTrackerType.getSimpleName(), e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
        }

        if (result == null) {
            result = NullTracker.getInstance();
        }

        return (T) result;
    }

    @SuppressWarnings("unchecked")
    protected <T extends Tracker> T getCompositeTracker(final Class<T> expectedTrackerType, final StatsKey... keys) {

        Tracker result = null;

        try {
            assertNotNull(expectedTrackerType, "expectedTrackerType");
            assertNotEmpty(keys, "keys");
            if (keys.length == 1) {
                return getTracker(expectedTrackerType, keys[0]);
            }

            if (expectedTrackerType == SpanTracker.class) {
                final SpanTracker[] trackers = new SpanTracker[keys.length];
                for (int i = 0; i < keys.length; i++) {
                    trackers[i] = getTracker(SpanTracker.class, keys[i]);
                }

                result = new CompositeSpanTracker(trackers);

            } else if (expectedTrackerType == IncidentTracker.class) {
                final IncidentTracker[] trackers = new IncidentTracker[keys.length];
                for (int i = 0; i < keys.length; i++) {
                    trackers[i] = getTracker(IncidentTracker.class, keys[i]);
                }

                result = new CompositeIncidentTracker(trackers);

            } else {
                throw new RuntimeException("Tracker type does not support composites: " + expectedTrackerType.getName());
            }

        } catch (Exception e) {
            logger.error("Failed to obtain a " + expectedTrackerType.getSimpleName(), e);

            StatsKey key = null;
            if (keys.length > 0) {
                key = keys[0];
            }

            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(key, e);
        }

        if (result == null) {
            result = NullTracker.getInstance();
        }

        return (T) result;
    }

    @Override
    public SpanTracker getSpanTracker(final String keyName) {
        return getTracker(SpanTracker.class, newKey(keyName));
    }

    @Override
    public SpanTracker getSpanTracker(final StatsKey key) {
        return getTracker(SpanTracker.class, key);
    }

    @Override
    public SpanTracker getSpanTracker(final StatsKey... keys) {
        return getCompositeTracker(SpanTracker.class, keys);
    }

    @Override
    public SpanTracker track(final String keyName) {
        return getTracker(SpanTracker.class, newKey(keyName)).track();
    }

    @Override
    public SpanTracker track(final StatsKey key) {
        return getTracker(SpanTracker.class, key).track();
    }

    @Override
    public SpanTracker track(final StatsKey... keys) {
        return getCompositeTracker(SpanTracker.class, keys).track();
    }


    @Override
    public IncidentTracker getIncidentTracker(final String keyName) {
        return getTracker(IncidentTracker.class, newKey(keyName));
    }

    @Override
    public IncidentTracker getIncidentTracker(final StatsKey key) {
        return getTracker(IncidentTracker.class, key);
    }

    @Override
    public void incident(final String keyName) {
        getTracker(IncidentTracker.class, newKey(keyName)).incident();
    }

    @Override
    public void incident(final StatsKey key) {
        getTracker(IncidentTracker.class, key).incident();
    }

    @Override
    public void incident(final StatsKey... keys) {
        getCompositeTracker(IncidentTracker.class, keys).incident();
    }

    @Override
    public void failure(final Throwable failure,
                        final String keyName) {
        getIncidentTracker(StatsKeyUtil.keyForFailure(newKey(keyName), failure)).incident();
    }

    @Override
    public void failure(final Throwable failure, final StatsKey key) {
        getIncidentTracker(StatsKeyUtil.keyForFailure(key, failure)).incident();
    }

    @Override
    public void failure(final Throwable failure, final StatsKey... keys) {
        try {
            assertNotEmpty(keys, "keys");

            for (StatsKey key : keys) {
                getIncidentTracker(StatsKeyUtil.keyForFailure(key, failure)).incident();
            }
        } catch (Exception e) {
            logger.error("Failed to report a failure", e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
        }
    }

    @Override
    public ManualTracker getManualTracker(final String keyName) {
        return getTracker(ManualTracker.class, newKey(keyName));
    }

    @Override
    public ManualTracker getManualTracker(final StatsKey key) {
        return getTracker(ManualTracker.class, key);
    }

    @Override
    public StatsKey newKey(final String name) {
        try {
            return statsManager.getKeyFactory()
                               .createKey(name);
        } catch (Exception e) {
            logger.error("Failed to create a " + StatsKey.class.getSimpleName() + " for key name: " + name, e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
            return NullStatsKey.getInstance();
        }
    }

    @Override
    public StatsKeyBuilder buildKey(final String name) {
        try {
            return statsManager.getKeyFactory()
                               .createKeyBuilder(name);
        } catch (Exception e) {
            logger.error("Failed to create a " + StatsKeyBuilder.class.getSimpleName() + " for key name: " + name, e);
            statsManager.getUncaughtExceptionHandler()
                        .uncaughtException(null, e);
            return NullStatsKeyBuilder.getInstance();
        }
    }

    @Override
    public StatsConfigBuilder buildConfig() {
        return statsManager.getConfigBuilderFactory()
                           .createConfigBuilder();
    }

    
}

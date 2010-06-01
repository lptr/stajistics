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
package org.stajistics.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.incident.CompositeIncidentTracker;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.CompositeSpanTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * The default {@link TrackerLocator} implementation which locates appropriate
 * {@link Tracker} instances using {@link StatsKey}'s associated {@link org.stajistics.configuration.StatsConfig} and
 * {@link TrackerFactory}.
 *
 * @author The Stajistics Project
 */
public class DefaultTrackerLocator implements TrackerLocator {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTrackerLocator.class);

    private final StatsConfigManager configManager;
    private final StatsSessionManager sessionManager;

    public DefaultTrackerLocator(final StatsConfigManager configManager,
                                 final StatsSessionManager sessionManager) {
        if (configManager == null) {
            throw new NullPointerException("configManager");
        }
        if (sessionManager == null) {
            throw new NullPointerException("sessionManager");
        }

        this.configManager = configManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public Tracker getTracker(final StatsKey key) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        Tracker tracker = null;

        StatsConfig config = configManager.getOrCreateConfig(key);
        if (config.isEnabled()) {
            tracker = config.getTrackerFactory()
                            .createTracker(key, sessionManager);
        }

        if (tracker == null) {
            tracker = NullTracker.getInstance();
        }

        return tracker;
    }

    @Override
    public SpanTracker getSpanTracker(StatsKey key) {

        Tracker tracker = getTracker(key);

        try {
            return (SpanTracker)tracker;

        } catch (ClassCastException cce) {
            logUnsuitableTrackerImpl(cce,
                                     "span",
                                     key,
                                     tracker.getClass(),
                                     SpanTracker.class);
        }

        return SpanTracker.FACTORY.createTracker(key, sessionManager);
    }

    @Override
    public SpanTracker getSpanTracker(StatsKey... keys) {
        if (keys == null) {
            throw new NullPointerException("keys");
        }

        if (keys.length == 0) {
            throw new IllegalArgumentException("must supply at least one key");
        }

        if (keys.length == 1) {
            return getSpanTracker(keys[0]);
        }

        final SpanTracker[] trackers = new SpanTracker[keys.length];

        for (int i = 0; i < keys.length; i++) {
            trackers[i] = getSpanTracker(keys[i]);
        }

        return new CompositeSpanTracker(trackers);
    }

    @Override
    public IncidentTracker getIncidentTracker(final StatsKey key) {
        Tracker tracker = getTracker(key);

        try {
            return (IncidentTracker)tracker;

        } catch (ClassCastException cce) {
            logUnsuitableTrackerImpl(cce,
                                     "incident",
                                     key,
                                     tracker.getClass(),
                                     IncidentTracker.class);
        }

        return IncidentTracker.FACTORY.createTracker(key, sessionManager);
    }

    @Override
    public IncidentTracker getIncidentTracker(final StatsKey... keys) {

        if (keys == null) {
            throw new NullPointerException("keys");
        }

        if (keys.length == 0) {
            throw new IllegalArgumentException("must supply at least one key");
        }

        if (keys.length == 1) {
            return getIncidentTracker(keys[0]);
        }

        final IncidentTracker[] trackers = new IncidentTracker[keys.length];

        for (int i = 0; i < keys.length; i++) {
            trackers[i] = getIncidentTracker(keys[i]);
        }

        return new CompositeIncidentTracker(trackers);
    }

    @Override
    public ManualTracker getManualTracker(final StatsKey key) {
        Tracker tracker = getTracker(key);

        try {
            return (ManualTracker)tracker;

        } catch (ClassCastException cce) {
            logUnsuitableTrackerImpl(cce,
                                     "manual",
                                     key,
                                     tracker.getClass(),
                                     ManualTracker.class);
        }

        return ManualTracker.FACTORY.createTracker(key, sessionManager);
    }

    private void logUnsuitableTrackerImpl(final ClassCastException cce,
                                          final String trackerTypeName,
                                          final StatsKey key,
                                          final Class<? extends Tracker> configuredTrackerClass,
                                          final Class<? extends Tracker> fallbackTrackerClass) {
        logger.warn("The configured tracker is not suitable for {} tracking. " +
                        "Please check your configuration. " +
                        "Falling back on: {}. " +
                        "Key: {}. " +
                        "Configured tracker: {}.",
                    new Object[] {
                        trackerTypeName,
                        fallbackTrackerClass.getName(),
                        key,
                        configuredTrackerClass.getName()
                    });

        logger.debug("Unsuitable tracker implementation", cce);
    }
}

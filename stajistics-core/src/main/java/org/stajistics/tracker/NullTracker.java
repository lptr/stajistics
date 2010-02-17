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
package org.stajistics.tracker;

import org.stajistics.NullStatsKey;
import org.stajistics.StatsKey;
import org.stajistics.session.ImmutableStatsSession;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;


/**
 * A singleton {@link StatsTracker} implementation conforming to the null object pattern.
 *
 * @author The Stajistics Project
 */
public final class NullTracker
    implements StatsTracker,SpanTracker,IncidentTracker,ManualTracker {

    public static final StatsTrackerFactory<StatsTracker> FACTORY = new Factory();

    private static final NullTracker INSTANCE = new NullTracker();

    private static final StatsSession session = 
        new ImmutableStatsSession(NullStatsKey.getInstance());

    private NullTracker() {}

    public static NullTracker getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTracking() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpanTracker start() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpanTracker stop() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getStartTime() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IncidentTracker incident() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsTracker reset() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKey getKey() {
        return NullStatsKey.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSession getSession() {
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManualTracker setValue(double value) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManualTracker addValue(double value) {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManualTracker commit() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /* NESTED CLASSES */

    public static final class Factory implements StatsTrackerFactory<StatsTracker> {

        @Override
        public StatsTracker createTracker(final StatsKey key, 
                                          final StatsSessionManager sessionManager) {
            return NullTracker.getInstance();
        }
        
        @Override
        public Class<StatsTracker> getTrackerType() {
            return StatsTracker.class;
        }
    }
}

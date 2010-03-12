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

import org.stajistics.NullStatsKey;
import org.stajistics.StatsKey;
import org.stajistics.session.ImmutableSession;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;


/**
 * A singleton {@link Tracker} implementation conforming to the null object pattern.
 *
 * @author The Stajistics Project
 */
public final class NullTracker
    implements Tracker,SpanTracker,IncidentTracker,ManualTracker {

    public static final TrackerFactory<Tracker> FACTORY = new Factory();

    private static final NullTracker INSTANCE = new NullTracker();

    private static final StatsSession session =
        new ImmutableSession(NullStatsKey.getInstance());

    private NullTracker() {}

    public static NullTracker getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isTracking() {
        return false;
    }

    @Override
    public SpanTracker track() {
        return this;
    }

    @Override
    public long getStartTime() {
        return 0;
    }

    @Override
    public IncidentTracker incident() {
        return this;
    }

    @Override
    public double getValue() {
        return 0;
    }

    @Override
    public Tracker reset() {
        return this;
    }

    @Override
    public StatsKey getKey() {
        return NullStatsKey.getInstance();
    }

    @Override
    public StatsSession getSession() {
        return session;
    }

    @Override
    public ManualTracker setValue(double value) {
        return this;
    }

    @Override
    public ManualTracker addValue(double value) {
        return this;
    }

    @Override
    public void commit() {}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /* NESTED CLASSES */

    public static final class Factory implements TrackerFactory<Tracker> {

        @Override
        public Tracker createTracker(final StatsKey key,
                                     final StatsSessionManager sessionManager) {
            return NullTracker.getInstance();
        }

        @Override
        public Class<Tracker> getTrackerType() {
            return Tracker.class;
        }
    }
}

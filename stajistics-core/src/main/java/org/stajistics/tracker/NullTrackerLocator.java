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

import org.stajistics.StatsKey;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * A singleton {@link TrackerLocator} implementation that return a {@link NullTracker}
 * instance from all methods.
 *
 * @author The Stajistics Project
 */
public class NullTrackerLocator implements TrackerLocator {

    private static NullTrackerLocator INSTANCE = new NullTrackerLocator();

    private NullTrackerLocator() {}

    public static NullTrackerLocator getInstance() {
        return INSTANCE;
    }

    @Override
    public Tracker getTracker(StatsKey key) {
        return NullTracker.getInstance();
    }

    @Override
    public SpanTracker getSpanTracker(StatsKey key) {
        return NullTracker.getInstance();
    }

    @Override
    public SpanTracker getSpanTracker(StatsKey... keys) {
        return NullTracker.getInstance();
    }

    @Override
    public IncidentTracker getIncidentTracker(StatsKey key) {
        return NullTracker.getInstance();
    }

    @Override
    public IncidentTracker getIncidentTracker(StatsKey... keys) {
        return NullTracker.getInstance();
    }

    @Override
    public ManualTracker getManualTracker(StatsKey key) {
        return NullTracker.getInstance();
    }

}

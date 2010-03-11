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

import java.io.Serializable;

import org.stajistics.StatsKey;
import org.stajistics.tracker.incident.CompositeIncidentTracker;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.CompositeSpanTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * Provides access to various types of Tracker instances that are associated with
 * given {@link StatsKey}s.
 *
 * @author The Stajistics Project
 */
public interface TrackerLocator extends Serializable {

    /**
     * Obtain a {@link Tracker} for the given <tt>key</tt> that can be
     * used to collect statistics.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link Tracker}. Never <tt>null</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     */
    Tracker getTracker(StatsKey key);

    /**
     * Obtain a {@link SpanTracker} for the given <tt>key</tt> that can be
     * used to collect statistics. Regardless of the given <tt>key</tt>'s configured
     * {@link TrackerFactory}, a SpanTracker instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link SpanTracker}. Never <tt>null</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     */
    SpanTracker getSpanTracker(StatsKey key);

    /**
     * Obtain a {@link SpanTracker} for the given set of <tt>keys</tt> that can be used to
     * collect statistics. A {@link SpanTracker} is obtained for each key in <tt>keys</tt>
     * and all are combined in a {@link CompositeSpanTracker} instance.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link Tracker}, never <tt>null</tt>.
     *
     * @see CompositeSpanTracker
     */
    SpanTracker getSpanTracker(StatsKey... keys);

    /**
     * Obtain an {@link IncidentTracker} for the given <tt>key</tt> that can be used to
     * report an incident of an event. Regardless of the given <tt>key</tt>'s configured
     * {@link TrackerFactory}, a IncidentTracker instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link IncidentTracker}, never <tt>null</tt>.
     */
    IncidentTracker getIncidentTracker(StatsKey key);

    /**
     * Obtain an {@link IncidentTracker} for the given set of <tt>keys</tt> that can be used to
     * report an incident of an event. An {@link IncidentTracker} is obtained for each key in
     * <tt>keys</tt> and all are combined in an {@link CompositeIncidentTracker} instance.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return An {@link IncidentTracker}, never <tt>null</tt>.
     *
     * @see CompositeIncidentTracker
     */
    IncidentTracker getIncidentTracker(StatsKey... keys);

    /**
     * Obtain a {@link ManualTracker} for the given <tt>key</tt> that can be used to
     * report manually collected data. Regardless of the given <tt>key</tt>'s configured
     * {@link TrackerFactory}, a ManualTracker instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link ManualTracker}, never <tt>null</tt>.
     */
    ManualTracker getManualTracker(StatsKey key);

}

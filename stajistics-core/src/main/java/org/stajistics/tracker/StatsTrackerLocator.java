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
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface StatsTrackerLocator extends Serializable {

    /**
     * Determine if statistics collection is enabled.
     *
     * @return <tt>true</tt> if statistics collection is enabled, <tt>false</tt> otherwise.
     */
    boolean isEnabled();

    /**
     * Enabled or disable statistics collection.
     *
     * @param enabled <tt>true</tt> to enable statistics collection, <tt>false</tt> to disable.
     */
    void setEnabled(boolean enabled);

    /**
     * Obtain a {@link StatsTracker} for the given <tt>key</tt> that can be
     * used to collect statistics. If statistics collection is disabled, 
     * a safe no-op {@link NullTracker} instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a tracker.
     * @return A {@link StatsTracker}. Never <tt>null</tt>.
     * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>.
     */
    StatsTracker getTracker(StatsKey key);

    /**
     * Obtain a {@link StatsTracker} for the given set of <tt>keys</tt> that can be used to
     * collect statistics. A {@link StatsTracker} is obtained for each key in <tt>keys</tt>
     * and all are wrapped in a {@link AbstractCompositeStatsTracker} instance.
     *
     * @param keys The {@link StatsKey}s for which to return a tracker.
     * @return A {@link StatsTracker}, never <tt>null</tt>.
     * @see AbstractCompositeStatsTracker
     */
    //StatsTracker getTracker(StatsKey... keys);


    SpanTracker getSpanTracker(StatsKey key);

    SpanTracker getSpanTracker(StatsKey... keys);

    /**
     * Obtain a {@link IncidentTracker} for a given {@link StatsKey} that can be used to
     * report an incident of an event. Regardless of the given <tt>key</tt>s configured
     * {@link StatsTrackerFactory}, a IncidentTracker instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link IncidentTracker}, never <tt>null</tt>.
     */
    IncidentTracker getIncidentTracker(StatsKey key);


    IncidentTracker getIncidentTracker(StatsKey... keys);

    /**
     * Obtain a {@link ManualTracker} for a given {@link StatsKey} that can be used to
     * report manually collected data. Regardless of the given <tt>key</tt>s configured
     * {@link StatsTrackerFactory}, a ManualTracker instance is returned.
     *
     * @param key The {@link StatsKey} for which to return a manual tracker.
     * @return A {@link ManualTracker}, never <tt>null</tt>.
     */
    ManualTracker getManualTracker(StatsKey key);

    ManualTracker getManualTracker(StatsKey... keys);

}

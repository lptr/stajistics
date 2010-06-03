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
import org.stajistics.session.StatsSessionManager;

import java.io.Serializable;

/**
 * A factory for creating {@link Tracker} instances.
 *
 * @author The Stajistics Project
 */
public interface TrackerFactory<T extends Tracker> extends Serializable {

    /**
     * Create a new {@link Tracker} instance associated with the given <tt>key</tt>.
     *
     * @param key The key for which to create a Tracker.
     * @param sessionManager The session manager from which a StatsSession can be
     *                       retrieved to pass into the new Tracker instance.
     * @return A Tracker instance, never <tt>null</tt>.
     */
    T createTracker(StatsKey key, StatsSessionManager sessionManager);

    /**
     * Obtain the base Tracker sub-type that this factory creates. This will be one of
     * SpanTracker, IncidentTracker, or ManualTracker.
     *
     * @return The Tracker type class, never <tt>null</tt>.
     */
    Class<T> getTrackerType();
}

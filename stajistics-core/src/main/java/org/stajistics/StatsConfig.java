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
package org.stajistics;

import java.io.Serializable;

import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.session.recorder.DataRecorderFactory;
import org.stajistics.tracker.Tracker;
import org.stajistics.tracker.TrackerFactory;

/**
 * Represents the configuration that can be applied to a single target for which statistics 
 * can be collected. Implementations of this interface are immutable.
 *
 * @author The Stajistics Project
 */
public interface StatsConfig extends Serializable {

    /**
     * Determine if statistics tracking for the associated target is enabled.
     *
     * @return <tt>true</tt> if statistics tracking is enabled, <tt>false</tt> otherwise.
     */
    boolean isEnabled();

    /**
     * Obtain the factory that creates {@link Tracker}s for the associated target.
     *
     * @return A {@link TrackerFactory} instance, never <tt>null</tt>.
     */
    TrackerFactory<?> getTrackerFactory();

    /**
     * Obtain the factory that creates {@link StatsSession}s for the associated target.
     *
     * @return A {@link StatsSessionFactory} instance, never <tt>null</tt>.
     */
    StatsSessionFactory getSessionFactory();

    /**
     * Obtain the factory that creates {@link org.stajistics.session.recorder.DataRecorder}s for a session.
     *
     * @return A {@link DataRecorderFactory} instance, never <tt>null</tt>.
     */
    DataRecorderFactory getDataRecorderFactory();

    /**
     * Get the unit applicable to the data that is collected for the associated target.
     *
     * @return The data unit, never <tt>null</tt>.
     */
    String getUnit();

    /**
     * Get the description of the data being collected for the associated target.
     *
     * @return The description, or <tt>null</tt> if unspecified.
     */
    String getDescription();

}

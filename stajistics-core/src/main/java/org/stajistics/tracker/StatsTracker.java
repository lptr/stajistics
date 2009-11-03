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

import java.io.Serializable;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.session.StatsSession;


/**
 * <p>A tracker is the main interface that is manipulated directly by client code for the
 * purposes of collecting statistics in a client-specific manner.</p> 
 *
 * <p>StatsTracker store 
 *
 * <p>StatsTrackers are intentionally designed to be manipulated by only one thread at a time,
 * and as such, are thread-unsafe. A StatsTracker instance should not be stored unless it 
 * is known that it will be accessed in a thread-safe manner. Rather, it is recommended that
 * an instance be retrieved when needed using {@link StatsManager#getTracker(StatsKey)}, 
 * or one of the convenience methods defined in {@link Stats}.
 *
 * The statistical
 * data that is collected by a tracker is published to an associated {@link StatsSession}.
 *
 * @author The Stajistics Project
 */
public interface StatsTracker extends Serializable {

    /**
     * Obtain the numeric value that was collected as a result of operating this tracker.
     *
     * @return
     */
    double getValue();

    /**
     * Clear the state of the tracker. This does not revert any changes that may have occurred
     * as a result of publishing data to the associated {@link StatsSession}.
     *
     * @return <tt>this</tt>.
     */
    StatsTracker reset();

    /**
     * Get the key that represents the target for which this tracker is collecting statistics.
     * This is equivalent to calling <tt>getSession().getKey()</tt>.
     *
     * @return A {@link StatsKey} instance, never <tt>null</tt>.
     */
    StatsKey getKey();

    /**
     * Obtain the {@link StatsSession} to which this tracker publishes statistics data.
     *
     * @return A {@link StatsSession} instance, never <tt>null</tt>.
     */
    StatsSession getSession();

}

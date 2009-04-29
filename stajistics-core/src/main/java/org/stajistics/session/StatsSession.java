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
package org.stajistics.session;

import java.io.Serializable;
import java.util.List;

import org.stajistics.StatsKey;
import org.stajistics.session.data.DataSet;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 * Stores statistics data collected for a single {@link StatsKey}.
 *
 * @author The Stajistics Project
 */
public interface StatsSession extends Serializable {

    /**
     * Obtain the {@link StatsKey} associated with this session.
     *
     * @return A {@link StatsKey} instance, never <tt>null</tt>.
     */
    StatsKey getKey();

    /**
     * Get a List of {@link DataRecorder}s that are associated with this session.
     *
     * @return A List of {@link DataRecorder}s, or an empty List if none exist.
     */
    List<DataRecorder> getDataRecorders();

    /**
     * Get the number of hits, or the number of times a {@link StatsTracker} associated with 
     * this session's {@link StatsKey} has called {@link #track(StatsTracker, long)}.
     *
     * @return The positive number of hits to this session.
     */
    long getHits();

    /**
     * Get the time stamp of the first hit to this session.
     *
     * @return The time stamp in milliseconds of the first hit, or <tt>-1</tt> if not yet hit.
     */
    long getFirstHitStamp();

    /**
     * Get the time stamp of the most recent hit to this session.
     *
     * @return The time stamp in milliseconds of the last hit, or <tt>-1</tt> if not yet hit.
     */
    long getLastHitStamp();

    /**
     * Get the number of commits, or the number of time a {@link StatsTracker} associated with
     * this session's {@link StatsKey} has called {@link #update(StatsTracker, long)}.
     *
     * @return The positive number of commits to ths session.
     */
    long getCommits();

    /**
     * Get the first value recorded for this session.
     *
     * @return The first value.
     *
     * @see StatsTracker#getValue()
     */
    double getFirst();

    /**
     * Get the most recent value recorded for this session.
     *
     * @return The most recent value.
     *
     * @see StatsTracker#getValue()
     */
    double getLast();

    /**
     * Get the smallest value recorded for this session.
     *
     * @return The minimum value seen for this session.
     *
     * @see StatsTracker#getValue()
     */
    double getMin();

    /**
     * Get the largest value recorded for this session.
     *
     * @return The maximum value seen for this session.
     *
     * @see StatsTracker#getValue()
     */
    double getMax();

    /**
     * Get the total of all values recorded for this session.
     *
     * @return The sum of all values seen for this session.
     *
     * @see StatsTracker#getValue()
     */
    double getSum();

    /**
     * Obtain a {@link DataSet} that is populated with all data collected for this session.
     * The {@link DataSet} is populated with default data stored by this session, such as hits and
     * commits, as well as data stored by the {@link DataRecorder}s associated with this session.
     * Depending on the implementation, this call may block calls to 
     * {@link #track(StatsTracker, long)} or {@link #update(StatsTracker, long)}.
     *
     * @return A {@link DataSet} full of data, never <tt>null</tt>.
     *
     * @see DataRecorder#collectData(StatsSession, org.stajistics.session.data.MutableDataSet)
     */
    DataSet collectData();

    /**
     * Do not call directly. Normally called by a {@link StatsTracker} implementation.
     * Increments the hits for this session by one.
     *
     * @param tracker The {@link StatsTracker} that, after this call, will be tracking 
     *        data for this session.
     * @param now The time stamp of the current time if known, otherwise <tt>-1</tt>.
     *
     * @see StatsTracker#track()
     */
    void track(StatsTracker tracker, long now);

    /**
     * Do not call directly. Normally called by a {@link StatsTracker} implementation.
     * Increments the commits for this session by one. The value reported by the given
     * <tt>tracker</tt>'s {@link StatsTracker#getValue()} method is processed and
     * offered to the {@link DataRecorder}s associated with this session. 
     *
     * @param tracker The {@link StatsTracker} that collected the data for this update.
     * @param now The time stamp of the current time if known, otherwise <tt>-1</tt>.
     *
     * @see StatsTracker#commit()
     * @see StatsTracker#getValue()
     * @see DataRecorder
     * @see #getDataRecorders()
     */
    void update(StatsTracker tracker, long now);

    /**
     * Clear all data recorded for this session. Resets all fields to initial values and
     * calls {@link DataRecorder#clear()} on all {@link DataRecorder} associated with this session.
     */
    void clear();

}

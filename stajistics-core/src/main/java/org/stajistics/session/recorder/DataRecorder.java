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
package org.stajistics.session.recorder;

import java.io.Serializable;
import java.util.Set;

import org.stajistics.data.DataSet;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface DataRecorder extends Serializable {

    /**
     * Obtain a Set of field names on which this DataRecorder operates.
     *
     * @return A Set of supported field names, never empty, and never <tt>null</tt>.
     */
    Set<String> getSupportedFieldNames();

    /**
     * 
     *
     * @param session
     * @param name
     * @return
     */
    Object getField(StatsSession session, String name);

    void update(StatsSession session, StatsTracker tracker, long now);

    void restore(DataSet dataSet);

    void collectData(StatsSession session, DataSet dataSet);

    /**
     * Clear all stored data. After this call the externally visible state will 
     * equal that of a newly created instance.
     */
    void clear();

}

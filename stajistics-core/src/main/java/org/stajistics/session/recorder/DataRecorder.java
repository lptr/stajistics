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
package org.stajistics.session.recorder;

import java.io.Serializable;
import java.util.List;

import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.Tracker;

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
    List<? extends Field> getSupportedFields();

    /**
     * Get the value of a single field.
     *
     * @param session The session that owns this DataRecorder instance.
     * @param name The name of the field.
     * @return The field value, or <tt>null</tt> if not found.
     */
    Object getObject(StatsSession session, Field field);
    long getLong(StatsSession session, Field field);
    double getDouble(StatsSession session, Field field);

    /**
     * Examine the tracker collected value, perform calculations, and store
     * the new data.
     *
     * @param session The session that owns this DataRecorder instance.
     * @param tracker The tracker that is triggering the update.
     * @param now The current time.
     */
    void update(StatsSession session, Tracker tracker, long now);

    /**
     * Populate internal data structures with the data provided in the given <tt>dataSet</tt>.
     *
     * @param dataSet The DataSet from which to extract data.
     */
    void restore(DataSet dataSet);

    /**
     * Prepare recorded data and add data fields to the given <tt>dataSet</tt>.
     * The data fields added defined by the Set returned by {@link #getSupportedFieldNames()}.
     *
     * @param session The session that owns this DataRecorder instance.
     * @param dataSet The DataSet to populate with data fields.
     */
    void collectData(StatsSession session, DataSetBuilder dataSet);

    /**
     * Clear all stored data. After this call the externally visible state will
     * equal that of a newly created instance.
     */
    void clear();

}

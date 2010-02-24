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
package org.stajistics.data;

import java.util.Set;

/**
 * 
 * @author The Stajistics Project
 */
public interface DataContainer {

    /**
     * Obtain the number of fields in this DataContainer.
     *
     * @return The current number of fields.
     */
    int size();

    /**
     * Determine if this DataContainer is empty.
     *
     * @return <tt>true</tt> if there are zero elements, <tt>false</tt> otherwise.
     */
    boolean isEmpty();

    Object getField(String name);

    <T> T getField(String name, Class<T> type) throws ClassCastException;

    /**
     * 
     * @param <T>
     * @param name The name of the field to retrieve.
     * @param defaultValue The value to return if the field doesn't exist or is not of type <tt>T</tt>.
     * @return The field value, or <tt>defaultValue</tt> if not found or if the value is not of type <tt>T</tt>.
     */
    <T> T getField(String name, T defaultValue);

    /**
     * Obtains a Set of field names that are contained in this DataContainer.
     * @return The Set of field names, never <tt>null</tt>.
     */
    Set<String> getFieldNames();

    void setField(String name, Object value);

    Object removeField(String name);

    void clear();

    /* NESTED INTERFACES */

    interface Entry {

        String getName();

        Object getValue();

        <T> T getValue(Class<T> type);

    }
}

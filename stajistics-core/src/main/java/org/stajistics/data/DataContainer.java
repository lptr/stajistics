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

    int size();

    boolean isEmpty();

    Object getField(String name);

    <T> T getField(String name, Class<T> type);

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

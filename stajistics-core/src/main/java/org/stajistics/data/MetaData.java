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
public interface MetaData {

    /**
     * Common attribute names 
     */
    interface Attribute {
        public static final String TYPE = "type";
        public static final String GENERATED = "generated";
    }

    boolean isEmpty();

    int size();

    Set<String> getAttributeNames();

    Object getAttribute(String name);

    <T> T getAttribute(String name, Class<T> type);

    void setAttribute(String name, Object value);

    Object removeAttribute(String name);

    void clear();
}

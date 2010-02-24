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
package org.stajistics.data;


/**
 *
 * @author The Stajistics Project
 */
public abstract class AbstractDataContainer implements DataContainer {

    @Override
    public <T> T getField(final String name, final Class<T> type) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (type == null) {
            throw new NullPointerException("type");
        }

        Object value = getField(name);
        if (value == null) {
            return null;
        }

        return type.cast(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getField(final String name, 
                          final T defaultValue) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (defaultValue == null) {
            throw new NullPointerException("defaultValue");
        }

        T result = defaultValue;
        Object value = getField(name);
        if (value != null) {
            try {
                result = (T) defaultValue.getClass()
                                         .cast(value);
            } catch (ClassCastException cce) {}
        }

        return result;
    }

}

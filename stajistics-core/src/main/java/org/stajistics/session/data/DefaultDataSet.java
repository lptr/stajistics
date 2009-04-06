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
package org.stajistics.session.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultDataSet implements MutableDataSet {

    private static final long serialVersionUID = 3617870089402050877L;

    private Map<String,Object> data = createDataMap();

    public DefaultDataSet() {}

    protected Map<String,Object> createDataMap() {
        return new LinkedHashMap<String,Object>();
    }

    @Override
    public Object getField(final String name) {
        return data.get(name);
    }

    @Override
    public Set<String> getFieldNames() {
        return data.keySet();
    }

    @Override
    public DataSet setField(final String name, final Object value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }

        data.put(name, value);

        return this;
    }

    @Override
    public Object removeField(final String name) {
        return data.remove(name);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }
}

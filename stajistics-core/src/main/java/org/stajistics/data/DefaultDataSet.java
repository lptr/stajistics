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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultDataSet implements DataSet {

    private static final long serialVersionUID = 3617870089402050877L;

    private Map<String,Object> dataMap = createDataMap();
    private Map<String,Object> metaDataMap = null;

    private MetaData metaData = null;
    private MetaDataSet metaDataSet = null;

    public DefaultDataSet() {}

    protected Map<String,Object> createDataMap() {
        return new HashMap<String,Object>();
    }

    private void ensureMetaDataInitialized() {
        if (metaDataMap == null) {
            metaDataMap = createDataMap();
            metaData = new DefaultMetaData(metaDataMap, "");
            metaDataSet = new DefaultMetaDataSet(metaDataMap);
        }
    }

    @Override
    public MetaData getMetaData() {
        ensureMetaDataInitialized();
        return metaData;
    }

    @Override
    public MetaDataSet getFieldMetaDataSet() {
        ensureMetaDataInitialized();
        return metaDataSet;
    }

    @Override
    public Object getField(final String name) {
        return dataMap.get(name);
    }

    @Override
    public <T> T getField(final String name, final Class<T> type) {
        return type.cast(getField(name));
    }

    @Override
    public Set<String> getFieldNames() {
        return dataMap.keySet();
    }

    @Override
    public void setField(final String name, final Object value) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (value == null) {
            throw new NullPointerException("value");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
        }

        dataMap.put(name, value);
    }

    @Override
    public Object removeField(final String name) {
        return dataMap.remove(name);
    }

    @Override
    public void clear() {
        dataMap.clear();
    }

    @Override
    public int size() {
        return dataMap.size();
    }

    @Override
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = dataMap.hashCode();
        if (metaDataMap != null) {
            hash ^= metaDataMap.hashCode();
            hash ^= metaData.hashCode();
            hash ^= metaDataSet.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof DataSet) && equals((DataSet)obj);
    }

    public boolean equals(final DataSet other) {

        if (this.size() != other.size()) {
            return false;
        }

        for (String fieldName : this.getFieldNames()) {
            Object thisValue = this.getField(fieldName);
            Object otherValue = other.getField(fieldName);

            if (otherValue == null || !thisValue.equals(otherValue)) {
                return false;
            }
        }

        if (this.getMetaData().equals(other.getMetaData())) {
            return false;
        }
        if (this.getFieldMetaDataSet().equals(other.getFieldMetaDataSet())) {
            return false;
        }

        return true;
    }
}

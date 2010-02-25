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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author The Stajistics Project
 */
class DefaultMetaData extends AbstractDataContainer implements MetaData {

    private static final String DELIMITER = "__" + DefaultMetaData.class.getSimpleName() + "__";

    private final Map<String,Object> dataMap;
    private final String fieldName;

    private int size = 0;

    private Set<String> attributeNames = null;

    DefaultMetaData(final Map<String,Object> dataMap, final String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName");
        }

        this.dataMap = dataMap;
        this.fieldName = fieldName;
    }

    @Override
    public Object getField(final String name) {
        if (name == null) {
            return null;
        }

        return dataMap.get(keyFor(name));
    }

    @Override
    public Set<String> getFieldNames() {
        if (attributeNames == null) {
            attributeNames = findAttributeNames();
        }

        return attributeNames;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        final String prefix = fieldName + DELIMITER;
        for (Iterator<String> it = dataMap.keySet().iterator(); it.hasNext(); ) {
            if (it.next().startsWith(prefix)) {
                it.remove();
            }
        }
        size = 0;
    }

    @Override
    public Object removeField(final String name) {
        Object value = dataMap.remove(keyFor(name));
        if (value != null) {
            size--;
        }
        return value;
    }

    @Override
    public void setField(final String name, final Object value) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (dataMap.put(keyFor(name), value) == null) {
            size++;
        }
    }

    private String keyFor(final String attrName) {
        StringBuilder buf = new StringBuilder(fieldName.length() + DELIMITER.length() + attrName.length());
        buf.append(fieldName);
        buf.append(DELIMITER);
        buf.append(attrName);
        return buf.toString();
    }

    private Set<String> findAttributeNames() {
        final String prefix = fieldName + DELIMITER;
        Set<String> attrNames = new HashSet<String>();
        for (Map.Entry<String,Object> entry : dataMap.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                String attrName = entry.getKey().substring(prefix.length());
                attrNames.add(attrName);
            }
        }

        return Collections.unmodifiableSet(attrNames);
    }
}

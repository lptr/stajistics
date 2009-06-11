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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author The Stajistics Project
 */
class DefaultMetaData implements MetaData {

    private static final String DELIMITER = "__" + MetaData.class.getName() + "__";

    private final Map<String,Object> dataMap;
    private final String keyPrefix;

    private int size = 0;

    private Set<String> attributeNames = null;

    DefaultMetaData(final Map<String,Object> dataMap, final String keyPrefix) {
        if (keyPrefix == null) {
            throw new NullPointerException("keyPrefix");
        }

        this.dataMap = dataMap;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public Object getAttribute(final String name) {
        return dataMap.get(keyFor(name));
    }

    @Override
    public <T> T getAttribute(final String name, final Class<T> type) {
        return type.cast(getAttribute(name));
    }

    @Override
    public Set<String> getAttributeNames() {
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
        final String prefix = keyPrefix + DELIMITER;
        for (Iterator<String> it = dataMap.keySet().iterator(); it.hasNext(); ) {
            if (it.next().startsWith(prefix)) {
                it.remove();
            }
        }
        size = 0;
    }

    @Override
    public Object removeAttribute(final String name) {
        Object value = dataMap.remove(keyFor(name));
        if (value != null) {
            size--;
        }
        return value;
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        if (dataMap.put(keyFor(name), value) == null) {
            size++;
        }
    }

    private String keyFor(final String attrName) {
        StringBuilder buf = new StringBuilder(keyPrefix.length() + DELIMITER.length() + attrName.length());
        buf.append(keyPrefix);
        buf.append(DELIMITER);
        buf.append(attrName);
        return buf.toString();
    }

    private Set<String> findAttributeNames() {
        final String prefix = keyPrefix + DELIMITER;
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

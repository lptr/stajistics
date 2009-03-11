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
package org.stajistics;

import java.util.Collections;
import java.util.Map;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class SingleAttributeStatsKey implements StatsKey {

    private static final long serialVersionUID = -4220144422224946459L;

    private final String name;
    private final String attrName;
    private final Object attrValue;

    private final int hashCode;

    protected SingleAttributeStatsKey(final String name,
                                      final String attrName,
                                      final Object attrValue) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (!Util.isValidKeyString(name)) {
            throw new IllegalArgumentException("invalid name: " + name);
        }

        if (attrName != null && attrValue == null) {
            throw new NullPointerException("attrValue");
        }

        if (!Util.isValidKeyString(attrName)) {
            throw new IllegalArgumentException("invalid attrName: " + attrName);
        }

        if (attrValue.getClass() == String.class) {
            if (!Util.isValidKeyString((String)attrValue)) {
                throw new IllegalArgumentException("invalid attrValue: " + attrValue);
            }
        }

        this.name = name;
        this.attrName = attrName;
        this.attrValue = attrValue;

        hashCode = hash();
    }

    protected int hash() {
        int h = name.hashCode();

        if (attrName != null) {
            h ^= attrName.hashCode();

            if (attrValue != null) {
                h ^= attrValue.hashCode();
            }
        }

        return h;
    }

    @Override
    public StatsKeyBuilder buildCopy() {
        return Stats.getInstance().createConfigBuilder(this);
    }

    @Override
    public Object getAttribute(final String name) {
        if (name.equals(attrName)) {
            return attrValue;
        }

        return null;
    }

    @Override
    public Map<String,Object> getAttributes() {
        return Collections.singletonMap(attrName, attrValue);
    }

    @Override
    public int getAttributeCount() {
        return attrName == null ? 0 : 1;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof StatsKey) && equals((StatsKey)obj);
    }

    public boolean equals(final StatsKey other) {
        if (other == null) {
            return false;
        }

        if (hashCode != other.hashCode()) {
            return false;
        }

        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {

        String attrValueStr = null;

        int size = 20 + name.length();

        if (attrName != null) {
            attrValueStr = attrValue.toString();

            size += attrName.length() + attrValueStr.length();
        }

        StringBuilder buf = new StringBuilder(size);

        buf.append(StatsKey.class.getSimpleName());
        buf.append("[name=");
        buf.append(name);

        if (attrName != null) {
            buf.append(',');
            buf.append(attrName);
            buf.append('=');
            buf.append(attrValueStr);
        }

        buf.append(']');

        return buf.toString();
    }
}

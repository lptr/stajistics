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
public class DefaultStatsKey implements StatsKey {

    private static final long serialVersionUID = -9052397460294109721L;

    protected final String name;
    protected final Map<String,Object> attributes;

    protected final int hashCode;

    protected DefaultStatsKey(final String name,
                              final Map<String,Object> attributes) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (attributes == null) {
            throw new NullPointerException("attributes");
        }

        this.name = name;
        this.attributes = attributes;

        this.hashCode = hash();
    }

    protected int hash() {
        return name.hashCode() ^
               attributes.size() ^ // This matters
               attributes.hashCode();
    }

    @Override
    public StatsKeyBuilder buildCopy() {
        return Stats.getInstance().createConfigBuilder(this);
    }

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public Map<String,Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public int getAttributeCount() {
        return attributes.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof StatsKey) && equals((StatsKey)other);
    }

    public boolean equals(final StatsKey other) {

        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (this.hashCode != other.hashCode()) {
            return false;
        }

        if (!this.name.equals(other.getName())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(StatsKey.class.getSimpleName());
        buf.append("[name=");
        buf.append(name);

        if (!attributes.isEmpty()) {
           buf.append(",attrs=");
           buf.append(attributes);
        }

        buf.append(']');

        return buf.toString();
    }

}

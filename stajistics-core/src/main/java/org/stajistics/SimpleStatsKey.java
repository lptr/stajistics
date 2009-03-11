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
public class SimpleStatsKey implements StatsKey {

    private static final long serialVersionUID = -8631853852832561419L;

    private final String name;

    protected SimpleStatsKey(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (!Util.isValidKeyString(name)) {
            throw new IllegalArgumentException("invalid name: " + name);
        }

        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof StatsKey) && equals((StatsKey)obj);
    }

    public boolean equals(final StatsKey other) {

        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (this.name.hashCode() != other.hashCode()) {
            return false;
        }

        if (!this.name.equals(other.getName())) {
            return false;
        }

        return true;
    }

    @Override
    public StatsKeyBuilder buildCopy() {
        return Stats.getInstance().createConfigBuilder(this);
    }

    @Override
    public Object getAttribute(final String name) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public int getAttributeCount() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(name.length() + 16);

        buf.append(StatsKey.class.getSimpleName());
        buf.append("[name=");
        buf.append(name);
        buf.append(']');

        return buf.toString();
    }
}

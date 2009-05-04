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


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsKey implements StatsKey {

    private static final long serialVersionUID = 3175918652708917322L;

    private final StatsKeyFactory keyFactory;
    private final String name;

    private int hashCode;

    protected AbstractStatsKey(final String name,
                               final StatsKeyFactory keyFactory)
    {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (keyFactory == null) {
            throw new NullPointerException("keyFactory");
        }

        this.name = name;
        this.keyFactory = keyFactory;
    }

    @Override
    public final StatsKeyBuilder buildCopy() {
        return keyFactory.createKeyBuilder(this);
    }

    @Override
    public final String getName() {
        return name;
    }

    protected void setHashCode() {
        this.hashCode = name.hashCode() ^
                        getAttributeCount() ^
                        getAttributes().hashCode();
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }

    @Override
    public final boolean equals(final Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (this.hashCode != other.hashCode()) {
            return false;
        }

        StatsKey otherKey = (StatsKey)other;

        if (!this.name.equals(otherKey.getName())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        int attrCount = getAttributeCount();

        StringBuilder buf = new StringBuilder(name.length() + 20 + (attrCount * 8));

        buf.append(StatsKey.class.getSimpleName());
        buf.append("[name=");
        buf.append(name);

        if (attrCount > 0) {
            buf.append(",attrs=");
            appendAttributes(buf);
        }

        buf.append(']');

        return buf.toString();
    }

    protected void appendAttributes(final StringBuilder buf) {}
}

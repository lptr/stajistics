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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A convenience base class for {@link StatsKey} implementations.
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsKey implements StatsKey {

    private static final long serialVersionUID = 3175918652708917322L;

    private final StatsKeyFactory keyFactory;
    private final String name;

    private int hashCode;

    /**
     * Construct a new instance.
     *
     * @param name The key name. Must not be null.
     * @param keyFactory The factory that supports the creation of copies of this StatsKey instance.
     *
     * @throws NullPointerException If <tt>name</tt> is <tt>null</tt>.
     */
    public AbstractStatsKey(final String name,
                            final StatsKeyFactory keyFactory) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        this.name = name;
        this.keyFactory = keyFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final StatsKeyBuilder buildCopy() {
        if (keyFactory == null) {
            throw new UnsupportedOperationException(StatsKeyFactory.class.getSimpleName() + " unavailable");
        }

        return keyFactory.createKeyBuilder(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     * A convenience method to be called by subclass constructors
     * to calculate the hash code value in the default manner.
     */
    protected void setHashCode() {
        int h = 31 + name.hashCode();
        h = h * 31 + getAttributeCount();
        h = h * 31 + getAttributes().hashCode();

        this.hashCode = h;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        // Try to short-circuit the attribute equality checks
        if (this.hashCode != other.hashCode()) {
            return false;
        }

        StatsKey otherKey = (StatsKey)other;

        if (this.getAttributeCount() != otherKey.getAttributeCount()) {
            return false;
        }

        if (!this.name.equals(otherKey.getName())) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final StatsKey other) {
        // Compare key names
        int i = this.name.compareTo(other.getName());

        if (i == 0) {
            // Compare attribute counts
            i = other.getAttributeCount() - this.getAttributeCount();

            if (i == 0) {
                // Compare sorted attribute names
                Map<String,Object> thisAttrs = this.getAttributes();
                Map<String,Object> otherAttrs = other.getAttributes();

                List<String> thisAttrKeys = new ArrayList<String>(thisAttrs.keySet());
                List<String> otherAttrKeys = new ArrayList<String>(otherAttrs.keySet());

                Collections.sort(thisAttrKeys);
                Collections.sort(otherAttrKeys);

                Iterator<String> thisItr = thisAttrKeys.iterator();
                Iterator<String> otherItr = otherAttrKeys.iterator();

                while (thisItr.hasNext() && otherItr.hasNext()) {
                    i = thisItr.next()
                               .compareTo(otherItr.next());
                    if (i != 0) {
                        break;
                    }
                }

                if (i == 0) {
                    // Compare attribute values
                    for (Map.Entry<String,Object> entry : thisAttrs.entrySet()) {
                        Object otherValue = otherAttrs.get(entry.getKey());
                        i = ((Comparable)entry.getValue()).compareTo(otherValue);
                        if (i != 0) {
                            break;
                        }
                    }
                }
            }
        }

        return i;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * A hook for subclasses to insert attribute values into the result
     * of {@link #toString()} calls.
     *
     * @param buf The StringBuilder into which attributes should be appended.
     */
    protected void appendAttributes(final StringBuilder buf) {}
}

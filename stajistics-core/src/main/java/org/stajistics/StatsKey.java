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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.TimeDurationTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsKey implements Comparable<StatsKey>,Serializable {

    private static final long serialVersionUID = 3659296806180159830L;

    private static final String DEFAULT_UNIT = "ms";

    protected final String name;
    protected String unit;
    protected Class<? extends StatsTracker> trackerClass;
    protected Map<String,Object> attributes;

    private int hashCode = 0;

    protected StatsKey(final String name,
                       final String unit,
                       final Class<? extends StatsTracker> trackerClass,
                       final Map<String,Object> attributes) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (unit == null) {
            throw new NullPointerException("unit");
        }

        this.name = name;
        this.unit = unit;

        if (trackerClass == null) {
            this.trackerClass = TimeDurationTracker.class;
        } else {
            this.trackerClass = trackerClass;
        }

        this.attributes = attributes;

        this.hashCode = name.hashCode() ^ unit.hashCode() ^ this.trackerClass.hashCode();
        if (attributes != null) {
            this.hashCode ^= attributes.hashCode();
        }
    }

    public static StatsKey create(final String name) {
        return new StatsKey(name, DEFAULT_UNIT, null, null);
    }

    public static Builder build(final String name) {
        return new Builder(name);
    }

    public Builder copy() {
        return new Builder(this);
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public Class<? extends StatsTracker> getTrackerClass() {
        return trackerClass;
    }

    public Map<String,Object> getAttributes() {
        if (attributes == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(final StatsKey other) {
        int i = this.name.compareTo(other.name);
        if (i != 0) {
            return i;
        }

        i = this.unit.compareTo(other.unit);
        if (i != 0) {
            return i;
        }

        i = this.trackerClass.getName().compareTo(other.trackerClass.getName());
        if (i != 0) {
            return i;
        }

        if (this.attributes == null) {
            if (other.attributes == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (other.attributes == null) {
            return 1;
        }

        i = other.attributes.size() - this.attributes.size();

        // TODO: deep compare?

        return i;
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

        if (!this.name.equals(other.name)) {
            return false;
        }

        if (!this.unit.equals(other.unit)) {
            return false;
        }

        if (this.trackerClass != other.trackerClass) {
            return false;
        }

        if (this.attributes == null) {
            return other.attributes == null;
        }

        if (other.attributes == null) {
            return false;
        }

        if (!this.attributes.equals(other.attributes)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append(getClass().getSimpleName());
        buf.append("[name=");
        buf.append(name);
        buf.append(",unit=");
        buf.append(unit);
        buf.append(",trackerClass=");
        buf.append(trackerClass.getSimpleName());

        if (attributes != null) {
            buf.append(",attrs=");
            buf.append(attributes);
        }

        buf.append(']');

        return buf.toString();
    }

    /* INNER CLASSES */

    public static class Builder {

        protected String name;
        protected String unit;
        protected Class<? extends StatsTracker> trackerClass;
        protected Map<String,Object> attributes;

        protected Builder(final String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }

            this.name = name;
        }

        protected Builder(final StatsKey startFrom) {
            this.name = startFrom.name;
            this.unit = startFrom.unit;

            if (startFrom.attributes != null && !startFrom.attributes.isEmpty()) {
                this.attributes = new HashMap<String,Object>(startFrom.attributes);
            }
        }

        public Builder withUnit(final String unit) {
            this.unit = unit;

            return this;
        }

        public Builder withTracker(final Class<? extends StatsTracker> trackerClass) {
            this.trackerClass = trackerClass;

            return this;
        }

        public Builder withScope(final Class<?> scope) {
            return withScope(scope.getName());
        }

        public Builder withScope(final String scope) {
            this.name = scope + ": " + this.name;
            return this;
        }

        public Builder withAttribute(final String name, final Object value) {
            if (attributes == null) {
                attributes = new HashMap<String,Object>(4);
            }

            attributes.put(name, value);

            return this;
        }

        public StatsKey key() {

            if (unit == null) {
                unit = DEFAULT_UNIT;
            }

            Map<String,Object> attrs = null;

            if (attributes != null) {
                if (attributes.size() == 1) {
                    Map.Entry<String,Object> entry = attributes.entrySet()
                                                               .iterator()
                                                               .next();
                    attrs = Collections.singletonMap(entry.getKey(), 
                                                     entry.getValue());
    
                } else if (attributes.size() > 1) {
                    attrs = attributes;
                }
            }

            return new StatsKey(name, unit, trackerClass, attrs);
        }

    }
}

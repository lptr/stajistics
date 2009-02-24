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

import org.stajistics.session.DefaultStatsSession;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.TimeDurationTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsKey implements StatsKey {

    private static final long serialVersionUID = -9052397460294109721L;

    protected final String name;
    protected final String unit;
    protected final Map<String,Object> attributes;
    protected final Class<? extends StatsTracker> trackerClass;
    protected final Class<? extends StatsSession> sessionClass;

    protected final int hashCode;

    public DefaultStatsKey(final String name,
                              final String unit,
                              final Map<String,Object> attributes,
                              final Class<? extends StatsTracker> trackerClass,
                              final Class<? extends StatsSession> sessionClass) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (unit == null) {
            throw new NullPointerException("unit");
        }
        
        if (attributes == null) {
            throw new NullPointerException("attributes");
        }
        
        if (trackerClass == null) {
            throw new NullPointerException("trackerClass");
        }

        if (sessionClass == null) {
            throw new NullPointerException("sessionClass");
        }

        this.name = name;
        this.unit = unit;
        this.attributes = attributes;
        this.trackerClass = trackerClass;
        this.sessionClass = sessionClass;

        this.hashCode = hash();
    }

    protected int hash() {
        return name.hashCode() ^
               unit.hashCode() ^
               attributes.hashCode() ^
               trackerClass.hashCode() ^
               sessionClass.hashCode();
    }

    @Override
    public StatsKeyBuilder buildCopy() {
        // TODO
        return null;
    }

    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends StatsSession> getSessionClass() {
        return sessionClass;
    }

    @Override
    public Class<? extends StatsTracker> getTrackerClass() {
        return trackerClass;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {
        return hashCode;
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
        buf.append(",unit=");
        buf.append(unit);

        //TODO: more

        buf.append(']');

        return buf.toString();
    }

}

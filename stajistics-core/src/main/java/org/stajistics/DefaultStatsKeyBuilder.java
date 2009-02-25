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
import java.util.HashMap;
import java.util.Map;

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class DefaultStatsKeyBuilder implements StatsKeyBuilder {

    private static final int DEFAULT_ATTR_COUNT = 4;

    protected String name;
    protected String unit;
    protected Map<String,Object> attributes;
    protected Class<? extends StatsTracker> trackerClass;
    protected Class<? extends StatsSession> sessionClass;

    public DefaultStatsKeyBuilder() {}

    public DefaultStatsKeyBuilder(final StatsKey template) {
        this(template.getName(),
             template.getUnit(),
             copyAttributes(template.getAttributes()),
             template.getTrackerClass(),
             template.getSessionClass());
    }



    public DefaultStatsKeyBuilder(final String name, 
                                  final String unit,
                                  final Map<String, Object> attributes,
                                  final Class<? extends StatsTracker> trackerClass,
                                  final Class<? extends StatsSession> sessionClass) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        this.name = name;
        this.unit = unit;
        this.attributes = attributes;
        this.trackerClass = trackerClass;
        this.sessionClass = sessionClass;
    }

    protected static Map<String,Object> copyAttributes(final Map<String,Object> attrs) {
        if (attrs == null || attrs.isEmpty()) {
            return null;
        }

        return new HashMap<String,Object>(attrs);
    }

    protected void ensureAttributesInited() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>(DEFAULT_ATTR_COUNT);
        }
    }

    @Override
    public StatsKeyBuilder withAttribute(final String name, final Object value) {
        ensureAttributesInited();

        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }

        return this;
    }

    @Override
    public StatsKeyBuilder withAttributes(final Map<String, Object> attributes) {
        ensureAttributesInited();

        attributes.putAll(attributes);
        return this;
    }

    @Override
    public StatsKeyBuilder withName(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        this.name = name;
        return this;
    }

    @Override
    public StatsKeyBuilder withSession(final Class<? extends StatsSession> sessionClass) {
        if (sessionClass == null) {
            throw new NullPointerException("sessionClass");
        }

        this.sessionClass = sessionClass;
        return this;
    }

    @Override
    public StatsKeyBuilder withTracker(final Class<? extends StatsTracker> trackerClass) {
        if (trackerClass == null) {
            throw new NullPointerException("trackerClass");
        }

        this.trackerClass = trackerClass;
        return this;
    }

    @Override
    public StatsKeyBuilder withUnit(final String unit) {
        if (unit == null) {
            throw new NullPointerException("unit");
        }

        this.unit = unit;
        return this;
    }

    @Override
    public StatsKey newKey() {

        if (name == null) {
            throw new IllegalStateException("Must specify a name");
        }

        String unit = this.unit;
        if (unit == null) {
            unit = Constants.DEFAULT_UNIT;
        }

        Map<String,Object> attributes;

        if (this.attributes == null || this.attributes.isEmpty()) {
            attributes = Collections.emptyMap();

        } else {
            if (this.attributes.size() == 1) {
                Map.Entry<String,Object> entry = this.attributes.entrySet()
                                                                .iterator()
                                                                .next();
                attributes = Collections.singletonMap(entry.getKey(), 
                                                      entry.getValue());

            } else {
                attributes = this.attributes;
            }
        }

        Class<? extends StatsTracker> trackerClass = this.trackerClass;
        if (trackerClass == null) {
            trackerClass = Constants.DEFAULT_TRACKER_CLASS;
        }

        Class<? extends StatsSession> sessionClass = this.sessionClass;
        if (sessionClass == null) {
            sessionClass = Constants.DEFAULT_SESSION_CLASS;
        }

        return new DefaultStatsKey(name, unit, attributes, trackerClass, sessionClass);
    }

}

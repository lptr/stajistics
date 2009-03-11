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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class DefaultStatsKeyBuilder implements StatsKeyBuilder {

    private static final int DEFAULT_ATTR_COUNT = 4;

    protected String name;

    protected String firstAttrName;
    protected Object firstAttrValue;

    protected Map<String,Object> attributes;

    public DefaultStatsKeyBuilder(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (!Util.isValidKeyString(name)) {
            throw new IllegalArgumentException("invalid name: " + name);
        }

        this.name = name;
    }

    public DefaultStatsKeyBuilder(final StatsKey template) {
        if (template == null) {
            throw new NullPointerException("template");
        }

        this.name = template.getName();

        if (!Util.isValidKeyString(name)) {
            throw new IllegalArgumentException("invalid name: " + name);
        }

        Map<String,Object> attrs = template.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            if (attrs.size() == 1) {
                Map.Entry<String,Object> entry = attrs.entrySet()
                                                      .iterator()
                                                      .next();
                firstAttrName = entry.getKey();
                firstAttrValue = entry.getValue();

            } else {
                firstAttrName = "dummy"; //TODO: this is lame

                this.attributes = new HashMap<String,Object>(attrs);
            }
        }
    }

    protected DefaultStatsKeyBuilder(final String name, 
                                     final Map<String, Object> attributes) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        if (!Util.isValidKeyString(name)) {
            throw new IllegalArgumentException("invalid name: " + name);
        }

        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public StatsKeyBuilder withAttribute(final String name, final String value) {
        putAttribute(name, value);
        return this;
    }

    @Override
    public StatsKeyBuilder withAttribute(final String name, final Boolean value) {
        putAttribute(name, value);
        return this;
    }

    @Override
    public StatsKeyBuilder withAttribute(final String name, final Integer value) {
        putAttribute(name, value);
        return this;
    }

    @Override
    public StatsKeyBuilder withAttribute(final String name, final Long value) {
        putAttribute(name, value);
        return this;
    }

    protected void putAttribute(final String name, final Object value) {

        if (name == null) {
            throw new NullPointerException("name");
        }

        if (value == null) {
            throw new NullPointerException("value for name: " + name);
        }

        if (!Util.isValidKeyString(name)) {
            throw new IllegalArgumentException("invalid name: " + name);
        }

        if (value.getClass() == String.class) {
            if (!Util.isValidKeyString((String)value)) {
                throw new IllegalArgumentException("invalid value: " + value);
            }
        }

        if (firstAttrName == null) {
            firstAttrName = name;
            firstAttrValue = value;

        } else {
            if (attributes == null) {
                attributes = new HashMap<String, Object>(DEFAULT_ATTR_COUNT);
                attributes.put(firstAttrName, firstAttrValue);
            }

            attributes.put(name, value);
        }
    }

    @Override
    public StatsKey newKey() {

        if (name == null) {
            throw new IllegalStateException("Must specify a name");
        }

        // If no attributes
        if (firstAttrName == null) {
            return new SimpleStatsKey(name);
        }

        // If one attribute
        if (attributes == null) {
            return new SingleAttributeStatsKey(name,
                                               firstAttrName, 
                                               firstAttrValue);
        }

        // Many attributes
        return new DefaultStatsKey(name, attributes);
    }

}

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

import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class DefaultStatsKeyBuilder implements StatsKeyBuilder {

    protected String name;

    protected String firstAttrName;
    protected Object firstAttrValue;

    protected Map<String,Object> attributes;

    public DefaultStatsKeyBuilder(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        this.name = name;
    }

    public DefaultStatsKeyBuilder(final StatsKey template) {
        if (template == null) {
            throw new NullPointerException("template");
        }

        this.name = template.getName();

        Map<String,Object> attrs = template.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            attributes = new TreeMap<String,Object>(attrs);
        }
    }

    protected DefaultStatsKeyBuilder(final String name, 
                                     final Map<String, Object> attributes) {
        if (name == null) {
            throw new NullPointerException("name");
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

        if (!Util.isValidKeyAttributeName(name)) {
            throw new IllegalArgumentException("invalid attribute name: " + name);
        }

        if (value == null) {
            throw new NullPointerException("value for name: " + name);
        }

        if (attributes == null) {
            if (firstAttrName == null) {
                firstAttrName = name;
                firstAttrValue = value;
            } else {
                attributes = new TreeMap<String,Object>();
                attributes.put(firstAttrName, firstAttrValue);
            }
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public StatsKey newKey() {

        if (name == null) {
            throw new IllegalStateException("Must specify a name");
        }

        if (attributes == null) {
            // If no attributes
            if (firstAttrName == null) {
                return new SimpleStatsKey(name);
            }

            // One attribute
            return new SingleAttributeStatsKey(name,
                                               firstAttrName, 
                                               firstAttrValue);
        }

        // Many attributes
        return new DefaultStatsKey(name, attributes);
    }

}

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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The default implementation of {@link StatsKeyBuilder}.
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
            attributes = new LinkedHashMap<String,Object>(attrs);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder withNameSuffix(final String nameSuffix) {
        if (nameSuffix == null) {
            throw new NullPointerException("nameSuffix");
        }
        if (nameSuffix.length() == 0) {
            throw new IllegalArgumentException("nameSuffix is empty");
        }

        this.name += StatsConstants.KEY_HIERARCHY_DELIMITER + nameSuffix;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder withAttribute(final String name, final String value) {
        putAttribute(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder withAttribute(final String name, final Boolean value) {
        putAttribute(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder withAttribute(final String name, final Integer value) {
        putAttribute(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder withAttribute(final String name, final Long value) {
        putAttribute(name, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder withoutAttribute(final String name) {
        if (name == null) {
            return this;
        }

        if (attributes != null) {
            attributes.remove(name);

        } else if (firstAttrName != null && firstAttrName.equals(name)) {
            firstAttrName = null;
            firstAttrValue = null;
        }

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
                attributes = new LinkedHashMap<String,Object>(8);
                attributes.put(firstAttrName, firstAttrValue);
                firstAttrName = null;
                firstAttrValue = null;

                attributes.put(name, value);
            }
        } else {
            attributes.put(name, value);
        }
    }

    /**
     * {@inheritDoc}
     */
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

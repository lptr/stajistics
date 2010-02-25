/* Copyright 2009 - 2010 The Stajistics Project
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
 * The default implementation of {@link StatsKeyBuilder}. Do not
 * instantiate this class directly. Instead use the {@link StatsKeyFactory} provided by
 * {@link StatsManager#getKeyFactory()}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsKeyBuilder implements StatsKeyBuilder {

    protected StatsKeyFactory keyFactory;
    protected String name;

    protected String firstAttrName;
    protected Object firstAttrValue;

    protected Map<String,Object> attributes;

    /**
     * Create a new instance.
     *
     * @param name The initial key name. Must not be <tt>null</tt>.
     * @param keyFactory The factory that supports the creation of StatsKey instances. 
     *                   Must not be <tt>null</tt>.
     * @throws NullPointerException If <tt>name</tt> or <tt>keyFactory</tt> is <tt>null</tt>.
     */
    public DefaultStatsKeyBuilder(final String name,
                                  final StatsKeyFactory keyFactory) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (keyFactory == null) {
            throw new NullPointerException("keyFactory");
        }

        this.name = name;
        this.keyFactory = keyFactory;
    }

    /**
     * Create a new instance initialized with the fields provided by <tt>template</tt>.
     * A copy of the <tt>template</tt> attribute Map is used by this new instance.
     *
     * @param template The StatsKey from which the initial state of this builder will be 
     *                 extracted. Must not be null.
     * @param keyFactory The factory that supports the creation of StatsKey instances. Must not be null.
     */
    public DefaultStatsKeyBuilder(final StatsKey template,
                                  final StatsKeyFactory keyFactory) {
        if (template == null) {
            throw new NullPointerException("template");
        }
        if (keyFactory == null) {
            throw new NullPointerException("keyFactory");
        }

        this.name = template.getName();
        this.keyFactory = keyFactory;

        Map<String,Object> attrs = template.getAttributes();
        if (attrs != null && !attrs.isEmpty()) {
            attributes = new LinkedHashMap<String,Object>(attrs);
        }
    }

    @Override
    public StatsKeyBuilder withNameSuffix(final String nameSuffix) {
        if (nameSuffix == null) {
            throw new NullPointerException("nameSuffix");
        }

        if (nameSuffix.length() > 0) {
            this.name += StatsConstants.KEY_HIERARCHY_DELIMITER + nameSuffix;
        }

        return this;
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

    @Override
    public StatsKey newKey() {

        if (name == null) {
            throw new IllegalStateException("Must specify a name");
        }

        if (attributes == null) {
            // If no attributes
            if (firstAttrName == null) {
                return new SimpleStatsKey(name, keyFactory);
            }

            // One attribute
            return new SingleAttributeStatsKey(name,
                                               keyFactory, 
                                               firstAttrName, 
                                               firstAttrValue);
        }

        // Many attributes
        return new DefaultStatsKey(name, keyFactory, attributes);
    }

}

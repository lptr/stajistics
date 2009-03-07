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

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class DefaultStatsKeyBuilder implements StatsKeyBuilder {

    private static final int DEFAULT_ATTR_COUNT = 4;

    protected String name;
    protected Map<String,Object> attributes;

    public DefaultStatsKeyBuilder(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        this.name = name;
    }

    public DefaultStatsKeyBuilder(final StatsKey template) {
        this(template.getName(),
             copyAttributes(template));
    }

    public DefaultStatsKeyBuilder(final String name, 
                                  final Map<String, Object> attributes) {
        if (name == null) {
            throw new NullPointerException("name");
        }

        this.name = name;
        this.attributes = attributes;
    }

    protected static Map<String,Object> copyAttributes(final StatsKey template) {
        if (template.getAttributeCount() > 0) {
            return new HashMap<String,Object>(template.getAttributes());
        }

        return null;
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
    public StatsKey newKey() {

        if (name == null) {
            throw new IllegalStateException("Must specify a name");
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

        return new DefaultStatsKey(name, attributes);
    }

}

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

/**
 * A {@link StatsKey} implementation that stores attributes in a {@link Map}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsKey extends AbstractStatsKey {

    private static final long serialVersionUID = -9052397460294109721L;

    protected final Map<String,Object> attributes;

    protected DefaultStatsKey(final String name,
                              final StatsKeyFactory keyFactory,
                              final Map<String,Object> attributes) {
        super(name, keyFactory);

        if (attributes == null) {
            throw new NullPointerException("attributes");
        }

        this.attributes = attributes;

        setHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAttributeCount() {
        return attributes.size();
    }

    @Override
    protected void appendAttributes(final StringBuilder buf) {
        buf.append(attributes);
    }
}

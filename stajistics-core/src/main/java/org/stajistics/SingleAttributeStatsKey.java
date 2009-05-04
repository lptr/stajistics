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
 * A {@link StatsKey} implementation that can store only a single attribute.
 *
 * @author The Stajistics Project
 */
public class SingleAttributeStatsKey extends AbstractStatsKey {

    private static final long serialVersionUID = -4220144422224946459L;

    private final String attrName;
    private final Object attrValue;

    protected SingleAttributeStatsKey(final String name,
                                      final StatsKeyFactory keyFactory,
                                      final String attrName,
                                      final Object attrValue) {
        super(name, keyFactory);

        if (attrName != null && attrValue == null) {
            throw new NullPointerException("attrValue");
        }

        this.attrName = attrName;
        this.attrValue = attrValue;

        setHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getAttribute(final String name) {
        if (name.equals(attrName)) {
            return attrValue;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String,Object> getAttributes() {
        if (attrName == null) {
            return Collections.emptyMap();
        }

        return Collections.singletonMap(attrName, attrValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAttributeCount() {
        return attrName == null ? 0 : 1;
    }

    @Override
    protected void appendAttributes(final StringBuilder buf) {
        buf.append('{');
        buf.append(attrName);
        buf.append('=');
        buf.append(attrValue);
        buf.append('}');
    }
}

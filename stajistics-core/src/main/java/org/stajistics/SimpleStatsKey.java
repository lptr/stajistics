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

import java.util.Collections;
import java.util.Map;

/**
 * A {@link StatsKey} implementation that does not store any attributes. Do not
 * instantiate this class directly. Instead use the {@link StatsKeyFactory} provided by
 * {@link StatsManager#getKeyFactory()}, or {@link Stats#newKey(String)}, or
 * {@link Stats#buildKey(String)}.
 *
 * @author The Stajistics Project
 */
public class SimpleStatsKey extends AbstractStatsKey {

    /**
     * Create a new instance.
     *
     * @param name The key name. Must not be <tt>null</tt>.
     * @param keyFactory The factory that supports the creation of copies of this StatsKey instance.
     * @throws NullPointerException If <tt>name</tt> is <tt>null</tt>.
     */
    public SimpleStatsKey(final String name,
                          final StatsKeyFactory keyFactory) {
        super(name, keyFactory);

        setHashCode();
    }

    /**
     * @return <tt>null</tt>.
     */
    @Override
    public Object getAttribute(final String name) {
        return null;
    }

    /**
     * @return An empty {@link Map}.
     */
    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    /**
     * @return <tt>0</tt>.
     */
    @Override
    public int getAttributeCount() {
        return 0;
    }
}

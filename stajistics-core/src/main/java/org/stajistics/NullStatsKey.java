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
 * 
 * 
 *
 * @author The Stajistics Project
 */
public final class NullStatsKey implements StatsKey {

    private static final long serialVersionUID = 6441310707399203190L;

    private static final NullStatsKey instance = new NullStatsKey();

    private NullStatsKey() {}

    public static NullStatsKey getInstance() {
        return instance;
    }

    @Override
    public String getName() {
        return "null";
    }

    @Override
    public Object getAttribute(final String name) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public int getAttributeCount() {
        return 0;
    }

    @Override
    public StatsKeyBuilder buildCopy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }

}

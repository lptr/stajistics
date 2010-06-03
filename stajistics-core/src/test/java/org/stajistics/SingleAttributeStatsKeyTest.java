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

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class SingleAttributeStatsKeyTest extends AbstractStatsKeyTestCase {

    @Override
    protected StatsKey createStatsKey(final String name,
                                      final StatsKeyFactory keyFactory,
                                      final Map<String,Object> attributes) {
        String attrName = null;
        Object attrValue = null;

        if (attributes != null && !attributes.isEmpty()) {
            Map.Entry<String,Object> entry = attributes.entrySet()
                                                       .iterator()
                                                       .next();
            attrName = entry.getKey();
            attrValue = entry.getValue();
        }

        return new SingleAttributeStatsKey(name,
                                           keyFactory,
                                           attrName,
                                           attrValue);
    }

    @Test
    @Override
    public void testConstructWithNullAttributes() {
        StatsKey key = createStatsKey(TEST_NAME, mockKeyFactory, null);
        assertEquals(0, key.getAttributeCount());
        assertTrue(key.getAttributes().isEmpty());
    }
}

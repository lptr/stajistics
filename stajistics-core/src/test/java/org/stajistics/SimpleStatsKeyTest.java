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

import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class SimpleStatsKeyTest extends AbstractStatsKeyTestCase {

    @Override
    protected StatsKey createStatsKey(final String name,
                                      final StatsKeyFactory keyFactory,
                                      final Map<String, Object> attributes) {
        return new SimpleStatsKey(name, keyFactory);
    }

    @Test
    public void testGetAttribute() {
        final Map<String,Object> testAttributes = new HashMap<String,Object>();
        testAttributes.put("test1", Boolean.TRUE);

        StatsKey key = createStatsKey(TEST_NAME, null, testAttributes);
        assertNull(key.getAttribute("test1"));
        assertNull(key.getAttribute("test2"));
    }

    @Override
    @Ignore
    public void testConstructWithNullAttributes() {}

    @Override
    @Ignore
    public void testEqualsKeyWithDifferentAttributes() {}

    @Override
    @Ignore
    public void testToStringContainsAttributes() {}

    @Override
    @Ignore
    public void testCompareToWithAttributes() {}
}

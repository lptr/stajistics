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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.stajistics.StatsKey;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsKeyTest {

    private static final String TEST_NAME = "testName";
    private static final String TEST_UNIT = "testUnit";

    @Test
    public void testBuild1() {
        StatsKey statsKey = Stats.newKey(TEST_NAME);
        assertEquals(TEST_NAME, statsKey.getName());
        assertEquals("ms", statsKey.getUnit());
        assertEquals(0, statsKey.getAttributes().size());
    }

    @Test
    public void testBuild2() {
        StatsKey statsKey = Stats.buildKey(TEST_NAME)
                                 .withUnit(TEST_UNIT)
                                 .newKey();

        assertEquals(TEST_NAME, statsKey.getName());
        assertEquals(TEST_UNIT, statsKey.getUnit());
        assertEquals(0, statsKey.getAttributes().size());
    }

    @Test
    public void testBuild3() {
        StatsKey statsKey = Stats.buildKey(TEST_NAME)
                                 .withUnit(TEST_UNIT)
                                 .withAttribute("one", "one")
                                 .newKey();

        assertEquals(TEST_NAME, statsKey.getName());
        assertEquals(TEST_UNIT, statsKey.getUnit());
        assertEquals(1, statsKey.getAttributes().size());
        assertEquals("one", statsKey.getAttributes().get("one"));
    }

    @Test
    public void testCopy1() {
        StatsKey firstKey = Stats.buildKey(TEST_NAME)
                                 .withUnit(TEST_UNIT)
                                 .withAttribute("one", "one")
                                 .newKey();

        StatsKey secondKey = firstKey.buildCopy().newKey();

        assertEquals(firstKey, secondKey);
        assertEquals(firstKey.hashCode(), secondKey.hashCode());
        assertEquals(firstKey.getName(), secondKey.getName());
        assertEquals(firstKey.getUnit(), secondKey.getUnit());
        assertEquals(firstKey.getAttributes(), secondKey.getAttributes());
    }

    @Test
    public void testCopy2() {
        StatsKey firstKey = Stats.buildKey(TEST_NAME)
                                 .withUnit(TEST_UNIT)
                                 .withAttribute("one", "one")
                                 .newKey();

        StatsKey secondKey = firstKey.buildCopy()
                                     .withUnit(TEST_UNIT + "2")
                                     .newKey();

        assertFalse(firstKey.equals(secondKey));
        assertTrue(firstKey.hashCode() != secondKey.hashCode());
        assertEquals(firstKey.getName(), secondKey.getName());
        assertFalse(firstKey.getUnit().equals(secondKey.getUnit()));
        assertEquals(TEST_UNIT + "2", secondKey.getUnit());
        assertEquals(firstKey.getAttributes(), secondKey.getAttributes());
    }

    @Test
    public void testCopy3() {
        StatsKey firstKey = Stats.buildKey(TEST_NAME)
                                 .withUnit(TEST_UNIT)
                                 .withAttribute("one", "one")
                                 .newKey();

        StatsKey secondKey = firstKey.buildCopy()
                                     .withAttribute("one", "two")
                                     .newKey();

        assertFalse(firstKey.equals(secondKey));
        assertTrue(firstKey.hashCode() != secondKey.hashCode());
        assertEquals(firstKey.getName(), secondKey.getName());
        assertEquals(firstKey.getUnit(), secondKey.getUnit());
        assertFalse(firstKey.getAttributes().equals(secondKey.getAttributes()));
        assertEquals("two", secondKey.getAttributes().get("one"));
        assertEquals(1, secondKey.getAttributes().size());
    }
}

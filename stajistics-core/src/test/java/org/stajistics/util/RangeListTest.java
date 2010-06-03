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
package org.stajistics.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class RangeListTest {

    private static final double DELTA = 0.000000000000001;

    // no overlap
    private static final List<Range> TEST_RANGES1 = new ArrayList<Range>(
            Arrays.asList(new Range(0, 2),
                          new Range(4, 6),
                          new Range(8, 10)));

    // no overlap
    private static final List<Range> TEST_RANGES2 = new ArrayList<Range>(
            Arrays.asList(new Range(0, 2),
                          new Range(3, 5),
                          new Range(6, 8)));

    // overlap for inclusive range end
    private static final List<Range> TEST_RANGES3 = new ArrayList<Range>(
            Arrays.asList(new Range(0, 2),
                          new Range(2, 4),
                          new Range(4, 6)));

    // overlap
    private static final List<Range> TEST_RANGES4 = new ArrayList<Range>(
            Arrays.asList(new Range[] {
                    new Range(0, 2),
                    new Range(1, 3),
                    new Range(2, 4)
            }));


    @Test
    public void testConstructList() {
        RangeList rl = new RangeList(TEST_RANGES1);
        assertEquals(TEST_RANGES1, rl.getRanges());
    }

    @Test
    public void testConstructNullList() {
        try {
            new RangeList(null);
            fail("Allowed construction with null range list");

        } catch (NullPointerException npe) {
            //expected
        }
    }

    @Test
    public void testConstructListBoolean() {
        RangeList rl = new RangeList(TEST_RANGES1, false);
        assertEquals(TEST_RANGES1, rl.getRanges());
        assertFalse(rl.isExclusiveRangeEnd());
    }

    @Test
    public void testHasOverlap1() {
        RangeList rl = new RangeList(TEST_RANGES1, true);
        assertFalse(rl.hasOverlap());

        rl = new RangeList(TEST_RANGES1, false);
        assertFalse(rl.hasOverlap());
    }

    @Test
    public void testHasOverlap2() {
        RangeList rl = new RangeList(TEST_RANGES2, true);
        assertFalse(rl.hasOverlap());

        rl = new RangeList(TEST_RANGES2, false);
        assertFalse(rl.hasOverlap());
    }

    @Test
    public void testHasOverlap3() {
        RangeList rl = new RangeList(TEST_RANGES3, true);
        assertFalse(rl.hasOverlap());

        rl = new RangeList(TEST_RANGES3, false);
        assertTrue(rl.hasOverlap());
    }

    @Test
    public void testHasOverlap4() {
        RangeList rl = new RangeList(TEST_RANGES4, true);
        assertTrue(rl.hasOverlap());

        rl = new RangeList(TEST_RANGES4, false);
        assertTrue(rl.hasOverlap());
    }

    @Test
    public void testGetMinBegin() {
        RangeList rl = new RangeList(TEST_RANGES1);
        assertEquals(0, rl.getMinBegin(), DELTA);
    }

    @Test
    public void testGetMaxEnd() {
        RangeList rl = new RangeList(TEST_RANGES1);
        assertEquals(10, rl.getMaxEnd(), DELTA);
    }

    @Test
    public void testSize() {
        RangeList rl = new RangeList(TEST_RANGES1);
        assertEquals(TEST_RANGES1.size(), rl.size());
    }

    @Test
    public void testIterator() {
        RangeList rl = new RangeList(TEST_RANGES1);
        Iterator<Range> i1 = TEST_RANGES1.iterator();
        Iterator<Range> i2 = rl.iterator();
        while (i1.hasNext()) {
            assertEquals(i1.next(), i2.next());
        }
        assertFalse(i2.hasNext());
    }

    @Test
    public void testIndexOfRangeContaining1() {

        final List<List<Range>> testRangesList = new ArrayList<List<Range>>(4);
        testRangesList.add(TEST_RANGES1);
        testRangesList.add(TEST_RANGES2);
        testRangesList.add(TEST_RANGES3);

        for (int i = 0; i < testRangesList.size(); i++) {

            final List<Range> testRanges = testRangesList.get(i);
            final RangeList rl = new RangeList(testRanges);

            assertEquals("TEST_RANGES" + (i + 1),
                         -1, rl.indexOfRangeContaining(testRanges.get(0).getBegin() - 1));

            for (int j = 0; j < testRanges.size(); j++) {

                final Range r = testRanges.get(j);

                for (double v = r.getBegin(); v < r.getEnd(); v++) {
                    assertEquals("TEST_RANGES" + (i + 1) + ", Range: " + j + ", value: " + v,
                                 j, rl.indexOfRangeContaining(v));
                }
            }

            assertEquals("TEST_RANGES" + (i + 1),
                         -1, rl.indexOfRangeContaining(testRanges.get(testRanges.size() - 1).getEnd() + 1));

        }
    }
}

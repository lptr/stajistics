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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class RangeTest {

    private static final double DELTA = 0.000000000000001;

    @Test
    public void testConstructBeginEnd() {
        double begin = 5.5;
        double end = 10.5;
        Range r = new Range(begin, end);
        assertEquals(begin, r.getBegin(), DELTA);
        assertEquals(end, r.getEnd(), DELTA);
        assertEquals(Range.defaultName(begin, end), r.getName());
    }

    @Test
    public void testConstructBeginEndName() {
        double begin = 3;
        double end = 4;
        Range r = new Range(begin, end, "test");
        assertEquals(begin, r.getBegin(), DELTA);
        assertEquals(end, r.getEnd(), DELTA);
        assertEquals("test", r.getName());
    }

    @Test
    public void testContains() {

        double begin = 1;
        double end = 10;

        Range r = new Range(begin, end);

        assertFalse(r.contains(0, true));
        assertFalse(r.contains(0, false));

        for (double i = begin; i < end; i++) {
            assertTrue(r.contains(i, true));
            assertTrue(r.contains(i, false));
        }

        assertFalse(r.contains(end, true));
        assertTrue(r.contains(end, false));
    }

    @Test
    public void testOverlaps1() {
        Range r1 = new Range(1.1, 3.3);
        Range r2 = new Range(4.4, 6.6);
        assertFalse(r1.overlaps(r2, true));
        assertFalse(r1.overlaps(r2, false));
        assertFalse(r2.overlaps(r1, true));
        assertFalse(r2.overlaps(r1, false));
    }

    @Test
    public void testOverlaps2() {
        Range r1 = new Range(1.1, 3.3);
        Range r2 = new Range(2.2, 4.4);
        assertTrue(r1.overlaps(r2, true));
        assertTrue(r1.overlaps(r2, false));
        assertTrue(r2.overlaps(r1, true));
        assertTrue(r2.overlaps(r1, false));
    }

    @Test
    public void testOverlaps3() {
        Range r1 = new Range(1.1, 3.3);
        Range r2 = new Range(3.3, 4.4);
        assertFalse(r1.overlaps(r2, true));
        assertTrue(r1.overlaps(r2, false));
        assertFalse(r2.overlaps(r1, true));
        assertTrue(r2.overlaps(r1, false));
    }

    @Test
    public void testIteratorExclusiveRangeEnd() {
        Range r = new Range(1, 10);
        Iterator<Double> it = r.iterator(1, true);

        for (int i = 1; i < 10; i++) {
            assertTrue(it.hasNext());
            assertEquals(i, it.next().intValue());
        }

        assertFalse(it.hasNext());

        try {
            it.remove();
        } catch (UnsupportedOperationException uoe) {
            // expected
        }

        try {
            it.next();
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testIteratorInclusiveRangeEnd() {
        Range r = new Range(1, 10);
        Iterator<Double> it = r.iterator(1, false);

        for (int i = 1; i <= 10; i++) {
            assertTrue(it.hasNext());
            assertEquals(i, it.next().intValue());
        }

        assertFalse(it.hasNext());

        try {
            it.remove();
        } catch (UnsupportedOperationException uoe) {
            // expected
        }

        try {
            it.next();
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    @Test
    public void testIterateEmptyRange() {
        Range r = new Range(1, 1);
        Iterator<Double> it = r.iterator(1, true);
        assertFalse(it.hasNext());

        try {
            it.next();
        } catch (NoSuchElementException nsee) {
            // expected
        }

        it = r.iterator(1, false);
        assertTrue(it.hasNext());
        assertEquals(1, it.next().intValue());
    }


}

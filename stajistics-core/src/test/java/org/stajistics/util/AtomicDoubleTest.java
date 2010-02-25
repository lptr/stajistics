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

import org.junit.Test;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class AtomicDoubleTest {

    private static final double DELTA = 0.000000000000001;

    @Test
    public void testConstruct() {
        assertEquals(0, new AtomicDouble().get(), DELTA);
    }

    @Test
    public void testConstructValue() {
        assertEquals(4.4, new AtomicDouble(4.4).get(), DELTA);
    }

    @Test
    public void testSet() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(5.5);
        assertEquals(5.5, ad.get(), DELTA);
    }

    @Test
    public void testLazySet() {
        AtomicDouble ad = new AtomicDouble();
        ad.lazySet(6.6);
        assertEquals(6.6, ad.get(), DELTA);
    }

    @Test
    public void testAddAndGet() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(2.2);
        double val = ad.addAndGet(3.3);
        assertEquals(5.5, val, DELTA);
        assertEquals(5.5, ad.get(), DELTA);
    }

    @Test
    public void testCompareAndSet() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(1.1);
        assertTrue(ad.compareAndSet(1.1, 2.2));
        assertEquals(2.2, ad.get(), DELTA);
        assertFalse(ad.compareAndSet(3.3, 4.4));
    }

    @Test
    public void testIncrementAndGet() {
        AtomicDouble ad = new AtomicDouble();
        for (double i = 1.0; i <= 100.0; i++) {
            assertEquals(i, ad.incrementAndGet(), DELTA);
        }
    }

    @Test
    public void testDecrementAndGet() {
        AtomicDouble ad = new AtomicDouble();
        for (double i = -1.0; i >= -100.0; i--) {
            assertEquals(i, ad.decrementAndGet(), DELTA);
        }
    }

    @Test
    public void testGetAndAdd() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(2.2);
        double val = ad.getAndAdd(3.3);
        assertEquals(2.2, val, DELTA);
        assertEquals(5.5, ad.get(), DELTA);
    }

    @Test
    public void testGetAndIncrement() {
        AtomicDouble ad = new AtomicDouble();
        for (double i = 0.0; i <= 100.0; i++) {
            assertEquals(i, ad.getAndIncrement(), DELTA);
        }
    }

    @Test
    public void testGetAndDecrement() {
        AtomicDouble ad = new AtomicDouble();
        for (double i = -0.0; i >= -100.0; i--) {
            assertEquals(i, ad.getAndDecrement(), DELTA);
        }
    }

    @Test
    public void testGetAndSet() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(2.2);
        double val = ad.getAndSet(3.3);
        assertEquals(2.2, val, DELTA);
        assertEquals(3.3, ad.get(), DELTA);
    }

    @Test
    public void testDoubleValue() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, ad.doubleValue(), DELTA);
        ad.set(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, ad.doubleValue(), DELTA);
        ad.set(Double.MIN_NORMAL);
        assertEquals(Double.MIN_NORMAL, ad.doubleValue(), DELTA);
        ad.set(Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, ad.doubleValue(), DELTA);
        ad.set(Double.NEGATIVE_INFINITY);
        assertEquals(Double.NEGATIVE_INFINITY, ad.doubleValue(), DELTA);
        ad.set(Double.NaN);
        assertEquals(Double.NaN, ad.doubleValue(), DELTA);
    }

    @Test
    public void testFloatValue() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, ad.floatValue(), DELTA);
        ad.set(Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, ad.floatValue(), DELTA);
        ad.set(Float.MIN_NORMAL);
        assertEquals(Float.MIN_NORMAL, ad.floatValue(), DELTA);
        ad.set(Float.POSITIVE_INFINITY);
        assertEquals(Float.POSITIVE_INFINITY, ad.floatValue(), DELTA);
        ad.set(Float.NEGATIVE_INFINITY);
        assertEquals(Float.NEGATIVE_INFINITY, ad.floatValue(), DELTA);
        ad.set(Float.NaN);
        assertEquals(Float.NaN, ad.floatValue(), DELTA);
    }

    @Test
    public void testLongValue() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, ad.longValue());
        ad.set(Long.MIN_VALUE);
        assertEquals(Long.MIN_VALUE, ad.longValue());
    }

    @Test
    public void testIntValue() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, ad.intValue());
        ad.set(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, ad.intValue());
    }

    @Test
    public void testShortValue() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, ad.shortValue());
        ad.set(Short.MIN_VALUE);
        assertEquals(Short.MIN_VALUE, ad.shortValue());
    }

    @Test
    public void testByteValue() {
        AtomicDouble ad = new AtomicDouble();
        ad.set(Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, ad.byteValue());
        ad.set(Byte.MIN_VALUE);
        assertEquals(Byte.MIN_VALUE, ad.byteValue());
    }
    
}

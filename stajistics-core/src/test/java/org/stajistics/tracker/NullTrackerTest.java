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
package org.stajistics.tracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.stajistics.NullStatsKey;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class NullTrackerTest {

    protected static final double DELTA = 0.0000000000001;

    @Test
    public void testIsTracking() {
        assertFalse(NullTracker.getInstance().isTracking());
    }

    @Test
    public void testStart() {
        assertSame(NullTracker.getInstance(),
                   NullTracker.getInstance().track());
        assertFalse(NullTracker.getInstance().isTracking());
    }

    @Test
    public void testGetValue() {
        assertEquals(0, NullTracker.getInstance().getValue(), DELTA);
    }

    @Test
    public void testGetStartTime() {
        assertEquals(0, NullTracker.getInstance().getStartTime());
    }

    @Test
    public void testReset() {
        assertSame(NullTracker.getInstance(),
                   NullTracker.getInstance().reset());
    }

    @Test
    public void testGetSession() {
        assertEquals(NullStatsKey.getInstance(),
                     NullTracker.getInstance()
                                .getSession()
                                .getKey());
    }

    @Test
    public void testSetValue() {
        double val = NullTracker.getInstance().getValue();
        NullTracker.getInstance().setValue(val + 100);
        assertEquals(val, NullTracker.getInstance().getValue(), DELTA);
    }

    @Test
    public void testUpdate() {
        double val = NullTracker.getInstance().getValue();
        NullTracker.getInstance().addValue(100);
        assertEquals(val, NullTracker.getInstance().getValue(), DELTA);
    }

    @Test
    public void testToString() {
        assertEquals(NullTracker.class.getSimpleName(),
                     NullTracker.getInstance().toString());
    }
}

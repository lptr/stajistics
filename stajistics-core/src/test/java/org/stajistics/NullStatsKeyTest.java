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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Mockery;
import org.junit.Test;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class NullStatsKeyTest {

    @Test
    public void testGetName() {
        assertNotNull(NullStatsKey.getInstance().getName());
    }

    @Test
    public void testGetAttribute() {
        assertNull(NullStatsKey.getInstance().getAttribute(null));
        assertNull(NullStatsKey.getInstance().getAttribute("attr"));
    }

    @Test
    public void testGetAttributes() {
        assertTrue(NullStatsKey.getInstance().getAttributes().isEmpty());
    }

    @Test
    public void testGetAttributeCount() {
        assertEquals(0, NullStatsKey.getInstance().getAttributeCount());
    }

    @Test
    public void testBuildCopy() {
        assertEquals(NullStatsKeyBuilder.getInstance(),
                     NullStatsKey.getInstance().buildCopy());
    }

    @Test
    public void testHashCode() {
        assertEquals(0, NullStatsKey.getInstance().hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(NullStatsKey.getInstance(),
                     NullStatsKey.getInstance());
        assertFalse(NullStatsKey.getInstance()
                                .equals(new Mockery().mock(StatsKey.class)));
    }
}

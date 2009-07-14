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
package org.stajistics.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author The Stajistics Project
 */
public class DefaultMetaDataTest {

    protected static final double DELTA = 0.0000000000001;

    private static final String FIELD_NAME = "test";

    private Map<String,Object> map;
    private MetaData metaData;

    @Before
    public void setUp() {
        map = new HashMap<String,Object>();
        metaData = new DefaultMetaDataSet(map).getMetaData(FIELD_NAME);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(metaData.isEmpty());
        metaData.setField("test", "value");
        assertFalse(metaData.isEmpty());
        metaData.removeField("test");
        assertTrue(metaData.isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, metaData.size());
        metaData.setField("test1", "value1");
        assertEquals(1, metaData.size());
        metaData.setField("test2", "value2");
        assertEquals(2, metaData.size());
        metaData.removeField("test2");
        assertEquals(1, metaData.size());
        metaData.removeField("test1");
        assertEquals(0, metaData.size());
    }

    @Test
    public void testSetAndGetAttribute() {
        assertNull(metaData.getField("test"));
        metaData.setField("test", "value");
        assertEquals("value", metaData.getField("test"));
    }

    @Test
    public void testRemoveAttribute() {
        metaData.setField("test", "value");
        assertEquals("value", metaData.removeField("test"));
        assertNull(metaData.removeField("test"));
        assertTrue(metaData.isEmpty());
    }

    @Test
    public void testClear() {
        metaData.setField("test1", "value1");
        metaData.setField("test2", "value2");
        metaData.clear();
        assertTrue(metaData.isEmpty());
        assertEquals(0, metaData.size());
        assertNull(metaData.getField("test1"));
        assertNull(metaData.getField("test2"));
    }

    @Test
    public void testGetAttribueWithType() {
        metaData.setField("test1", 3.0);
        assertEquals(3.0, metaData.getField("test1", Double.class), DELTA);
        metaData.setField("test2", "value2");
        assertEquals("value2", metaData.getField("test2", String.class));
        try {
            metaData.getField("test1", String.class);
            fail("Allowed cast from double to String");
        } catch (ClassCastException cce) {
            // expected
        }
    }
}

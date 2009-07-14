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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultDataSetTest {

    protected static final double DELTA = 0.0000000000001;

    private DataSet dataSet;

    @Before
    public void setUp() {
        dataSet = new DefaultDataSet();
    }

    @Test
    public void testInitializedState() {
        assertTrue(dataSet.isEmpty());
        assertEquals(0, dataSet.size());
        assertNotNull(dataSet.getFieldNames());
        assertTrue(dataSet.getFieldNames().isEmpty());
        assertNull(dataSet.getField("test1"));
        assertNull(dataSet.getField("test2", Double.class));
        assertNull(dataSet.removeField("test3"));
    }

    @Test
    public void testSize() {
        dataSet.setField("test1", "value1");
        assertEquals(1, dataSet.size());
        dataSet.setField("test2", "value2");
        assertEquals(2, dataSet.size());
        dataSet.removeField("test2");
        assertEquals(1, dataSet.size());
        dataSet.removeField("test1");
        assertEquals(0, dataSet.size());
    }

    @Test
    public void testIsEmpty() {
        dataSet.setField("test1", "value1");
        assertFalse(dataSet.isEmpty());
        dataSet.setField("test2", "value2");
        assertFalse(dataSet.isEmpty());
        dataSet.removeField("test2");
        assertFalse(dataSet.isEmpty());
        dataSet.removeField("test1");
        assertTrue(dataSet.isEmpty());
    }
    
    @Test
    public void testSetGetField() {
        dataSet.setField("test1", "value1");
        assertEquals("value1", dataSet.getField("test1"));
        dataSet.setField("test2", "value2");
        assertEquals("value2", dataSet.getField("test2"));
    }

    @Test
    public void testRemoveField() {
        dataSet.setField("test", "value");
        assertEquals("value", dataSet.removeField("test"));
        assertNull(dataSet.removeField("test"));
        assertTrue(dataSet.isEmpty());
    }

    @Test
    public void testGetFieldWithType() {
        dataSet.setField("test1", 3.0);
        assertEquals(3.0, dataSet.getField("test1", Double.class), DELTA);
        dataSet.setField("test2", "value2");
        assertEquals("value2", dataSet.getField("test2", String.class));
        try {
            dataSet.getField("test1", String.class);
            fail("Allowed cast from double to String");
        } catch (ClassCastException cce) {
            // expected
        }
    }

    @Test
    public void testGetMetaData() {
        assertNotNull(dataSet.getMetaData());
    }

    @Test
    public void testGetFieldMetaDataSet() {
        assertNotNull(dataSet.getFieldMetaDataSet());
    }
}

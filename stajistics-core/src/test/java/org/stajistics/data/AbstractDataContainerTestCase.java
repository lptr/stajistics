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
package org.stajistics.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.TestUtil;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public abstract class AbstractDataContainerTestCase extends AbstractStajisticsTestCase {

    private DataContainer dataContainer;

    protected abstract DataContainer createDataContainer();

    protected DataContainer dc() {
        return dataContainer;
    }

    @Before
    public void setUp() {
        dataContainer = createDataContainer();
    }

    @Test
    public void testInitializedState() {
        assertTrue(dataContainer.isEmpty());
        assertEquals(0, dataContainer.size());
        assertNotNull(dataContainer.getFieldNames());
        assertTrue(dataContainer.getFieldNames().isEmpty());
        assertNull(dataContainer.getField("test1"));
        assertNull(dataContainer.getField("test2", Double.class));
        assertNull(dataContainer.removeField("test3"));
    }

    @Test
    public void testSize() {
        dataContainer.setField("test1", "value1");
        assertEquals(1, dataContainer.size());
        dataContainer.setField("test2", "value2");
        assertEquals(2, dataContainer.size());
        dataContainer.removeField("test2");
        assertEquals(1, dataContainer.size());
        dataContainer.removeField("test1");
        assertEquals(0, dataContainer.size());
    }

    @Test
    public void testIsEmpty() {
        dataContainer.setField("test1", "value1");
        assertFalse(dataContainer.isEmpty());
        dataContainer.setField("test2", "value2");
        assertFalse(dataContainer.isEmpty());
        dataContainer.removeField("test2");
        assertFalse(dataContainer.isEmpty());
        dataContainer.removeField("test1");
        assertTrue(dataContainer.isEmpty());
    }

    @Test
    public void testGetFieldWithNullName() {
        assertNull(dataContainer.getField(null));
    }

    @Test
    public void testGetFieldWithType() {
        dataContainer.setField("test1", 3.0);
        assertEquals(3.0, dataContainer.getField("test1", Double.class), TestUtil.DELTA);
        dataContainer.setField("test2", "value2");
        assertEquals("value2", dataContainer.getField("test2", String.class));
        try {
            dataContainer.getField("test1", String.class);
            fail("Allowed cast from double to String");
        } catch (ClassCastException cce) {
            // expected
        }
    }

    @Test
    public void testGetFieldWithNullNameAndType() {
        assertNull(dataContainer.getField(null, String.class));
    }

    @Test
    public void testGetFieldWithNameAndNullType() {
        dataContainer.setField("test", 1.0);

        Class<Double> type = null;
        Double value = dataContainer.getField("test", type);
        assertEquals(1.0, value, TestUtil.DELTA);
    }

    @Test
    public void testFieldFieldWithNameAndWrongType() {
        dataContainer.setField("test", 1.0);

        try {
            dataContainer.getField("test", String.class);
            fail("Failed to throw ClassCastException");
        } catch (ClassCastException cce) {
            // Expected
        }
    }

    @Test
    public void testGetFieldWithNullNameAndNullDefault() {
        assertNull(dataContainer.getField(null, null));
    }

    @Test
    public void testGetFieldWithNullNameAndDefault() {
        assertEquals("", dataContainer.getField(null, ""));
    }

    @Test
    public void testGetFieldWithNonExistentNameAndDefault() {
        assertEquals(1.0, dataContainer.getField("test", 1.0), TestUtil.DELTA);
    }

    @Test
    public void testFieldFieldWithExistingNameAndDefault() {
        dataContainer.setField("test", 1.0);

        assertEquals(1.0, dataContainer.getField("test", 2.0), TestUtil.DELTA);
    }

    @Test
    public void testFieldFieldWithNameAndDefaultOfWrongType() {
        dataContainer.setField("test", 1.0);

        String value = dataContainer.getField("test", "default");

        assertEquals("default", value);
    }

    @Test
    public void testSetGetField() {
        dataContainer.setField("test1", "value1");
        assertEquals("value1", dataContainer.getField("test1"));
        dataContainer.setField("test2", "value2");
        assertEquals("value2", dataContainer.getField("test2"));
    }

    @Test
    public void testSetFieldWithNullValue() {
        try {
            dataContainer.setField("test", null);
        } catch (NullPointerException npe) {
            assertEquals("value", npe.getMessage());
        }
    }

    @Test
    public void testSetFieldWithEmptyName() {
        try {
            dataContainer.setField("", "something");
        } catch (IllegalArgumentException iae) {
            assertEquals("empty name", iae.getMessage());
        }
    }

    @Test
    public void testSetFieldWithNullName() {
        try {
            dataContainer.setField(null, "something");
        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
        try {
            dataContainer.setField(null, null);
        } catch (NullPointerException npe) {
            assertEquals("name", npe.getMessage());
        }
    }

    @Test
    public void testRemoveField() {
        dataContainer.setField("test", "value");
        assertEquals("value", dataContainer.removeField("test"));
        assertNull(dataContainer.removeField("test"));
        assertTrue(dataContainer.isEmpty());
    }

    @Test
    public void testClear() {
        dataContainer.setField("test", 1);
        assertFalse(dataContainer.isEmpty());
        assertEquals(1, dataContainer.size());

        dataContainer.clear();

        assertNull(dataContainer.getField("test"));
        assertTrue(dataContainer.isEmpty());
        assertEquals(0, dataContainer.size());
    }
}

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class NullDataSetTest {

    protected DataContainer dc() {
        return NullDataSet.getInstance();
    }

    @Test
    public void testGetMetaData() {
        assertEquals(NullMetaData.getInstance(),
                     NullDataSet.getInstance().getMetaData());
    }

    @Test
    public void testGetFieldMetaDataSet() {
        MetaDataSet mds = NullDataSet.getInstance().getFieldMetaDataSet();
        assertNotNull(mds);
        assertEquals(NullMetaData.getInstance(),
                     mds.getMetaData("anything"));
    }

    @Test
    public void testGetField() {
        assertNull(dc().getField("a"));
        assertNull(dc().getField("b"));
    }

    @Test
    public void testGetFieldWithClass() {
        assertNull(dc().getField("a", Double.class));
        assertNull(dc().getField("b", Long.class));
    }

    @Test
    public void testGetFieldNames() {
        Set<String> fieldNames = dc().getFieldNames();
        assertNotNull(fieldNames);
        assertTrue(fieldNames.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(dc().isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, dc().size());
    }

    @Test
    public void testClear() {
        dc().clear(); // umm.. what to test?
    }

    @Test
    public void testSetAndRemoveField() {
        dc().setField("a", true);
        assertNull(dc().removeField("a"));
    }

}

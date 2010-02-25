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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultFieldMetaDataSetTest {

    private Map<String,Object> map;
    private FieldMetaDataSet fieldMetaDataSet;

    @Before
    public void setUp() {
        map = new HashMap<String,Object>();
        fieldMetaDataSet = new DefaultFieldMetaDataSet(map);
    }

    @Test
    public void testConstructWithNullMap() {
        try {
            new DefaultFieldMetaDataSet(null);
            fail("Allowed construction with null metaDataMap");
        } catch (NullPointerException npe) {
            assertEquals("metaDataMap", npe.getMessage());
        }
    }

    @Test
    public void testGetMetaData() {
        MetaData metaData1 = fieldMetaDataSet.getMetaData("test1");
        assertNotNull(metaData1);
        assertSame(metaData1, fieldMetaDataSet.getMetaData("test1"));
        MetaData metaData2 = fieldMetaDataSet.getMetaData("test2");
        assertNotNull(metaData2);
        assertSame(metaData2, fieldMetaDataSet.getMetaData("test2"));
        assertFalse(metaData1 == metaData2);
        assertFalse(metaData1.equals(metaData2));
    }

    @Test
    public void testClear() {
        MetaData metaData = fieldMetaDataSet.getMetaData("test");
        metaData.setField("test", "value");
        assertEquals("value", metaData.getField("test"));
        fieldMetaDataSet.clear();
        assertNull(metaData.getField("test"));
        metaData = fieldMetaDataSet.getMetaData("test");
        assertNull(metaData.getField("test"));
    }
    
}

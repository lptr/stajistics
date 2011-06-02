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
package org.stajistics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * @author The Stajistics Project
 *
 */
//@RunWith(JMock.class)
public class StatsPropertiesTest extends AbstractStajisticsTestCase {

    private Map<String,String> propsMap;

    @Before
    public void setUp() {

        propsMap = new HashMap<String,String>();
        propsMap.put("string", "string");
        propsMap.put("boolean", Boolean.TRUE.toString());
        propsMap.put("int", String.valueOf(Integer.MAX_VALUE));
        propsMap.put("float", String.valueOf(Float.MAX_VALUE));
        propsMap.put("double", String.valueOf(Double.MAX_VALUE));
        propsMap.put("long", String.valueOf(Long.MAX_VALUE));

        StatsProperties.load(new StatsProperties.MapStatsProperties(propsMap));
    }
    
    @After
    public void tearDown() {
    	// Revert back to using system properties after tests
    	StatsProperties.load(new StatsProperties.SystemStatsProperties());
    }

    @Test
    public void testLoadAndGetInstance() {
        final StatsProperties props = new StatsProperties.MapStatsProperties(Collections.emptyMap());
        StatsProperties.load(props);

        assertEquals(props, StatsProperties.getInstance());
    }

    @Test
    public void testGetInstanceNotNull() {
        StatsProperties.load(null);
        assertNotNull(StatsProperties.getInstance());
    }

    @Test
    public void testGetProperty() {
        assertEquals("string", StatsProperties.getProperty("string"));
        assertNull(StatsProperties.getProperty("string1"));
    }

    @Test
    public void testGetPropertyWithDefault() {
        assertEquals("string", StatsProperties.getProperty("string", null));
        assertNull(StatsProperties.getProperty("string1", null));
        assertEquals("default", StatsProperties.getProperty("string1", "default"));
    }

    @Test
    public void testGetBooleanProperty() {
        assertEquals(true, StatsProperties.getBooleanProperty("boolean"));
        assertNull(StatsProperties.getBooleanProperty("boolean1"));
    }

    @Test
    public void testGetBooleanPropertyWithDefault() {
        assertEquals(true, StatsProperties.getBooleanProperty("boolean", null));
        assertNull(StatsProperties.getBooleanProperty("boolean1", null));
        assertEquals(false, StatsProperties.getBooleanProperty("boolean1", false));
    }

    @Test
    public void testGetIntegerProperty() {
        assertEquals(Integer.MAX_VALUE, (int)StatsProperties.getIntegerProperty("int"));
        assertNull(StatsProperties.getIntegerProperty("int1"));
    }

    @Test
    public void testGetIntegerPropertyWithDefault() {
        assertEquals(Integer.MAX_VALUE, (int)StatsProperties.getIntegerProperty("int", 1));
        assertNull(StatsProperties.getIntegerProperty("int1", null));
        assertEquals(2, (int)StatsProperties.getIntegerProperty("int1", 2));
    }

    @Test
    public void testGetFloatProperty() {
        assertEquals("" + Float.MAX_VALUE, "" + StatsProperties.getFloatProperty("float"));
        assertNull(StatsProperties.getFloatProperty("float1"));
    }

    @Test
    public void testGetFloatPropertyWithDefault() {
        assertEquals("" + Float.MAX_VALUE, "" + StatsProperties.getFloatProperty("float", 1f));
        assertNull(StatsProperties.getFloatProperty("float1", null));
        assertEquals(2f, StatsProperties.getFloatProperty("float1", 2f), TestUtil.DELTA);
    }

    @Test
    public void testGetLongProperty() {
        assertEquals(Long.MAX_VALUE, (long)StatsProperties.getLongProperty("long"));
        assertNull(StatsProperties.getLongProperty("long1"));
    }

    @Test
    public void testGetLongPropertyWithDefault() {
        assertEquals(Long.MAX_VALUE, (long)StatsProperties.getLongProperty("long", 1L));
        assertNull(StatsProperties.getLongProperty("long1", null));
        assertEquals(2L, (long)StatsProperties.getLongProperty("long1", 2L));
    }

    @Test
    public void testGetDoubleProperty() {
        assertEquals(Double.MAX_VALUE, StatsProperties.getDoubleProperty("double"), TestUtil.DELTA);
        assertNull(StatsProperties.getDoubleProperty("double1"));
    }

    @Test
    public void testGetDoublePropertyWithDefault() {
        assertEquals(Double.MAX_VALUE, StatsProperties.getDoubleProperty("double", 1D), TestUtil.DELTA);
        assertNull(StatsProperties.getDoubleProperty("double1", null));
        assertEquals(2D, StatsProperties.getDoubleProperty("double1", 2D), TestUtil.DELTA);
    }

    @Test
    public void testSystemProperties() {
        try {
            System.setProperty("test", "value");
            StatsProperties.load(new StatsProperties.SystemStatsProperties());

            assertEquals("value", StatsProperties.getProperty("test"));

        } finally {
            System.getProperties().remove("test");
        }
    }

    @Test
    public void testAsChildOf() {
        Map<String,String> propsMapChild = new HashMap<String,String>();
        propsMapChild.put("boolean", Boolean.FALSE.toString());

        StatsProperties.load(new StatsProperties.MapStatsProperties(propsMapChild)
                                                .asChildOf(StatsProperties.getInstance()));

        assertFalse(StatsProperties.getBooleanProperty("boolean"));
        assertEquals("string", StatsProperties.getProperty("string"));
    }
}

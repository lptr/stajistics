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
package org.stajistics.tracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class CompositeTrackerFactoryTest {

    protected Mockery mockery;

    private TrackerFactory<Tracker> trackerFactory1;
    private TrackerFactory<Tracker> trackerFactory2;
    private TrackerFactory<Tracker> trackerFactory3;

    private Map<String,TrackerFactory<Tracker>> factoryMap;

    private CompositeTrackerFactory<Tracker> cTrackerFactory;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockery = new Mockery();

        trackerFactory1 = mockery.mock(TrackerFactory.class, "trackerFactory1");
        trackerFactory2 = mockery.mock(TrackerFactory.class, "trackerFactory2");
        trackerFactory3 = mockery.mock(TrackerFactory.class, "trackerFactory3");

        factoryMap = new LinkedHashMap<String,TrackerFactory<Tracker>>();
        factoryMap.put("trackerFactory1", trackerFactory1);
        factoryMap.put("trackerFactory2", trackerFactory2);
        factoryMap.put("trackerFactory3", trackerFactory3);

        cTrackerFactory = new CompositeTrackerFactory<Tracker>(factoryMap);
    }

    @Test
    public void testConstructWithNullFactoryMap() {
        try {
            new CompositeTrackerFactory<Tracker>(null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("factoryMap", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithEmptyFactoryMap() {
        Map<String,TrackerFactory<Tracker>> map = Collections.emptyMap();
        try {
            new CompositeTrackerFactory<Tracker>(map);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("factoryMap is empty", iae.getMessage());
        }
    }

    @Test
    public void testConstructWithNullFactoryInMap() {
        Map<String,TrackerFactory<Tracker>> map = new HashMap<String,TrackerFactory<Tracker>>();
        map.put("test", null);

        try {
            new CompositeTrackerFactory<Tracker>(map);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("null factory for nameSuffix: test", iae.getMessage());
        }
    }

    @Test
    public void testGetFactoryMap() {
        assertEquals(factoryMap, cTrackerFactory.getFactoryMap());
    }

    @Test
    public void testComposites() {
        assertEquals(new ArrayList<TrackerFactory<Tracker>>(factoryMap.values()), 
                     new ArrayList<TrackerFactory<Tracker>>(cTrackerFactory.composites()));
    }

    @Test
    public void testBuildNotNull() {
        CompositeTrackerFactory.Builder<Tracker> builder = CompositeTrackerFactory.build();
        assertNotNull(builder);
    }

    @Test
    public void testBuilderWithFactoryWithNullNameSuffix() {
        CompositeTrackerFactory.Builder<Tracker> builder = CompositeTrackerFactory.build();
        try {
            builder.withFactory(null, trackerFactory1);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("nameSuffix", npe.getMessage());
        }
    }

    @Test
    public void testBuilderWithFactoryWithEmptyNameSuffix() {
        CompositeTrackerFactory.Builder<Tracker> builder = CompositeTrackerFactory.build();
        try {
            builder.withFactory("", trackerFactory1);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("nameSuffix is empty", iae.getMessage());
        }
    }

    @Test
    public void testBuilderWithFactoryWithBlankNameSuffix() {
        CompositeTrackerFactory.Builder<Tracker> builder = CompositeTrackerFactory.build();
        try {
            builder.withFactory("\t ", trackerFactory1);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("nameSuffix is empty", iae.getMessage());
        }
    }

    @Test
    public void testBuilderWithFactoryWithNullFactory() {
        CompositeTrackerFactory.Builder<Tracker> builder = CompositeTrackerFactory.build();
        try {
            builder.withFactory("test", null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("factory", npe.getMessage());
        }
    }

    @Test
    public void testGetTrackerType() {

        final Class<Tracker> type = Tracker.class;

        mockery.checking(new Expectations() {{
            one(trackerFactory1).getTrackerType(); will(returnValue(type));
        }});

        assertSame(type, cTrackerFactory.getTrackerType());
    }

}

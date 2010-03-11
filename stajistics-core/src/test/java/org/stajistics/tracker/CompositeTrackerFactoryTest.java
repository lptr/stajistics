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
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Map;

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

    @Before
    public void setUp() {
        mockery = new Mockery();
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

    
}

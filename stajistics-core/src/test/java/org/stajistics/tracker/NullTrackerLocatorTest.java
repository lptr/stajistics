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

import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;

import static org.junit.Assert.assertEquals;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class NullTrackerLocatorTest extends AbstractStajisticsTestCase {

    private final TrackerLocator ntl = NullTrackerLocator.getInstance();

    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
    }

    @Test
    public void testGetTracker() {
        assertEquals(NullTracker.getInstance(),
                     ntl.getTracker(mockKey));
    }

    @Test
    public void testGetSpanTrackerWithKey() {
        assertEquals(NullTracker.getInstance(),
                     ntl.getSpanTracker(mockKey));
    }

    @Test
    public void testGetSpanTrackerWithKeys() {
        assertEquals(NullTracker.getInstance(),
                     ntl.getSpanTracker(new StatsKey[] { mockKey }));
    }

    @Test
    public void testGetIncidentTrackerWithKey() {
        assertEquals(NullTracker.getInstance(),
                     ntl.getIncidentTracker(mockKey));
    }

    @Test
    public void testGetIncidentTrackerWithKeys() {
        assertEquals(NullTracker.getInstance(),
                     ntl.getIncidentTracker(new StatsKey[] { mockKey }));
    }

    @Test
    public void testGetManualTracker() {
        assertEquals(NullTracker.getInstance(),
                     ntl.getManualTracker(mockKey));
    }
}

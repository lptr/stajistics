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
package org.stajistics.tracker.manual;

import static org.junit.Assert.assertEquals;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class DefaultManualTrackerTest {

    protected static final double DELTA = 0.0000000000001;

    private Mockery mockery;
    private StatsSession mockSession;

    @Before
    public void setUp() throws Exception {
        mockery = new Mockery();
        mockSession = mockery.mock(StatsSession.class);
    }

    @Test
    public void testInitialState() {
        DefaultManualTracker mTracker = new DefaultManualTracker(mockSession);
        assertEquals(0.0, mTracker.getValue(), DELTA);
    }

    @Test
    public void testUpdate() {
        DefaultManualTracker mTracker = new DefaultManualTracker(mockSession);

        int total = 0;

        for (int i = 0; i < 100; i++) {
            mTracker.addValue(i);
            total += i;

            assertEquals(total, mTracker.getValue(), DELTA);
        }
    }

    @Test
    public void testSetValue() {
        DefaultManualTracker mTracker = new DefaultManualTracker(mockSession);

        for (int i = 0; i < 100; i++) {
            mTracker.setValue(i);

            assertEquals(i, mTracker.getValue(), DELTA);
        }
    }
}

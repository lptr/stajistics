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
package org.stajistics.tracker;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultManualTrackerTest extends AbstractStatsTrackerTestCase {

    @Override
    protected DefaultManualStatsTracker createStatsTracker() {
        return new DefaultManualStatsTracker(mockSession);
    }

    @Test
    public void testInitialState() {
        DefaultManualStatsTracker mTracker = createStatsTracker();

        assertEquals(0, mTracker.getTimeStamp());
        assertEquals(0.0, mTracker.getValue(), DELTA);
    }

    @Test
    public void testUpdate() {
        DefaultManualStatsTracker mTracker = createStatsTracker();

        int total = 0;

        for (int i = 0; i < 100; i++) {
            mTracker.update(i);
            total += i;

            assertEquals(total, mTracker.getValue(), DELTA);
        }
    }

    @Test
    public void testSetValue() {
        DefaultManualStatsTracker mTracker = createStatsTracker();

        for (int i = 0; i < 100; i++) {
            mTracker.setValue(i);

            assertEquals(i, mTracker.getValue(), DELTA);
        }
    }
}

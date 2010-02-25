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
package org.stajistics.tracker.incident;

import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.tracker.AbstractCompositeStatsTrackerTestCase;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class IncidentCompositeStatsTrackerTest 
    extends AbstractCompositeStatsTrackerTestCase<IncidentTracker> {

    @Override
    protected IncidentTracker[] createMockTrackers() {
        return new IncidentTracker[] {
                   mockery.mock(IncidentTracker.class, "IncidentStatsTracker1"),
                   mockery.mock(IncidentTracker.class, "IncidentStatsTracker2"),
                   mockery.mock(IncidentTracker.class, "IncidentStatsTracker3")
               };
    }

    @Override
    protected IncidentCompositeStatsTracker createCompositeStatsTracker(final List<IncidentTracker> mockTrackers) {
        return new IncidentCompositeStatsTracker(mockTrackers);
    }

    @Override
    protected IncidentCompositeStatsTracker createCompositeStatsTracker(final IncidentTracker[] mockTrackers) {
        return new IncidentCompositeStatsTracker(mockTrackers);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructWithEmptyTrackerArray() {
        new IncidentCompositeStatsTracker();
    }

    @Test
    public void testIncident() {

        mockery.checking(new Expectations() {{
            for (int i = 0; i < mockTrackers.length; i++) {
                one(mockTrackers[i]).incident(); will(returnValue(mockTrackers[i]));
            }
        }});

        IncidentCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.incident();
    }

}

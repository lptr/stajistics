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
public class ManualCompositeStatsTrackerTest 
    extends AbstractCompositeStatsTrackerTestCase<ManualTracker> {

    @Override
    protected ManualTracker[] createMockTrackers() {
        return new ManualTracker[] {
                   mockery.mock(ManualTracker.class, "ManualTracker1"),
                   mockery.mock(ManualTracker.class, "ManualTracker2"),
                   mockery.mock(ManualTracker.class, "ManualTracker3")
               };
    }

    @Override
    protected ManualCompositeStatsTracker createCompositeStatsTracker(final List<ManualTracker> mockTrackers) {
        return new ManualCompositeStatsTracker(mockTrackers);
    }

    @Override
    protected ManualCompositeStatsTracker createCompositeStatsTracker(final ManualTracker[] mockTrackers) {
        return new ManualCompositeStatsTracker(mockTrackers);
    }

    @Test
    public void testAddValue() {

        final double value = 2.0;

        mockery.checking(new Expectations() {{
            for (int i = 0; i < mockTrackers.length; i++) {
                one(mockTrackers[i]).addValue(with(value)); will(returnValue(mockTrackers[i]));
            }
        }});

        ManualCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.addValue(value);
    }

    @Test
    public void testSetValue() {

        final double value = 2.0;

        mockery.checking(new Expectations() {{
            for (int i = 0; i < mockTrackers.length; i++) {
                one(mockTrackers[i]).setValue(with(value)); will(returnValue(mockTrackers[i]));
            }
        }});

        ManualCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.setValue(value);
    }

    @Test
    public void testCommit() {

        mockery.checking(new Expectations() {{
            for (int i = 0; i < mockTrackers.length; i++) {
                one(mockTrackers[i]).commit(); will(returnValue(mockTrackers[i]));
            }
        }});

        ManualCompositeStatsTracker cTracker = createCompositeStatsTracker(mockTrackers);
        cTracker.commit();
    }
}

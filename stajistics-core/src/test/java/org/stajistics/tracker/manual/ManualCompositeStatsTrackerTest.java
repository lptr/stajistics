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
    protected ManualCompositeStatsTracker createCompositeStatsTracker() {
        return new ManualCompositeStatsTracker(mockTrackers);
    }

}

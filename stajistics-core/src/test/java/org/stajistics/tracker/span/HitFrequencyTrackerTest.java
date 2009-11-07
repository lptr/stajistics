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
package org.stajistics.tracker.span;

import org.jmock.Expectations;
import org.junit.Test;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class HitFrequencyTrackerTest extends AbstractSpanStatsTrackerTestCase {

    @Override
    protected SpanTracker createStatsTracker() {
        return new HitFrequencyTracker(mockSession);
    }

    @Test
    public void testStartStopStartStop() {
        final SpanTracker tracker = createStatsTracker();

        mockery.checking(new Expectations() {{
            // start()
            one(mockSession).track(with(tracker), with(any(long.class)));
            one(mockSession).getLastHitStamp(); will(returnValue(0L));

            // stop()
            // Does nothing because we are measuring in between hits, and the
            // first hit of the session cannot be counted.

            // start()
            one(mockSession).track(with(tracker), with(any(long.class)));
            one(mockSession).getLastHitStamp(); will(returnValue(1L));

            // stop()
            one(mockSession).update(with(tracker), with(any(long.class)));
        }});

        tracker.start();
        tracker.stop();
        tracker.start();
        tracker.stop();
    }
}

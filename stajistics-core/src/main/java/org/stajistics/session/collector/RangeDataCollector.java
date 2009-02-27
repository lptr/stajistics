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
package org.stajistics.session.collector;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.Range;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class RangeDataCollector implements DataCollector {

    private final Range[] ranges;
    private final AtomicInteger[] hits;
    private final boolean exclusiveRangeEnd;

    private final boolean hasOverlap;

    public RangeDataCollector(final List<Range> rangeList,
                              final boolean exclusiveRangeEnd) {

        this.ranges = rangeList.toArray(new Range[rangeList.size()]);
        this.exclusiveRangeEnd = exclusiveRangeEnd;

        hits = new AtomicInteger[this.ranges.length];
        for (int i = 0; i < hits.length; i++) {
            hits[i] = new AtomicInteger(0);
        }

        boolean hasOverlap = false;

        for (int i = 0; i < ranges.length; i++) {
            for (int j = 0; j < ranges.length; j++) {
                if (i == j) {
                    continue;
                }

                if (ranges[i].overlaps(ranges[j], exclusiveRangeEnd)) {
                    hasOverlap = true;
                    break;
                }
            }
        }

        this.hasOverlap = hasOverlap;
    }

    @Override
    public void update(final StatsSession session,
                       final StatsTracker tracker, 
                       final long now) {
        final double value = tracker.getValue();

        final int rangeCount = ranges.length;
        Range range;

        for (int i = 0; i < rangeCount; i++) {
            range = ranges[i];
            if (range.contains(value, exclusiveRangeEnd)) {
                hits[i].incrementAndGet();

                if (!hasOverlap) {
                    break;
                }
            }
        }
    }

    @Override
    public void getAttributes(final StatsSession session,
                              final Map<String, Object> attributes) {
        final int rangeCount = ranges.length;
        Range range;
        for (int i = 0; i < rangeCount; i++) {
            range = ranges[i];
            attributes.put(range.getName(), hits[i].get());
        }
    }

    @Override
    public void clear() {
        final int rangeCount = ranges.length;
        for (int i = 0; i < rangeCount; i++) {
            hits[i].set(0);
        }
    }

}

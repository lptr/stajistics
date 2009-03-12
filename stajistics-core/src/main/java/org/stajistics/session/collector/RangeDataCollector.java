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
import java.util.concurrent.atomic.AtomicInteger;

import org.stajistics.session.StatsSession;
import org.stajistics.session.data.MutableDataSet;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.Range;
import org.stajistics.util.RangeList;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class RangeDataCollector implements DataCollector {

    private final RangeList rangeList;
    private final AtomicInteger[] hits;

    public RangeDataCollector(final RangeList rangeList) {
        if (rangeList == null) {
            throw new NullPointerException("rangeList");
        }

        this.rangeList = rangeList;

        int size = rangeList.size();
        hits = new AtomicInteger[size];
        for (int i = 0; i < size; i++) {
            hits[i] = new AtomicInteger(0);
        }
    }


    @Override
    public void update(final StatsSession session,
                       final StatsTracker tracker, 
                       final long now) {
        final double value = tracker.getValue();
        final boolean hasOverlap = rangeList.hasOverlap();

        int i = -1;
        do {
            i = rangeList.indexOfRangeContaining(value, i + 1);
            if (i != -1) {
                hits[i].incrementAndGet();
            }
        } while (i != -1 && hasOverlap);
    }

    @Override
    public void getData(final StatsSession session, final MutableDataSet dataSet) {
        List<Range> ranges = rangeList.getRanges();
        final int rangeCount = ranges.size();
        for (int i = 0; i < rangeCount; i++) {
            dataSet.setField(ranges.get(i).getName(), hits[i].get());
        }
    }

    @Override
    public void clear() {
        final int rangeCount = rangeList.size();
        for (int i = 0; i < rangeCount; i++) {
            hits[i].set(0);
        }
    }

}

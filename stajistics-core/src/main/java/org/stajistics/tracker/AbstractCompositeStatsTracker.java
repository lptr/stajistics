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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistic Project
 */
public abstract class AbstractCompositeStatsTracker<T extends StatsTracker>
    implements CompositeStatsTracker<T>, StatsTracker {

    protected final T[] trackers;

    public AbstractCompositeStatsTracker(final T... trackers) {
        if (trackers.length == 0) {
            throw new IllegalArgumentException("Must provide at least one tracker");
        }

        this.trackers = trackers;
    }

    @SuppressWarnings("unchecked")
    public AbstractCompositeStatsTracker(final List<T> trackers) {
        this(trackers.toArray((T[])new StatsTracker[trackers.size()]));
    }

    public List<T> getTrackers() {
        return Collections.unmodifiableList(Arrays.asList(trackers));
    }

    @Override
    public StatsKey getKey() {
        return trackers[0].getKey();
    }

    @Override
    public StatsSession getSession() {
        return trackers[0].getSession();
    }

    @Override
    public double getValue() {
        return trackers[0].getValue();
    }

    @Override
    public StatsTracker reset() {
        int len = trackers.length;
        for (int i = 0; i < len; i++) {
            trackers[i].reset();
        }

        return this;
    }

    @Override
    public String toString() {
        final int trackerCount = trackers.length;

        StringBuilder buf = new StringBuilder(32 + (64 * trackerCount));

        buf.append(getClass().getSimpleName());
        buf.append('[');

        for (int i = 0; i < trackerCount; i++) {
            if (i > 0) {
                buf.append(',');
            }

            buf.append(trackers[i]);
        }

        buf.append(']');

        return buf.toString();
    }
}

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

import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistic Project
 */
public class CompositeStatsTracker implements StatsTracker {

    private final StatsTracker[] trackers;

    public CompositeStatsTracker(final StatsTracker... trackers) {
        if (trackers.length == 0) {
            throw new IllegalArgumentException("Must provide at least one tracker");
        }

        this.trackers = trackers;
    }

    public CompositeStatsTracker(final List<StatsTracker> trackers) {
        this(trackers.toArray(new StatsTracker[trackers.size()]));
    }

    public List<StatsTracker> getTrackers() {
        return Collections.unmodifiableList(Arrays.asList(trackers));
    }

    @Override
    public StatsTracker commit() {
        for (int i = 0; i < trackers.length; i++) {
            trackers[i].commit();
        }

        return this;
    }

    @Override
    public StatsSession getSession() {
        return trackers[0].getSession();
    }

    @Override
    public long getTimeStamp() {
        return trackers[0].getTimeStamp();
    }

    @Override
    public double getValue() {
        return trackers[0].getValue();
    }

    @Override
    public boolean isTracking() {
        for (int i = 0; i < trackers.length; i++) {
            if (trackers[i].isTracking()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public StatsTracker track() {
        for (int i = 0; i < trackers.length; i++) {
            trackers[i].track();
        }

        return this;
    }

    @Override
    public StatsTracker reset() {
        for (int i = 0; i < trackers.length; i++) {
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

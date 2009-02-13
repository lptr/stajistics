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
package org.stajistics.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.stajistics.StatsKey;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ImmutableStatsSession implements StatsSession {

    private static final long serialVersionUID = -1491982324221671738L;

    private final StatsKey key;

    private final long hits;
    private final long firstHitStamp;
    private final long lastHitStamp;
    private final long commits;
    private final double first;
    private final double last;
    private final double min;
    private final double max;

    private final Map<String,Object> attrs;

    public ImmutableStatsSession(final StatsSession copyFrom) {
        this.key = copyFrom.getKey();

        this.hits = copyFrom.getHits();
        this.firstHitStamp = copyFrom.getFirstHitStamp();
        this.lastHitStamp = copyFrom.getLastHitStamp();
        this.commits = copyFrom.getCommits();
        this.first = copyFrom.getFirst();
        this.last = copyFrom.getLast();
        this.min = copyFrom.getMin();
        this.max = copyFrom.getMax();

        this.attrs = Collections.unmodifiableMap(new HashMap<String,Object>(copyFrom.getAttributes()));
    }

    @Override
    public Object getAttribute(String name) {
        return attrs.get(name);
    }

    @Override
    public Map<String,Object> getAttributes() {
        return attrs;
    }

    @Override
    public double getFirst() {
        return first;
    }

    @Override
    public long getFirstHitStamp() {
        return firstHitStamp;
    }

    @Override
    public long getHits() {
        return hits;
    }

    @Override
    public long getCommits() {
        return commits;
    }

    @Override
    public StatsKey getKey() {
        return key;
    }

    @Override
    public double getLast() {
        return last;
    }

    @Override
    public long getLastHitStamp() {
        return lastHitStamp;
    }

    @Override
    public double getMax() {
        return max;
    }

    @Override
    public double getMin() {
        return min;
    }

    @Override
    public void open(StatsTracker tracker, long now) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(StatsTracker tracker, long now) {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatsSession snapshot() {
        return this;
    }

}

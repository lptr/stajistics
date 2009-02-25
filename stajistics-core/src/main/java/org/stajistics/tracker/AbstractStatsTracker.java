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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.session.StatsSession;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsTracker implements StatsTracker {

    protected static final Logger logger = LoggerFactory.getLogger("org.stajistics.tracker");

    protected StatsSession statsSession;

    protected boolean tracking = false;
    protected long timeStamp = 0;
    protected double value = 0;

    protected AbstractStatsTracker() {}

    protected AbstractStatsTracker(final StatsSession statsSession) {
        if (statsSession == null) {
            throw new NullPointerException("statsSession");
        }

        this.statsSession = statsSession;
    }

    @Override
    public boolean isTracking() {
        return tracking;
    }

    @Override
    public StatsTracker track() {

        if (tracking) {
            throw new IllegalStateException("Already tracking");
        }

        tracking = true;

        timeStamp = System.currentTimeMillis();

        trackImpl(timeStamp);

        return this;
    }

    protected void trackImpl(final long now) {
        statsSession.open(this, now);
    }

    @Override
    public StatsTracker commit() {

        if (!tracking) {
            throw new IllegalStateException("Not tracking");
        }

        tracking = false;

        commitImpl(System.currentTimeMillis());

        return this;
    }

    protected void commitImpl(final long now) {
        statsSession.update(this, now);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public StatsTracker reset() {
        tracking = false;
        timeStamp = 0;
        value = 0;
        return this;
    }

    @Override
    public StatsSession getSession() {
        return statsSession;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);

        buf.append(getClass().getSimpleName());
        buf.append("[timeStamp=");
        buf.append(timeStamp);
        buf.append(",value=");
        buf.append(value);
        buf.append(",statsSession=");
        buf.append(statsSession);
        buf.append(']');

        return buf.toString();
    }

}

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

    private static final long serialVersionUID = 7869543246230561742L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractStatsTracker.class);

    protected StatsSession session;

    protected boolean tracking = false;
    protected long timeStamp = 0;
    protected double value = 0;

    protected AbstractStatsTracker() {}

    protected AbstractStatsTracker(final StatsSession session) {
        if (session == null) {
            throw new NullPointerException("session");
        }

        this.session = session;
    }

    @Override
    public boolean isTracking() {
        return tracking;
    }

    @Override
    public final StatsTracker track() {

        if (tracking) {
            logger.warn("track() called when already tracking: {}", this);

            return this;
        }

        tracking = true;

        timeStamp = System.currentTimeMillis();

        trackImpl(timeStamp);

        return this;
    }

    protected void trackImpl(final long now) {
        session.track(this, now);
    }

    @Override
    public final StatsTracker commit() {

        if (!tracking) {
            logger.warn("commit() called when not tracking: {}", this);

            return this;
        }

        tracking = false;

        commitImpl(-1);

        return this;
    }

    @Override
    public StatsTracker incident() {

        if (tracking) {
            logger.warn("incident() called when already tracking: {}", this);

            return this;
        }

        final long now = System.currentTimeMillis();
        trackImpl(now);
        commitImpl(now);

        return this;
    }

    protected void commitImpl(final long now) {
        session.update(this, now);
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
        return session;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);

        buf.append(getClass().getSimpleName());
        buf.append("[timeStamp=");
        buf.append(timeStamp);
        buf.append(",value=");
        buf.append(value);
        buf.append(",session=");
        buf.append(session);
        buf.append(']');

        return buf.toString();
    }

}

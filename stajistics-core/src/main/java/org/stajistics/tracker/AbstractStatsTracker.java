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

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;

/**
 * A convenience base implementation of {@link StatsTracker}.
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsTracker implements StatsTracker {

    private static final long serialVersionUID = 7869543246230561742L;

    protected StatsSession session;

    protected double value = 0;

    public AbstractStatsTracker(final StatsSession session) {
        if (session == null) {
            throw new NullPointerException("session");
        }

        this.session = session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsTracker reset() {
        value = 0;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKey getKey() {
        return session.getKey();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsSession getSession() {
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);

        buf.append(getClass().getSimpleName());
        buf.append("[value=");
        buf.append(value);
        buf.append(",session=");
        buf.append(session);
        buf.append(']');

        return buf.toString();
    }

}

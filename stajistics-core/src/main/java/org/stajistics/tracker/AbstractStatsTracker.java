/* Copyright 2009 - 2010 The Stajistics Project
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
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSession;
import org.stajistics.util.Misc;

/**
 * A convenience base implementation of {@link StatsTracker}.
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsTracker implements StatsTracker {

    private static final Logger logger = LoggerFactory.getLogger(AbstractStatsTracker.class);

    protected final StatsSession session;

    protected double value = 0;

    public AbstractStatsTracker(final StatsSession session) {
        if (session == null) {
            throw new NullPointerException("session");
        }

        this.session = session;
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public StatsTracker reset() {
        value = 0;
        return this;
    }

    @Override
    public StatsKey getKey() {
        return session.getKey();
    }

    @Override
    public StatsSession getSession() {
        return session;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);

        buf.append(getClass().getSimpleName());
        buf.append("[value=");
        buf.append(value);
        buf.append(",session=");
        try {
            buf.append(session);
        } catch (Exception e) {
            buf.append(e.toString());

            Misc.logSwallowedException(logger, 
                                       e, 
                                       "Caught Exception in toString()");
        }
        buf.append(']');

        return buf.toString();
    }

}

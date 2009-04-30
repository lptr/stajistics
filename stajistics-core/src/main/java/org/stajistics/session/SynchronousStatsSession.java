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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.stajistics.StatsKey;
import org.stajistics.event.StatsEventManager;
import org.stajistics.session.data.DataSet;
import org.stajistics.tracker.StatsTracker;

/**
 * An implementation of {@link StatsSession} that reads ands writes data fields atomically
 * and locks calls to {@link #track(StatsTracker, long)}, {@link #update(StatsTracker, long)}, 
 * {@link #collectData()}, and {@link #clear()}. This allows guaranteed consistency between the 
 * values contained in the result of {@link #collectData()}, however, at the cost of increased 
 * statistics collection overhead and reduced scalability.
 *
 * The other methods that are not mentioned above are not locked, but since this class 
 * extends {@link ConcurrentStatsSession}, thread safety is retained.
 *
 * @author The Stajistics Project
 */
public class SynchronousStatsSession extends ConcurrentStatsSession {

    private static final long serialVersionUID = -32360770003453383L;

    protected final Lock lock = new ReentrantLock();

    public SynchronousStatsSession(final StatsKey key,
                                   final StatsEventManager eventManager) {
        super(key, eventManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void track(final StatsTracker tracker, final long now) {
        lock.lock();
        try {
            super.track(tracker, now);

        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final StatsTracker tracker, final long now) {

        StatsSession snapshot;

        lock.lock();
        try {
            super.update(tracker, now);
            snapshot = new ImmutableStatsSession(this);

        } finally {
            lock.unlock();
        }

        // Fire the event outside of the lock
        super.fireUpdateEvent(snapshot, tracker);
    }

    /* This no-op override prevents the event firing from within the locked scope */
    @Override
    protected void fireUpdateEvent(final StatsSession session,
                                   final StatsTracker tracker) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSet collectData() {
        lock.lock();
        try {
            return super.collectData();

        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        lock.lock();
        try {
            super.clear();

        } finally {
            lock.unlock();
        }
    }
}

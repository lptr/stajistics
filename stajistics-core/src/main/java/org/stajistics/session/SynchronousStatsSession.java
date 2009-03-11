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
import org.stajistics.session.data.DataSet;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class SynchronousStatsSession extends ConcurrentStatsSession {

    private static final long serialVersionUID = -32360770003453383L;

    protected final Lock lock = new ReentrantLock();

    public SynchronousStatsSession(final StatsKey key) {
        super(key);
    }

    @Override
    public void update(final StatsTracker tracker, final long now) {

        StatsSession snapshot;

        lock.lock();
        try {
            super.update(tracker, now);
            snapshot = snapshot();

        } finally {
            lock.unlock();
        }

        super.fireUpdateEvent(snapshot, tracker);
    }

    @Override
    protected void fireUpdateEvent(final StatsSession session,
                                   final StatsTracker tracker) {}

    @Override
    public StatsSession snapshot() {
        lock.lock();
        try {
            return super.snapshot();

        } finally {
            lock.unlock();
        }
    }

    @Override
    public DataSet dataSet() {
        lock.lock();
        try {
            return super.dataSet();

        } finally {
            lock.unlock();
        }
    }

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

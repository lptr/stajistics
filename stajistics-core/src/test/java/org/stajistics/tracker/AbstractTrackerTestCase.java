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

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.data.DataSet;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.session.recorder.DataRecorder;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public abstract class AbstractTrackerTestCase<T extends Tracker> extends AbstractStajisticsTestCase {

    protected StatsKey mockKey;
    protected StatsSession mockSession;

    protected final T createStatsTracker() {
        return createStatsTracker(mockSession);
    }

    protected abstract T createStatsTracker(final StatsSession session);

    @Before
    public void setUp() throws Exception {
        mockSession = mockery.mock(StatsSession.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
        }});
    }

    @Test
    public void testCreate() {
        final Tracker tracker = createStatsTracker();

        assertEquals(mockSession, tracker.getSession());
        assertEquals(0, tracker.getValue(), 0);
    }

    @Test
    public void testGetKey() {
        final Tracker tracker = createStatsTracker();

        assertEquals(mockSession.getKey(), tracker.getKey());
    }

    @Test
    public void testInitialReset() {
        final Tracker tracker = createStatsTracker();

        assertEquals(0, tracker.getValue(), 0);
        tracker.reset();
        assertEquals(0, tracker.getValue(), 0);
    }

    @Test
    public void testToStringIsOverridden() {
        assertTrue(createStatsTracker().toString().indexOf("Tracker@") < 0);
    }

    @Test
    public void testConstantFactoryField() throws Exception {

        final Tracker tracker = createStatsTracker();

        Class<?> trackerClass = tracker.getClass();
        Field field = trackerClass.getDeclaredField("FACTORY");

        Object fieldValue = field.get(0);

        assertNotNull(fieldValue);
        assertTrue(fieldValue instanceof TrackerFactory<?>);

        final StatsKey mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        final StatsSessionManager mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockery.checking(new Expectations() {{
            allowing(mockSessionManager).getOrCreateSession(mockKey);
            will(returnValue(mockSession));
        }});

        TrackerFactory<?> factory = (TrackerFactory<?>)fieldValue;
        Tracker tracker2 = factory.createTracker(mockKey, mockSessionManager);

        assertEquals(trackerClass, tracker2.getClass());
    }

    @Test
    public void testToStringEatsSessionException() {
        final Tracker tracker = createStatsTracker(new NastySession());

        tracker.toString();
    }

    /* NESTED CLASSES */

    public static final class NastySession implements StatsSession {

        @Override
        public void clear() {
            throw new RuntimeException();
        }

        @Override
        public DataSet collectData() {
            throw new RuntimeException();
        }

        @Override
        public DataSet drainData() {
            throw new RuntimeException();
        }

        @Override
        public long getCommits() {
            throw new RuntimeException();
        }

        @Override
        public List<DataRecorder> getDataRecorders() {
            throw new RuntimeException();
        }

        @Override
        public Object getField(String name) {
            throw new RuntimeException();
        }

        @Override
        public double getFirst() {
            throw new RuntimeException();
        }

        @Override
        public long getFirstHitStamp() {
            throw new RuntimeException();
        }

        @Override
        public long getHits() {
            throw new RuntimeException();
        }

        @Override
        public StatsKey getKey() {
            throw new RuntimeException();
        }

        @Override
        public double getLast() {
            throw new RuntimeException();
        }

        @Override
        public long getLastHitStamp() {
            throw new RuntimeException();
        }

        @Override
        public double getMax() {
            throw new RuntimeException();
        }

        @Override
        public double getMin() {
            throw new RuntimeException();
        }

        @Override
        public double getSum() {
            throw new RuntimeException();
        }

        @Override
        public void restore(DataSet dataSet) {
            throw new RuntimeException();
        }

        @Override
        public void track(Tracker tracker, long now) {
            throw new RuntimeException();
        }

        @Override
        public void update(Tracker tracker, long now) {
            throw new RuntimeException();
        }

        @Override
        public String toString() {
            throw new RuntimeException();
        }
    }
}

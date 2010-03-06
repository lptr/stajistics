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
package org.stajistics.session;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.data.DataSet;
import org.stajistics.data.DefaultDataSet;
import org.stajistics.data.DataSet.Field;
import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.StatsTracker;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public abstract class AbstractStatsSessionTestCase {

    protected static final double DELTA = 0.0000000000001;

    protected Mockery mockery;
    protected StatsKey mockKey;
    protected StatsTracker mockTracker;
    protected StatsEventManager mockEventManager;

    protected StatsSession session;

    @Before
    public void setUp() {
        mockery = new Mockery();

        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        mockTracker = mockery.mock(StatsTracker.class);
        mockEventManager = mockery.mock(StatsEventManager.class);

        initMocks();

        session = createStatsSession();
    }

    protected void initMocks() {}

    protected abstract StatsSession createStatsSession(DataRecorder... dataRecorders);

    @Test
    public void testConstructWithNullKey() {
        try {
            new ConcurrentStatsSession(null, mockEventManager, (DataRecorder[])null);

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new ConcurrentStatsSession(mockKey, null, (DataRecorder[])null);

        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testInitialData() {
        assertEquals(DataSet.Field.Default.HITS.longValue(), session.getHits());
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), session.getFirstHitStamp());
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), DELTA);
    }

    @Test
    public void testInitialCollectData() {
        DataSet data = session.collectData();
        assertEquals(DataSet.Field.Default.HITS.longValue(), 
                     (long) data.getField(Field.HITS, Long.class));
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), 
                     (long) data.getField(Field.FIRST_HIT_STAMP, Long.class));
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), 
                     (long) data.getField(Field.LAST_HIT_STAMP, Long.class));
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), 
                     (long) data.getField(Field.COMMITS, Long.class));
        assertEquals(DataSet.Field.Default.FIRST, data.getField(Field.FIRST, Double.class), DELTA);
        assertEquals(DataSet.Field.Default.MIN, data.getField(Field.MIN, Double.class), DELTA);
        assertEquals(DataSet.Field.Default.MAX, data.getField(Field.MAX, Double.class), DELTA);
        assertEquals(DataSet.Field.Default.LAST, data.getField(Field.LAST, Double.class), DELTA);
        assertEquals(DataSet.Field.Default.SUM, data.getField(Field.SUM, Double.class), DELTA);
    }

    @Test
    public void testInitialDataEqualsClearedData() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        long hits = session.getHits();
        long firstHitStamp = session.getFirstHitStamp();
        long lastHitStamp  = session.getLastHitStamp();
        long commits = session.getCommits();
        double first = session.getFirst();
        double min = session.getMin();
        double max = session.getMax();
        double last = session.getLast();
        double sum = session.getSum();

        session.clear();

        assertEquals(hits, session.getHits());
        assertEquals(firstHitStamp, session.getFirstHitStamp());
        assertEquals(lastHitStamp, session.getLastHitStamp());
        assertEquals(commits, session.getCommits());
        assertEquals(first, session.getFirst(), DELTA);
        assertEquals(min, session.getMin(), DELTA);
        assertEquals(max, session.getMax(), DELTA);
        assertEquals(last, session.getLast(), DELTA);
        assertEquals(sum, session.getSum(), DELTA);
    }

    @Test
    public void testClearFiresSessionClearedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(StatsEventType.SESSION_CLEARED),
                                            with(mockKey),
                                            with(session));
        }});

        session.clear();
    }

    @Test
    public void testTrack() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        final long firstNow = System.currentTimeMillis();

        session.track(mockTracker, firstNow);

        assertEquals(1, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(firstNow, session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), DELTA);

        final long secondNow = System.currentTimeMillis();

        session.track(mockTracker, secondNow);

        assertEquals(2, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(secondNow, session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), DELTA);
    }

    @Test
    public void testTrackFiresTrackerTrackingEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(StatsEventType.TRACKER_TRACKING),
                                            with(mockKey),
                                            with(mockTracker));
        }});

        session.track(mockTracker, 0L);
    }

    @Test
    public void testUpdate() {
        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(1.0));
            ignoring(mockEventManager);
        }});

        long now = System.currentTimeMillis();

        session.update(mockTracker, now);

        assertEquals(1, session.getCommits());
        assertEquals(1, session.getFirst(), DELTA);
        assertEquals(1, session.getMin(), DELTA);
        assertEquals(1, session.getMax(), DELTA);
        assertEquals(1, session.getLast(), DELTA);
        assertEquals(1, session.getSum(), DELTA);

        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(2.0));
        }});

        now = System.currentTimeMillis();

        session.update(mockTracker, now);

        assertEquals(2, session.getCommits());
        assertEquals(1, session.getFirst(), DELTA);
        assertEquals(1, session.getMin(), DELTA);
        assertEquals(2, session.getMax(), DELTA);
        assertEquals(2, session.getLast(), DELTA);
        assertEquals(3, session.getSum(), DELTA);
    }

    @Test
    public void testUpdateFiresTrackerCommittedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(StatsEventType.TRACKER_COMMITTED),
                                            with(mockKey),
                                            with(mockTracker));
            ignoring(mockTracker).getValue(); will(returnValue(1.0));
        }});

        session.update(mockTracker, 0L);
    }

    @Test
    public void testTrackAndUpdate() {
        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(2.0));
            ignoring(mockEventManager);
        }});

        final long firstNow = System.currentTimeMillis();

        session.track(mockTracker, firstNow);
        session.update(mockTracker, firstNow);

        assertEquals(1, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(firstNow, session.getLastHitStamp());
        assertEquals(1, session.getCommits());
        assertEquals(2, session.getFirst(), DELTA);
        assertEquals(2, session.getMin(), DELTA);
        assertEquals(2, session.getMax(), DELTA);
        assertEquals(2, session.getLast(), DELTA);
        assertEquals(2, session.getSum(), DELTA);

        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(4.0));
        }});

        final long secondNow = System.currentTimeMillis();

        session.track(mockTracker, secondNow);
        session.update(mockTracker, secondNow);

        assertEquals(2, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(secondNow, session.getLastHitStamp());
        assertEquals(2, session.getCommits());
        assertEquals(2, session.getFirst(), DELTA);
        assertEquals(2, session.getMin(), DELTA);
        assertEquals(4, session.getMax(), DELTA);
        assertEquals(4, session.getLast(), DELTA);
        assertEquals(6, session.getSum(), DELTA);
    }

    @Test
    public void testRestore() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        DataSet dataSet = session.collectData();

        StatsSession anotherSession = createStatsSession(null);

        anotherSession.restore(dataSet);

        assertEquals(session.getHits(), anotherSession.getHits());
        assertEquals(session.getFirstHitStamp(), anotherSession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), anotherSession.getLastHitStamp());
        assertEquals(session.getCommits(), anotherSession.getCommits());
        assertEquals(session.getFirst(), anotherSession.getFirst(), DELTA);
        assertEquals(session.getMin(), anotherSession.getMin(), DELTA);
        assertEquals(session.getMax(), anotherSession.getMax(), DELTA);
        assertEquals(session.getLast(), anotherSession.getLast(), DELTA);
        assertEquals(session.getSum(), anotherSession.getSum(), DELTA);
    }

    @Test
    public void testRestoreWithUnchangedDataSet() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        DataSet dataSet = session.collectData();

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        StatsSession emptySession = createStatsSession((DataRecorder[])null);

        session.restore(dataSet);

        assertEquals(session.getHits(), emptySession.getHits());
        assertEquals(session.getFirstHitStamp(), emptySession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), emptySession.getLastHitStamp());
        assertEquals(session.getCommits(), emptySession.getCommits());
        assertEquals(session.getFirst(), emptySession.getFirst(), DELTA);
        assertEquals(session.getMin(), emptySession.getMin(), DELTA);
        assertEquals(session.getMax(), emptySession.getMax(), DELTA);
        assertEquals(session.getLast(), emptySession.getLast(), DELTA);
        assertEquals(session.getSum(), emptySession.getSum(), DELTA);
    }

    @Test
    public void testRestoreWithEmptyDataSet() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.restore(new DefaultDataSet());

        // Should equal initial/cleared state
        assertEquals(DataSet.Field.Default.HITS.longValue(), session.getHits());
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), session.getFirstHitStamp());
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), DELTA);
    }

    @Test
    public void testRestoreWithNullDataSet() {
        try {
            session.restore(null);
        } catch (NullPointerException npe) {
            assertEquals("dataSet", npe.getMessage());
        }
    }

    @Test
    public void testRestoreWithoutHits() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        DataSet dataSet = session.collectData();

        // Remove hits
        dataSet.removeField(DataSet.Field.HITS);

        StatsSession anotherSession = createStatsSession(null);

        anotherSession.restore(dataSet);

        assertEquals(DataSet.Field.Default.HITS.longValue(), anotherSession.getHits());
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), anotherSession.getFirstHitStamp());
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), anotherSession.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), anotherSession.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, anotherSession.getFirst(), DELTA);
        assertEquals(DataSet.Field.Default.MIN, anotherSession.getMin(), DELTA);
        assertEquals(DataSet.Field.Default.MAX, anotherSession.getMax(), DELTA);
        assertEquals(DataSet.Field.Default.LAST, anotherSession.getLast(), DELTA);
        assertEquals(DataSet.Field.Default.SUM, anotherSession.getSum(), DELTA);
    }

    @Test
    public void testRestoreWithoutCommits() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        DataSet dataSet = session.collectData();

        // Remove commits
        dataSet.removeField(DataSet.Field.COMMITS);

        StatsSession anotherSession = createStatsSession(null);

        anotherSession.restore(dataSet);

        assertEquals(session.getHits(), anotherSession.getHits());
        assertEquals(session.getFirstHitStamp(), anotherSession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), anotherSession.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), anotherSession.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, anotherSession.getFirst(), DELTA);
        assertEquals(DataSet.Field.Default.MIN, anotherSession.getMin(), DELTA);
        assertEquals(DataSet.Field.Default.MAX, anotherSession.getMax(), DELTA);
        assertEquals(DataSet.Field.Default.LAST, anotherSession.getLast(), DELTA);
        assertEquals(DataSet.Field.Default.SUM, anotherSession.getSum(), DELTA);
    }

    @Test
    public void testClearEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.clear();
    }

    @Test
    public void testCollectDataEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.collectData();
    }

    @Test
    public void testDrainDataEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.drainData();
    }

    @Test
    public void testGetFieldEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());
        session.getField(DataSet.Field.HITS);
    }

    @Test
    public void testRestoreEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.restore(new DefaultDataSet());
    }

    @Test
    public void testTrackEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.track(mockTracker, -1L);
    }

    @Test
    public void testUpdateEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            one(mockTracker).getValue(); will(returnValue(1.0));
        }});

        session.update(mockTracker, -1L);
    }

    /* NESTED CLASSES */

    public static class NastyDataRecorder implements DataRecorder {

        @Override
        public void clear() {
            throw new RuntimeException();
        }

        @Override
        public void collectData(StatsSession session, DataSet dataSet) {
            throw new RuntimeException();
        }

        @Override
        public Object getField(StatsSession session, String name) {
            throw new RuntimeException();
        }

        @Override
        public Set<String> getSupportedFieldNames() {
            throw new RuntimeException();
        }

        @Override
        public void restore(DataSet dataSet) {
            throw new RuntimeException();
        }

        @Override
        public void update(StatsSession session, StatsTracker tracker, long now) {
            throw new RuntimeException();
        }

    };
}

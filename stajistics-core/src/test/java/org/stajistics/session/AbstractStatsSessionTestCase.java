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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.data.DataSet;
import org.stajistics.data.DataSet.Field;
import org.stajistics.data.DefaultDataSet;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.tracker.Tracker;
import org.stajistics.util.Decorator;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public abstract class AbstractStatsSessionTestCase extends AbstractStajisticsTestCase {

    protected StatsKey mockKey;
    protected Tracker mockTracker;
    protected EventManager mockEventManager;

    protected StatsSession session;

    @Before
    public void baseSetUp() {
        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        mockTracker = mockery.mock(Tracker.class);
        mockEventManager = mockery.mock(EventManager.class);

        session = createStatsSession();
    }

    protected abstract StatsSession createStatsSession(DataRecorder... dataRecorders);

    protected void assertInitialState(final StatsSession session) {
        assertEquals(DataSet.Field.Default.HITS.longValue(), session.getHits());
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), session.getFirstHitStamp());
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testConstructWithNullKey() {
        try {
            new ConcurrentSession(null, mockEventManager, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullEventManager() {
        try {
            new ConcurrentSession(mockKey, null, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testInitialData() {
        assertInitialState(session);
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
        assertEquals(DataSet.Field.Default.FIRST, data.getField(Field.FIRST, Double.class), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, data.getField(Field.MIN, Double.class), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, data.getField(Field.MAX, Double.class), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, data.getField(Field.LAST, Double.class), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, data.getField(Field.SUM, Double.class), TestUtil.DELTA);
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
        assertEquals(first, session.getFirst(), TestUtil.DELTA);
        assertEquals(min, session.getMin(), TestUtil.DELTA);
        assertEquals(max, session.getMax(), TestUtil.DELTA);
        assertEquals(last, session.getLast(), TestUtil.DELTA);
        assertEquals(sum, session.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testCreateDataSetIsNotNull() {
        AbstractStatsSession abstractSession = (AbstractStatsSession) session;
        assertNotNull(abstractSession.createDataSet(false));
    }

    @Test
    public void testCreateDataSetPassesAlongIsSessionDrained() {
        AbstractStatsSession abstractSession = (AbstractStatsSession) session;
        DataSet dataSet = abstractSession.createDataSet(true);
        assertTrue(dataSet.isSessionDrained());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetDataRecorders() {
        final DataRecorder mockDataRecorder = mockery.mock(DataRecorder.class);

        session = createStatsSession(mockDataRecorder);

        assertNotNull(session.getDataRecorders());
        assertFalse(session.getDataRecorders().isEmpty());
        assertEquals(1, session.getDataRecorders().size());

        DataRecorder dr = session.getDataRecorders().get(0);
        while (dr instanceof Decorator<?>) {
            dr = ((Decorator<DataRecorder>)dr).delegate();
        }

        assertEquals(mockDataRecorder, dr);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetDataRecordersReturnsImmutableList() {
        session.getDataRecorders()
               .add(mockery.mock(DataRecorder.class));
    }

    @Test
    public void testGetFieldEqualsSessionGetters() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        assertEquals(session.getHits(), session.getField(DataSet.Field.HITS));
        assertEquals(session.getFirstHitStamp(), session.getField(DataSet.Field.FIRST_HIT_STAMP));
        assertEquals(session.getLastHitStamp(), session.getField(DataSet.Field.LAST_HIT_STAMP));
        assertEquals(session.getCommits(), session.getField(DataSet.Field.COMMITS));
        assertEquals(session.getFirst(), session.getField(DataSet.Field.FIRST));
        assertEquals(session.getLast(), session.getField(DataSet.Field.LAST));
        assertEquals(session.getMin(), session.getField(DataSet.Field.MIN));
        assertEquals(session.getMax(), session.getField(DataSet.Field.MAX));
        assertEquals(session.getSum(), session.getField(DataSet.Field.SUM));
    }

    @Test
    public void testClear() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);

            allowing(mockTracker).getValue();
            will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        session.clear();

        assertInitialState(session);
    }

    @Test
    public void testClearFiresSessionClearedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.SESSION_CLEARED),
                                            with(mockKey),
                                            with(session));
        }});

        session.clear();
    }

    @Test
    public void testDrainDataFiresSessionClearedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.SESSION_CLEARED),
                                            with(mockKey),
                                            with(session));
        }});

        session.drainData();
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
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), TestUtil.DELTA);

        final long secondNow = System.currentTimeMillis();

        session.track(mockTracker, secondNow);

        assertEquals(2, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(secondNow, session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testTrackFiresTrackerTrackingEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.TRACKER_TRACKING),
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
        assertEquals(1, session.getFirst(), TestUtil.DELTA);
        assertEquals(1, session.getMin(), TestUtil.DELTA);
        assertEquals(1, session.getMax(), TestUtil.DELTA);
        assertEquals(1, session.getLast(), TestUtil.DELTA);
        assertEquals(1, session.getSum(), TestUtil.DELTA);

        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(2.0));
        }});

        now = System.currentTimeMillis();

        session.update(mockTracker, now);

        assertEquals(2, session.getCommits());
        assertEquals(1, session.getFirst(), TestUtil.DELTA);
        assertEquals(1, session.getMin(), TestUtil.DELTA);
        assertEquals(2, session.getMax(), TestUtil.DELTA);
        assertEquals(2, session.getLast(), TestUtil.DELTA);
        assertEquals(3, session.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testUpdateFiresTrackerCommittedEvent() {

        mockery.checking(new Expectations() {{
            one(mockEventManager).fireEvent(with(EventType.TRACKER_COMMITTED),
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
        assertEquals(2, session.getFirst(), TestUtil.DELTA);
        assertEquals(2, session.getMin(), TestUtil.DELTA);
        assertEquals(2, session.getMax(), TestUtil.DELTA);
        assertEquals(2, session.getLast(), TestUtil.DELTA);
        assertEquals(2, session.getSum(), TestUtil.DELTA);

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
        assertEquals(2, session.getFirst(), TestUtil.DELTA);
        assertEquals(2, session.getMin(), TestUtil.DELTA);
        assertEquals(4, session.getMax(), TestUtil.DELTA);
        assertEquals(4, session.getLast(), TestUtil.DELTA);
        assertEquals(6, session.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testCollectData() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            final long now = i + 1;
            session.track(mockTracker, now);
            session.update(mockTracker, now);
        }

        DataSet dataSet = session.collectData();

        assertEquals(10L, dataSet.getField(DataSet.Field.HITS));
        assertEquals(1L, dataSet.getField(DataSet.Field.FIRST_HIT_STAMP));
        assertEquals(10L, dataSet.getField(DataSet.Field.LAST_HIT_STAMP));
        assertEquals(10L, dataSet.getField(DataSet.Field.COMMITS));
        assertEquals(1.0, dataSet.getField(DataSet.Field.FIRST, Double.class), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getField(DataSet.Field.MIN, Double.class), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getField(DataSet.Field.MAX, Double.class), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getField(DataSet.Field.LAST, Double.class), TestUtil.DELTA);
        assertEquals(10.0, dataSet.getField(DataSet.Field.SUM, Double.class), TestUtil.DELTA);
    }

    @Test
    public void testCollectDataEqualsSessionGetters() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        DataSet dataSet = session.collectData();

        assertEquals(session.getHits(), dataSet.getField(DataSet.Field.HITS));
        assertEquals(session.getFirstHitStamp(), dataSet.getField(DataSet.Field.FIRST_HIT_STAMP));
        assertEquals(session.getLastHitStamp(), dataSet.getField(DataSet.Field.LAST_HIT_STAMP));
        assertEquals(session.getCommits(), dataSet.getField(DataSet.Field.COMMITS));
        assertEquals(session.getFirst(), dataSet.getField(DataSet.Field.FIRST, Double.class), TestUtil.DELTA);
        assertEquals(session.getMin(), dataSet.getField(DataSet.Field.MIN, Double.class), TestUtil.DELTA);
        assertEquals(session.getMax(), dataSet.getField(DataSet.Field.MAX, Double.class), TestUtil.DELTA);
        assertEquals(session.getLast(), dataSet.getField(DataSet.Field.LAST, Double.class), TestUtil.DELTA);
        assertEquals(session.getSum(), dataSet.getField(DataSet.Field.SUM, Double.class), TestUtil.DELTA);
    }

    @Test
    public void testDrainData() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            final long now = i + 1;
            session.track(mockTracker, now);
            session.update(mockTracker, now);
        }

        DataSet dataSet = session.drainData();

        assertEquals(10L, dataSet.getField(DataSet.Field.HITS));
        assertEquals(1L, dataSet.getField(DataSet.Field.FIRST_HIT_STAMP));
        assertEquals(10L, dataSet.getField(DataSet.Field.LAST_HIT_STAMP));
        assertEquals(10L, dataSet.getField(DataSet.Field.COMMITS));
        assertEquals(1.0, dataSet.getField(DataSet.Field.FIRST, Double.class), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getField(DataSet.Field.MIN, Double.class), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getField(DataSet.Field.MAX, Double.class), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getField(DataSet.Field.LAST, Double.class), TestUtil.DELTA);
        assertEquals(10.0, dataSet.getField(DataSet.Field.SUM, Double.class), TestUtil.DELTA);

        assertInitialState(session);
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

        StatsSession anotherSession = createStatsSession();

        anotherSession.restore(dataSet);

        assertEquals(session.getHits(), anotherSession.getHits());
        assertEquals(session.getFirstHitStamp(), anotherSession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), anotherSession.getLastHitStamp());
        assertEquals(session.getCommits(), anotherSession.getCommits());
        assertEquals(session.getFirst(), anotherSession.getFirst(), TestUtil.DELTA);
        assertEquals(session.getMin(), anotherSession.getMin(), TestUtil.DELTA);
        assertEquals(session.getMax(), anotherSession.getMax(), TestUtil.DELTA);
        assertEquals(session.getLast(), anotherSession.getLast(), TestUtil.DELTA);
        assertEquals(session.getSum(), anotherSession.getSum(), TestUtil.DELTA);
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

        StatsSession emptySession = createStatsSession();

        session.restore(dataSet);

        assertEquals(session.getHits(), emptySession.getHits());
        assertEquals(session.getFirstHitStamp(), emptySession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), emptySession.getLastHitStamp());
        assertEquals(session.getCommits(), emptySession.getCommits());
        assertEquals(session.getFirst(), emptySession.getFirst(), TestUtil.DELTA);
        assertEquals(session.getMin(), emptySession.getMin(), TestUtil.DELTA);
        assertEquals(session.getMax(), emptySession.getMax(), TestUtil.DELTA);
        assertEquals(session.getLast(), emptySession.getLast(), TestUtil.DELTA);
        assertEquals(session.getSum(), emptySession.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testRestoreWithEmptyDataSet() {

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.restore(new DefaultDataSet(-1L, false));

        // Should equal initial/cleared state
        assertEquals(DataSet.Field.Default.HITS.longValue(), session.getHits());
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), session.getFirstHitStamp());
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), session.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), session.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, session.getFirst(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, session.getMin(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, session.getMax(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, session.getLast(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, session.getSum(), TestUtil.DELTA);
    }

    @Test
    public void testRestoreWithNullDataSet() {
        try {
            session.restore(null);
            fail();
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

        StatsSession anotherSession = createStatsSession((DataRecorder[]) null);

        anotherSession.restore(dataSet);

        assertEquals(DataSet.Field.Default.HITS.longValue(), anotherSession.getHits());
        assertEquals(DataSet.Field.Default.FIRST_HIT_STAMP.longValue(), anotherSession.getFirstHitStamp());
        assertEquals(DataSet.Field.Default.LAST_HIT_STAMP.longValue(), anotherSession.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), anotherSession.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, anotherSession.getFirst(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, anotherSession.getMin(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, anotherSession.getMax(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, anotherSession.getLast(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, anotherSession.getSum(), TestUtil.DELTA);
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

        StatsSession anotherSession = createStatsSession((DataRecorder[]) null);

        anotherSession.restore(dataSet);

        assertEquals(session.getHits(), anotherSession.getHits());
        assertEquals(session.getFirstHitStamp(), anotherSession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), anotherSession.getLastHitStamp());
        assertEquals(DataSet.Field.Default.COMMITS.longValue(), anotherSession.getCommits());
        assertEquals(DataSet.Field.Default.FIRST, anotherSession.getFirst(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MIN, anotherSession.getMin(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.MAX, anotherSession.getMax(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.LAST, anotherSession.getLast(), TestUtil.DELTA);
        assertEquals(DataSet.Field.Default.SUM, anotherSession.getSum(), TestUtil.DELTA);
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

        session.restore(new DefaultDataSet(-1L, false));
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

    @Test
    public void testToString() {
        String strVal = session.toString();

        assertTrue(strVal.startsWith(StatsSession.class.getSimpleName() + "["));
        assertTrue(strVal.indexOf("key=" + session.getKey()) > -1);
        assertTrue(strVal.indexOf("hits=" + session.getHits()) > -1);
        assertTrue(strVal.indexOf("commits=" + session.getCommits()) > -1);
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
        public void update(StatsSession session, Tracker tracker, long now) {
            throw new RuntimeException();
        }

    };
}

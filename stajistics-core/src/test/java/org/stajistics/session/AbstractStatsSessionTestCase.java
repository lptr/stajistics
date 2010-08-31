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

import static org.junit.Assert.*;
import static org.stajistics.data.FieldUtils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;
import org.stajistics.TestUtil;
import org.stajistics.data.DataSet;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.data.FieldSetFactory;
import org.stajistics.data.DataSet.StandardField;
import org.stajistics.data.DataSet.StandardMetaField;
import org.stajistics.data.fast.FastFieldSetFactory;
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
    protected FieldSetFactory mockFieldSetFactory;

    protected StatsSession session;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "test");

        mockTracker = mockery.mock(Tracker.class);
        mockEventManager = mockery.mock(EventManager.class);
        mockFieldSetFactory = FastFieldSetFactory.getInstance();

        initMocks();

        session = createStatsSession();
    }
    
    private DataSetBuilder createDataSetBuilder() {
        List<Field> fields = new ArrayList<Field>();
        Collections.addAll(fields, StandardField.values());
        Collections.addAll(fields, StandardMetaField.values());
        return FastFieldSetFactory.getInstance().newFieldSet(fields).newDataSetBuilder();
    }

    protected void initMocks() {}

    protected abstract StatsSession createStatsSession(DataRecorder... dataRecorders);

    protected void assertInitialState(final StatsSession session) {
        assertEquals(longDefault(StandardField.hits), session.getHits());
        assertEquals(longDefault(StandardField.firstHitStamp), session.getFirstHitStamp());
        assertEquals(longDefault(StandardField.lastHitStamp), session.getLastHitStamp());
        assertEquals(longDefault(StandardField.commits), session.getCommits());
        assertEquals(doubleDefault(StandardField.first), session.getFirst(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.min), session.getMin(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.max), session.getMax(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.last), session.getLast(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.sum), session.getSum(), TestUtil.DELTA);
    }

    @Test
    abstract public void testConstructWithNullKey();

    @Test
    abstract public void testConstructWithNullEventManager();

    @Test
    abstract public void testConstructWithNullFieldSetFactory();

    @Test
    public void testInitialData() {
        assertInitialState(session);
    }

    @Test
    public void testInitialCollectData() {
        DataSet data = session.collectData();
        for (Field field : StandardField.values()) {
            assertEquals(field.defaultValue(), data.getObject(field));
        }
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
    @SuppressWarnings("unchecked")
    public void testGetDataRecorders() {
        final DataRecorder mockDataRecorder = mockery.mock(DataRecorder.class);
        final Field mockField = mockery.mock(Field.class);

        mockery.checking(new Expectations() {{
            allowing(mockDataRecorder).getSupportedFields(); will(returnValue(Collections.singletonList(mockField)));
            allowing(mockField).name(); will(returnValue("test"));
            allowing(mockField).type(); will(returnValue(Field.Type.LONG));
            allowing(mockField).defaultValue(); will(returnValue(12L));
        }});

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

        assertEquals(session.getHits(), session.getLong(StandardField.hits));
        assertEquals(session.getFirstHitStamp(), session.getLong(StandardField.firstHitStamp));
        assertEquals(session.getLastHitStamp(), session.getLong(StandardField.lastHitStamp));
        assertEquals(session.getCommits(), session.getLong(StandardField.commits));
        assertEquals(session.getFirst(), session.getDouble(StandardField.first), TestUtil.DELTA);
        assertEquals(session.getLast(), session.getDouble(StandardField.last), TestUtil.DELTA);
        assertEquals(session.getMin(), session.getDouble(StandardField.min), TestUtil.DELTA);
        assertEquals(session.getMax(), session.getDouble(StandardField.max), TestUtil.DELTA);
        assertEquals(session.getSum(), session.getDouble(StandardField.sum), TestUtil.DELTA);
    }

    @Test
    public void testClear() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
            allowing(mockTracker).getValue(); will(returnValue(1.0));
        }});

        for (int i = 0; i < 10; i++) {
            session.track(mockTracker, System.currentTimeMillis());
            session.update(mockTracker, System.currentTimeMillis());
        }

        session.clear();

        assertInitialState(session);
    }

    @Test
    public void testCollectDataHasMetaCollectionStamp() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        final long now = System.currentTimeMillis();

        DataSet dataSet = session.collectData();

        long collectionStamp = dataSet.getLong(StandardMetaField.collectionStamp);
        assertTrue(collectionStamp >= now);
    }

    @Test
    public void testDrainDataHasMetaCollectionStamp() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        final long beforeDrain = System.currentTimeMillis();

        DataSet dataSet = session.drainData();

        long collectionStamp = dataSet.getLong(StandardMetaField.collectionStamp);
        assertTrue(collectionStamp >= beforeDrain);
    }

    @Test
    public void testDrainDataHasMetaDrainedSession() {
        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        DataSet dataSet = session.drainData();

        boolean drained = dataSet.getBoolean(StandardMetaField.drainedSession);
        assertTrue(drained);
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
        assertEquals(StandardField.commits.defaultValue(), session.getCommits());
        assertEquals(StandardField.first.defaultValue(), session.getFirst());
        assertEquals(StandardField.min.defaultValue(), session.getMin());
        assertEquals(StandardField.max.defaultValue(), session.getMax());
        assertEquals(StandardField.last.defaultValue(), session.getLast());
        assertEquals(StandardField.sum.defaultValue(), session.getSum());

        final long secondNow = System.currentTimeMillis();

        session.track(mockTracker, secondNow);

        assertEquals(2, session.getHits());
        assertEquals(firstNow, session.getFirstHitStamp());
        assertEquals(secondNow, session.getLastHitStamp());
        assertEquals(StandardField.commits.defaultValue(), session.getCommits());
        assertEquals(StandardField.first.defaultValue(), session.getFirst());
        assertEquals(StandardField.min.defaultValue(), session.getMin());
        assertEquals(StandardField.max.defaultValue(), session.getMax());
        assertEquals(StandardField.last.defaultValue(), session.getLast());
        assertEquals(StandardField.sum.defaultValue(), session.getSum());
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

        assertEquals(10L, dataSet.getLong(StandardField.hits));
        assertEquals(1L, dataSet.getLong(StandardField.firstHitStamp));
        assertEquals(10L, dataSet.getLong(StandardField.lastHitStamp));
        assertEquals(10L, dataSet.getLong(StandardField.commits));
        assertEquals(1.0, dataSet.getDouble(StandardField.first), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getDouble(StandardField.min), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getDouble(StandardField.max), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getDouble(StandardField.last), TestUtil.DELTA);
        assertEquals(10.0, dataSet.getDouble(StandardField.sum), TestUtil.DELTA);
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

        assertEquals(session.getHits(), dataSet.getLong(StandardField.hits));
        assertEquals(session.getFirstHitStamp(), dataSet.getLong(StandardField.firstHitStamp));
        assertEquals(session.getLastHitStamp(), dataSet.getLong(StandardField.lastHitStamp));
        assertEquals(session.getCommits(), dataSet.getLong(StandardField.commits));
        assertEquals(session.getFirst(), dataSet.getDouble(StandardField.first), TestUtil.DELTA);
        assertEquals(session.getMin(), dataSet.getDouble(StandardField.min), TestUtil.DELTA);
        assertEquals(session.getMax(), dataSet.getDouble(StandardField.max), TestUtil.DELTA);
        assertEquals(session.getLast(), dataSet.getDouble(StandardField.last), TestUtil.DELTA);
        assertEquals(session.getSum(), dataSet.getDouble(StandardField.sum), TestUtil.DELTA);
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

        assertEquals(10L, dataSet.getLong(StandardField.hits));
        assertEquals(1L, dataSet.getLong(StandardField.firstHitStamp));
        assertEquals(10L, dataSet.getLong(StandardField.lastHitStamp));
        assertEquals(10L, dataSet.getLong(StandardField.commits));
        assertEquals(1.0, dataSet.getDouble(StandardField.first), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getDouble(StandardField.min), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getDouble(StandardField.max), TestUtil.DELTA);
        assertEquals(1.0, dataSet.getDouble(StandardField.last), TestUtil.DELTA);
        assertEquals(10.0, dataSet.getDouble(StandardField.sum), TestUtil.DELTA);

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

        DataSet defaultDataSet = createDataSetBuilder().build();
        session.restore(defaultDataSet);

        // Should equal initial/cleared state
        assertInitialState(session);
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

        // Remove hits
        DataSet collectedData = session.collectData();
        DataSet dataSet = cloneDataSetWithoutField(collectedData, StandardField.hits);
        
        StatsSession anotherSession = createStatsSession((DataRecorder[]) null);

        anotherSession.restore(dataSet);

        assertInitialState(anotherSession);
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

        // Remove commits
        DataSet collectedDataSet = session.collectData();
        DataSet dataSet = cloneDataSetWithoutField(collectedDataSet, StandardField.commits);

        StatsSession anotherSession = createStatsSession((DataRecorder[]) null);

        anotherSession.restore(dataSet);

        assertEquals(session.getHits(), anotherSession.getHits());
        assertEquals(session.getFirstHitStamp(), anotherSession.getFirstHitStamp());
        assertEquals(session.getLastHitStamp(), anotherSession.getLastHitStamp());
        assertEquals(longDefault(StandardField.commits), anotherSession.getCommits());
        assertEquals(doubleDefault(StandardField.first), anotherSession.getFirst(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.min), anotherSession.getMin(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.max), anotherSession.getMax(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.last), anotherSession.getLast(), TestUtil.DELTA);
        assertEquals(doubleDefault(StandardField.sum), anotherSession.getSum(), TestUtil.DELTA);
    }

    private DataSet cloneDataSetWithoutField(DataSet data, StandardField fieldToSkip) {
        DataSetBuilder builder = createDataSetBuilder();
        for (Field field : data.getFieldSet().getFields()) {
            if (!field.equals(fieldToSkip)) {
                builder.set(field, data.getObject(field));
            }
        }
        return builder.build();
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
        session.getObject(StandardField.hits);
    }

    @Test
    public void testRestoreEatsDataManagerException() {
        // Recreate the session with a nasty DataRecorder
        session = createStatsSession(new NastyDataRecorder());

        mockery.checking(new Expectations() {{
            ignoring(mockEventManager);
        }});

        session.restore(createDataSetBuilder().build());
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
        public void collectData(StatsSession session, DataSetBuilder dataSet) {
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

        @Override
        public double getDouble(StatsSession session, Field field) {
            throw new RuntimeException();
        }

        @Override
        public long getLong(StatsSession session, Field field) {
            throw new RuntimeException();
        }

        @Override
        public Object getObject(StatsSession session, Field field) {
            throw new RuntimeException();
        }

        @Override
        public List<? extends Field> getSupportedFields() {
            throw new RuntimeException();
        }

    };
}

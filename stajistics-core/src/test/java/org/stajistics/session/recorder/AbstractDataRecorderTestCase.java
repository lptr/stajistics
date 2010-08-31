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
package org.stajistics.session.recorder;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.data.DataSetBuilder;
import org.stajistics.data.Field;
import org.stajistics.data.FieldSet;
import org.stajistics.data.DataSet.StandardField;
import org.stajistics.data.DataSet.StandardMetaField;
import org.stajistics.data.fast.FastFieldSetFactory;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.Tracker;

/**
 * @author The Stajistics Project
 */
public abstract class AbstractDataRecorderTestCase extends AbstractStajisticsTestCase {

    protected StatsSession mockSession;
    protected Tracker mockTracker;

    protected DataRecorder dataRecorder;

    @Before
    public void setUp() {
        mockSession = mockery.mock(StatsSession.class);
        mockTracker = mockery.mock(Tracker.class);

        dataRecorder = createDataRecorder();
    }

    protected abstract DataRecorder createDataRecorder();

    protected void buildStatsSessionExpectations() {
         mockery.checking(new Expectations() {{
            allowing(mockSession).getHits(); will(returnValue(1L));
            allowing(mockSession).getFirstHitStamp(); will(returnValue(10L));
            allowing(mockSession).getLastHitStamp(); will(returnValue(20L));
            allowing(mockSession).getCommits(); will(returnValue(1L));
            allowing(mockSession).getFirst(); will(returnValue(1.0));
            allowing(mockSession).getLast(); will(returnValue(1.0));
            allowing(mockSession).getMin(); will(returnValue(1.0));
            allowing(mockSession).getMax(); will(returnValue(1.0));
            allowing(mockSession).getSum(); will(returnValue(1.0));
        }});
    }

    @Test
    public void testGetSupportedFieldNamesNotEmpty() {
        assertNotNull(dataRecorder.getSupportedFields());
        assertFalse(dataRecorder.getSupportedFields()
                                .isEmpty());
    }

    @Test
    public void testGetSupportedFieldsNotNull() {

        buildStatsSessionExpectations();

        for (Field field : dataRecorder.getSupportedFields()) {
            assertNotNull(field + " is null",
                          dataRecorder.getObject(mockSession, field));
        }
    }

    @Test
    public void testClear() {

        mockery.checking(new Expectations() {{
            one(mockTracker).getValue(); will(returnValue(123.0));
        }});

        buildStatsSessionExpectations();

        dataRecorder.update(mockSession, mockTracker, 0L);
        dataRecorder.clear();
        
        FieldSet standardFieldSet = FastFieldSetFactory.getInstance()
                .newFieldSet(dataRecorder.getSupportedFields());
        DataSetBuilder emptyDataSetBuilder = standardFieldSet.newDataSetBuilder();
        dataRecorder.collectData(mockSession, emptyDataSetBuilder);

        dataRecorder = createDataRecorder(); // Create a new DataRecorder
        DataSetBuilder newDataSetBuilder = standardFieldSet.newDataSetBuilder();
        dataRecorder.collectData(mockSession, newDataSetBuilder);

        assertEquals(emptyDataSetBuilder.build(), newDataSetBuilder.build());
    }
}

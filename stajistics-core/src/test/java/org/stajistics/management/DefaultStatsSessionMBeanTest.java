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
package org.stajistics.management;

import static org.junit.Assert.*;

import java.util.Collections;

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.data.FieldSet;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionMBeanTest extends AbstractMBeanTestCase {

    protected StatsKey mockKey;
    protected StatsSessionManager mockSessionManager;
    protected StatsSession mockSession;
    protected FieldSet mockFieldSet;
    protected DataSet mockDataSet;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockSession = mockery.mock(StatsSession.class);
        mockFieldSet = mockery.mock(FieldSet.class);
        mockDataSet = mockery.mock(DataSet.class);
    }

    protected DefaultStatsSessionMBean createSessionMBean(final StatsSession session) {
        return new DefaultStatsSessionMBean(mockSessionManager, session);
    }

    private void buildEmptyDataSetExpectations() {
        mockery.checking(new Expectations() {{
            allowing(mockDataSet).getFieldSet(); will(returnValue(mockFieldSet));
            allowing(mockFieldSet).size(); will(returnValue(0));
            allowing(mockFieldSet).getFieldNames(); will(returnValue(Collections.emptyList()));
            allowing(mockDataSet).getObject(with(any(String.class))); will(returnValue(null));
        }});
    }

    @Test
    public void testGetImplementation() throws Exception {

        buildEmptyDataSetExpectations();

        mockery.checking(new Expectations() {{
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));
        }});

        StatsSessionMBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMBean.class);

        assertEquals(mockSession.getClass().getName(),
                     getMBeanServerConnection().getAttribute(name, "Implementation"));
    }

    @Test
    public void testGetDataSetField() throws Exception {

        final Long mockValue = 12L;
        mockery.checking(new Expectations() {{
            // For MBean registration
            
            allowing(mockDataSet).getFieldSet(); will(returnValue(mockFieldSet));
            allowing(mockFieldSet).size(); will(returnValue(1));
            allowing(mockFieldSet).getFieldNames(); will(returnValue(Collections.singletonList("test")));
            allowing(mockDataSet).getObject(with("test")); will(returnValue(mockValue));
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));

            // For this test
            allowing(mockSession).getObject(with("test")); will(returnValue(mockValue));
        }});

        StatsSessionMBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMBean.class);

        assertEquals(mockValue, getMBeanServerConnection().getAttribute(name, "_test"));
    }

    @Test
    public void testClear() throws Exception {
        buildEmptyDataSetExpectations();

        mockery.checking(new Expectations() {{
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));
            one(mockSession).clear();
        }});

        StatsSessionMBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMBean.class);

        getMBeanServerConnection().invoke(name, "clear", null, null);
    }

    @Test
    public void testDestroy() throws Exception {
        buildEmptyDataSetExpectations();

        mockery.checking(new Expectations() {{
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));
            one(mockSessionManager).remove(with(mockSession));
        }});

        StatsSessionMBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMBean.class);

        getMBeanServerConnection().invoke(name, "destroy", null, null);
    }


}

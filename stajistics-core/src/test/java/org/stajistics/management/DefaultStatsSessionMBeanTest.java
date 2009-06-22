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
package org.stajistics.management;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;
import org.stajistics.data.DataSet;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionMBeanTest extends AbstractMBeanTestCase {

    protected Mockery mockery;
    protected StatsKey mockKey;
    protected StatsSessionManager mockSessionManager;
    protected StatsSession mockSession;
    protected DataSet mockDataSet;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
        mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockSession = mockery.mock(StatsSession.class);
        mockDataSet = mockery.mock(DataSet.class);
    }

    protected DefaultStatsSessionMBean createSessionMBean(final StatsSession session) {
        return new DefaultStatsSessionMBean(mockSessionManager, session);
    }

    private void buildEmptyDataSetExpectations() {
        mockery.checking(new Expectations() {{
            allowing(mockDataSet).size(); will(returnValue(0));
            allowing(mockDataSet).getFieldNames(); will(returnValue(Collections.emptySet()));
            allowing(mockDataSet).getField(with(any(String.class))); will(returnValue(null));
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

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetDataSetField() throws Exception {

        mockery.checking(new Expectations() {{
            allowing(mockDataSet).size(); will(returnValue(1));
            allowing(mockDataSet).getFieldNames(); will(returnValue(new HashSet<String>(Collections.singletonList("test"))));
            allowing(mockDataSet).getField(with("test")); will(returnValue("value"));

            allowing(mockSession).collectData(); will(returnValue(mockDataSet));
        }});

        StatsSessionMBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMBean.class);

        assertEquals("value", getMBeanServerConnection().getAttribute(name, "_test"));

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
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

        mockery.assertIsSatisfied();
    }

    
}
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
package org.stajistics.management.beans;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashSet;

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.StatsManagerRegistry;
import org.stajistics.data.DataSet;
import org.stajistics.management.AbstractJMXTestCase;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionMXBeanTest extends AbstractJMXTestCase {

    private static final String NAMESPACE = "ns";

    protected StatsKey mockKey;
    protected StatsManager mockManager;
    protected StatsSessionManager mockSessionManager;
    protected StatsSession mockSession;
    protected DataSet mockDataSet;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        mockManager = mockery.mock(StatsManager.class);
        mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockSession = mockery.mock(StatsSession.class);
        mockDataSet = mockery.mock(DataSet.class);
        
        mockery.checking(new Expectations() {{
            allowing(mockManager).getNamespace();
            will(returnValue(NAMESPACE));

            allowing(mockManager).getSessionManager();
            will(returnValue(mockSessionManager));
        }});
        
        StatsManagerRegistry.getInstance().registerStatsManager(mockManager);
    }

    @After
    public void tearDown() {
        StatsManagerRegistry.getInstance().removeStatsManager(mockManager);
    }
    
    protected DefaultStatsSessionMXBean createSessionMBean(final StatsSession session) {
        return new DefaultStatsSessionMXBean(NAMESPACE, session);
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

        StatsSessionMXBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMXBean.class);

        assertEquals(mockSession.getClass().getName(),
                     getMBeanServerConnection().getAttribute(name, "Implementation"));
    }

    @Test
    public void testGetDataSetField() throws Exception {

        mockery.checking(new Expectations() {{
            // For MBean registration
            allowing(mockDataSet).size(); will(returnValue(1));
            allowing(mockDataSet).getFieldNames(); will(returnValue(new HashSet<String>(Collections.singletonList("test"))));
            allowing(mockDataSet).getField(with("test")); will(returnValue("value"));
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));

            // For this test
            allowing(mockSession).getField(with("test")); will(returnValue("value"));
        }});

        StatsSessionMXBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMXBean.class);

        assertEquals("value", getMBeanServerConnection().getAttribute(name, "_test"));
    }

    @Test
    public void testClear() throws Exception {
        buildEmptyDataSetExpectations();

        mockery.checking(new Expectations() {{
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));
            one(mockSession).clear();
        }});

        StatsSessionMXBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMXBean.class);

        getMBeanServerConnection().invoke(name, "clear", null, null);
    }

    @Test
    public void testDestroy() throws Exception {
        buildEmptyDataSetExpectations();

        mockery.checking(new Expectations() {{
            allowing(mockSession).collectData(); will(returnValue(mockDataSet));
            one(mockSessionManager).remove(with(mockSession));
        }});

        StatsSessionMXBean mBean = createSessionMBean(mockSession);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsSessionMXBean.class);

        getMBeanServerConnection().invoke(name, "destroy", null, null);
    }


}

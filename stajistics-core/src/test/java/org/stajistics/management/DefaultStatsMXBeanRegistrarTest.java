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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.stajistics.TestUtil.buildStatsKeyExpectations;
import static org.stajistics.management.StatsMXBeanUtil.MANAGER_NAME_CONFIG;
import static org.stajistics.management.StatsMXBeanUtil.MANAGER_NAME_SESSION;
import static org.stajistics.management.StatsMXBeanUtil.MANAGER_NAME_STATS;
import static org.stajistics.management.StatsMXBeanUtil.SUBTYPE_CONFIG;
import static org.stajistics.management.StatsMXBeanUtil.SUBTYPE_SESSION;
import static org.stajistics.management.StatsMXBeanUtil.TYPE_KEYS;
import static org.stajistics.management.StatsMXBeanUtil.buildKeyName;
import static org.stajistics.management.StatsMXBeanUtil.buildManagerName;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.management.beans.StatsConfigMXBean;
import org.stajistics.management.beans.StatsConfigManagerMXBean;
import org.stajistics.management.beans.StatsManagerMXBean;
import org.stajistics.management.beans.StatsSessionMXBean;
import org.stajistics.management.beans.StatsSessionManagerMXBean;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsMXBeanRegistrarTest extends AbstractStajisticsTestCase {

    private static final String NAMESPACE = "ns";

    private static final String NORMAL = "normal";

    private StatsKey mockKey;
    private StatsMXBeanFactory mockMBeanFactory;
    private StatsManager mockStatsManager;
    private StatsSessionManager mockSessionManager;
    private StatsConfigManager mockConfigManager;

    private MBeanServer mBeanServer;

    private DefaultStatsMXBeanRegistrar mxBeanRegistrar;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        mockStatsManager = mockery.mock(StatsManager.class);
        mockSessionManager = mockery.mock(StatsSessionManager.class);
        mockConfigManager = mockery.mock(StatsConfigManager.class);
        mockMBeanFactory = mockery.mock(StatsMXBeanFactory.class);

        mBeanServer = MBeanServerFactory.newMBeanServer();

        mxBeanRegistrar = new DefaultStatsMXBeanRegistrar(NAMESPACE, mockMBeanFactory, mBeanServer);

        mockery.checking(new Expectations() {{
            allowing(mockStatsManager).getNamespace();
            will(returnValue(NAMESPACE));
        }});
    }

    @After
    public void tearDown() {
        mxBeanRegistrar = null;
    }

    @Test
    public void testConstructWithNullNamespace() {
        try {
            new DefaultStatsMXBeanRegistrar(null, mockMBeanFactory, MBeanServerFactory.newMBeanServer());
        } catch (NullPointerException npe) {
            assertEquals("namespace", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithEmptyNamespace() {
        try {
            new DefaultStatsMXBeanRegistrar("", mockMBeanFactory, MBeanServerFactory.newMBeanServer());
        } catch (IllegalArgumentException e) {
            assertEquals("empty namespace", e.getMessage());
        }
    }

    @Test
    public void testConstructWithNullMBeanFactory() {
        try {
            new DefaultStatsMXBeanRegistrar(NAMESPACE, null, MBeanServerFactory.newMBeanServer());
        } catch (NullPointerException npe) {
            assertEquals("mBeanFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullMBeanServer() {
        try {
            new DefaultStatsMXBeanRegistrar(NAMESPACE, mockMBeanFactory, null);
        } catch (NullPointerException npe) {
            assertEquals("mBeanServer", npe.getMessage());
        }
    }

    @Test
    public void testGetMBeanServer() {
        assertSame(mBeanServer, mxBeanRegistrar.getMBeanServer());
    }

    @Test
    public void testRegisterManagerMBean() throws Exception {

        final StatsManagerMXBean mockManagerMBean = mockery.mock(StatsManagerMXBean.class);

        mockery.checking(new Expectations() {{
            one(mockMBeanFactory).createManagerMBean(mockStatsManager);
            will(returnValue(mockManagerMBean));
        }});

        ObjectName objectName = new ObjectName(buildManagerName(NAMESPACE, MANAGER_NAME_STATS, true));

        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
        mxBeanRegistrar.registerStatsManagerMXBean(mockStatsManager);
        assertEquals(1, mBeanServer.queryMBeans(objectName, null).size());
    }

    @Test
    public void testUnregisterManagerMBean() throws Exception {
        ObjectName objectName = new ObjectName(buildManagerName(NAMESPACE,MANAGER_NAME_STATS, true));

        final StatsManagerMXBean mockManagerMBean = mockery.mock(StatsManagerMXBean.class);

        mBeanServer.registerMBean(mockManagerMBean, objectName);

        mxBeanRegistrar.unregisterStatsManagerMXBean(mockStatsManager);
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
    }

    @Test
    public void testRegisterSessionManagerMBean() throws Exception {

        final StatsSessionManagerMXBean mockSessionManagerMBean = mockery.mock(StatsSessionManagerMXBean.class);

        mockery.checking(new Expectations() {{
            one(mockMBeanFactory).createSessionManagerMBean(with(mockSessionManager));
            will(returnValue(mockSessionManagerMBean));
        }});

        ObjectName objectName = new ObjectName(buildManagerName(NAMESPACE, MANAGER_NAME_SESSION, true));
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
        mxBeanRegistrar.registerSessionManagerMXBean(mockSessionManager);
        assertEquals(1, mBeanServer.queryMBeans(objectName, null).size());
    }

    @Test
    public void testUnregisterSessionManagerMBean() throws Exception {
        ObjectName objectName = new ObjectName(buildManagerName(NAMESPACE, MANAGER_NAME_SESSION, true));
        final StatsSessionManagerMXBean mockSessionManagerMBean = mockery.mock(StatsSessionManagerMXBean.class);

        mBeanServer.registerMBean(mockSessionManagerMBean, objectName);

        mxBeanRegistrar.unregisterSessionManagerMXBean();
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
    }

    @Test
    public void testRegisterConfigManagerMBean() throws Exception {

        final StatsConfigManagerMXBean mockConfigManagerMBean = mockery.mock(StatsConfigManagerMXBean.class);

        mockery.checking(new Expectations() {{
            one(mockMBeanFactory).createConfigManagerMBean(mockConfigManager);
            will(returnValue(mockConfigManagerMBean));
        }});

        ObjectName objectName = new ObjectName(buildManagerName(NAMESPACE,MANAGER_NAME_CONFIG, true));
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
        mxBeanRegistrar.registerConfigManagerMXBean(mockConfigManager);
        assertEquals(1, mBeanServer.queryMBeans(objectName, null).size());
    }

    @Test
    public void testUnregisterConfigManagerMBean() throws Exception {
        ObjectName objectName = new ObjectName(buildManagerName(NAMESPACE, MANAGER_NAME_CONFIG, true));
        final StatsConfigManagerMXBean mockConfigManagerMBean = mockery.mock(StatsConfigManagerMXBean.class);

        mBeanServer.registerMBean(mockConfigManagerMBean, objectName);

        mxBeanRegistrar.unregisterConfigManagerMXBean();
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
    }

    @Test
    public void testRegisterConfigMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsConfig mockConfig = mockery.mock(StatsConfig.class);
        final StatsConfigBuilderFactory mockConfigBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        final StatsConfigMXBean mockConfigMBean = mockery.mock(StatsConfigMXBean.class);

        mockery.checking(new Expectations() {{
            allowing(mockStatsManager).getConfigBuilderFactory(); will(returnValue(mockConfigBuilderFactory));
            one(mockMBeanFactory).createConfigMBean(NAMESPACE, mockKey, mockConfig); will(returnValue(mockConfigMBean));
            ignoring(mockConfig);
        }});

        ObjectName name = new ObjectName(buildKeyName(NAMESPACE, mockKey, TYPE_KEYS, SUBTYPE_CONFIG, true));

        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
        mxBeanRegistrar.registerConfigMXBean(mockKey, mockConfig);
        assertEquals(1, mBeanServer.queryMBeans(name, null).size());
    }

    @Test
    public void testUnregisterConfigMBeanIfNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsConfig mockConfig = mockery.mock(StatsConfig.class);
        final StatsConfigMXBean mockConfigMbean = mockery.mock(StatsConfigMXBean.class);

        mockery.checking(new Expectations() {{
            ignoring(mockConfig);
        }});

        ObjectName name = new ObjectName(buildKeyName(NAMESPACE, mockKey, TYPE_KEYS, SUBTYPE_CONFIG, true));

        mBeanServer.registerMBean(mockConfigMbean, name);

        mxBeanRegistrar.unregisterConfigMXBeanIfNecessary(mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    @Test
    public void testUnregisterConfigMBeanIfNotNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        ObjectName name = new ObjectName(buildKeyName(NAMESPACE, mockKey, TYPE_KEYS, SUBTYPE_SESSION, true));

        mxBeanRegistrar.unregisterConfigMXBeanIfNecessary(mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    @Test
    public void testRegisterSessionMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsSession mockSession = mockery.mock(StatsSession.class);
        final StatsSessionMXBean mockSessionMBean = mockery.mock(StatsSessionMXBean.class);

        mockery.checking(new Expectations() {{
            allowing(mockStatsManager).getSessionManager(); will(returnValue(mockSessionManager));
            one(mockMBeanFactory).createSessionMBean(NAMESPACE, mockSession); will(returnValue(mockSessionMBean));
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        ObjectName name = new ObjectName(buildKeyName(NAMESPACE, mockKey, TYPE_KEYS, SUBTYPE_SESSION, true));

        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
        mxBeanRegistrar.registerSessionMXBean(mockSession);
        assertEquals(1, mBeanServer.queryMBeans(name, null).size());
    }

    @Test
    public void testUnregisterSessionMBeanIfNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsSession mockSession = mockery.mock(StatsSession.class);
        final StatsSessionMXBean mockSessionMBean = mockery.mock(StatsSessionMXBean.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        ObjectName name = new ObjectName(buildKeyName(NAMESPACE, mockKey, TYPE_KEYS, SUBTYPE_SESSION, true));

        mBeanServer.registerMBean(mockSessionMBean, name);

        mxBeanRegistrar.unregisterSessionMXBeanIfNecessary(mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    @Test
    public void testUnregisterSessionMBeanIfNotNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        ObjectName name = new ObjectName(buildKeyName(NAMESPACE, mockKey, TYPE_KEYS, SUBTYPE_SESSION, true));

        mxBeanRegistrar.unregisterSessionMXBeanIfNecessary(mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

}

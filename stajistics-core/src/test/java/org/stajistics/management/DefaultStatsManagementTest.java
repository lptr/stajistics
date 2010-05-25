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
import static org.junit.Assert.fail;
import static org.stajistics.TestUtil.buildStatsKeyExpectations;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class DefaultStatsManagementTest {

    private static final String TYPE_TEST = "test";
    private static final String SUBTYPE_TEST = "test";

    private static final String NORMAL = "normal";

    private Mockery mockery;
    private StatsKey mockKey;
    private StatsMBeanFactory mockMBeanFactory;
    private StatsManager mockStatsManager;

    private MBeanServer mBeanServer;

    private DefaultStatsManagement statsManagement;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
        mockStatsManager = mockery.mock(StatsManager.class);
        mockMBeanFactory = mockery.mock(StatsMBeanFactory.class);

        mBeanServer = MBeanServerFactory.newMBeanServer();

        statsManagement = new DefaultStatsManagement(mockMBeanFactory, mBeanServer);
    }

    @After
    public void tearDown() {
        statsManagement = null;
    }

    @Test
    public void testConstructWithNullMBeanFactory() {
        try {
            new DefaultStatsManagement(null, MBeanServerFactory.newMBeanServer());
        } catch (NullPointerException npe) {
            assertEquals("mBeanFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullMBeanServer() {
        try {
            new DefaultStatsManagement(mockMBeanFactory, null);
        } catch (NullPointerException npe) {
            assertEquals("mBeanServer", npe.getMessage());
        }
    }

    @Test
    public void testGetMBeanServer() {
        assertSame(mBeanServer, statsManagement.getMBeanServer());
    }

    @Test
    public void testBuildNameDomain() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL);
        String name = statsManagement.buildName(mockStatsManager, mockKey, TYPE_TEST, SUBTYPE_TEST, true);
        assertTrue(name.startsWith(DefaultStatsManagement.DOMAIN));
    }

    @Test
    public void testBuildNameNormal() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithQuestion() {
        buildStatsKeyExpectations(mockery, mockKey, "with?question");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithAsterisk() {
        buildStatsKeyExpectations(mockery, mockKey, "with*asterisk");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithQuote() {
        buildStatsKeyExpectations(mockery, mockKey, "with\"quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithTwoQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, "with\"two\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithThreeQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, "with\"three\"awesome\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithEquals() {
        buildStatsKeyExpectations(mockery, mockKey, "with=equals");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithComma() {
        buildStatsKeyExpectations(mockery, mockKey, "with,comma");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameValNormal() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithQuestion() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with?question", NORMAL);
        assertInvalidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithAsterisk() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with*asterisk", NORMAL);
        assertInvalidObjectName();
    }

    /*
     * Hmm. This invalid(?) name is permitted by new ObjectName(name);
     * TODO: need to manually invalidate property names of this type?
     * 
    @Test
    public void testBuildNamePropNameWithQuote() {
        buildExpectations(NORMAL, "with\"quote", NORMAL);
        assertInvalidObjectName();
    }
    */

    @Test
    public void testBuildNamePropNameWithEquals() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with=equals", NORMAL);
        assertInvalidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithComma() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, "with,comma", NORMAL);
        assertInvalidObjectName();
    }

    @Test
    public void testBuildNamePropValWithQuestion() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with?question");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithAsterisk() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with*asterisk");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithQuote() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\"quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithTwoQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\"two\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithThreeQuotes() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with\"three\"awesome\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithEquals() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with=equals");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithComma() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL, NORMAL, "with,comma");
        assertValidObjectName();
    }

    @Test
    public void testRegisterSessionManagerMBean() throws Exception {

        final StatsSessionManagerMBean mockSessionManagerMBean = mockery.mock(StatsSessionManagerMBean.class);

        mockery.checking(new Expectations() {{
            one(mockMBeanFactory).createSessionManagerMBean(with(mockStatsManager)); will(returnValue(mockSessionManagerMBean));
        }});

        ObjectName objectName = new ObjectName(statsManagement.buildManagerName(mockStatsManager, 
                                                                                DefaultStatsManagement.MANAGER_NAME_SESSION));
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
        statsManagement.registerSessionManagerMBean(mockStatsManager);
        assertEquals(1, mBeanServer.queryMBeans(objectName, null).size());
    }

    @Test
    public void testUnregisterSessionManagerMBean() throws Exception {
        ObjectName objectName = new ObjectName(statsManagement.buildManagerName(mockStatsManager, 
                                                                                DefaultStatsManagement.MANAGER_NAME_SESSION));
        final StatsSessionManagerMBean mockSessionManagerMBean = mockery.mock(StatsSessionManagerMBean.class);

        mBeanServer.registerMBean(mockSessionManagerMBean, objectName);

        statsManagement.unregisterSessionManagerMBean(mockStatsManager);
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
    }

    @Test
    public void testRegisterConfigManagerMBean() throws Exception {

        final StatsConfigManagerMBean mockConfigManagerMBean = mockery.mock(StatsConfigManagerMBean.class);

        mockery.checking(new Expectations() {{
            one(mockMBeanFactory).createConfigManagerMBean(mockStatsManager); will(returnValue(mockConfigManagerMBean));
        }});

        ObjectName objectName = new ObjectName(statsManagement.buildManagerName(mockStatsManager,
                                                                                DefaultStatsManagement.MANAGER_NAME_CONFIG));
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
        statsManagement.registerConfigManagerMBean(mockStatsManager);
        assertEquals(1, mBeanServer.queryMBeans(objectName, null).size());
    }

    @Test
    public void testUnregisterConfigManagerMBean() throws Exception {
        ObjectName objectName = new ObjectName(statsManagement.buildManagerName(mockStatsManager,
                                                                                DefaultStatsManagement.MANAGER_NAME_CONFIG));
        final StatsConfigManagerMBean mockConfigManagerMBean = mockery.mock(StatsConfigManagerMBean.class);

        mBeanServer.registerMBean(mockConfigManagerMBean, objectName);

        statsManagement.unregisterConfigManagerMBean(mockStatsManager);
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
    }

    @Test
    public void testRegisterConfigMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsConfig mockConfig = mockery.mock(StatsConfig.class);
        final StatsConfigFactory mockConfigFactory = mockery.mock(StatsConfigFactory.class);
        final StatsConfigMBean mockConfigMBean = mockery.mock(StatsConfigMBean.class);

        mockery.checking(new Expectations() {{
            allowing(mockStatsManager).getConfigFactory(); will(returnValue(mockConfigFactory));
            one(mockMBeanFactory).createConfigMBean(mockStatsManager, mockKey, mockConfig); will(returnValue(mockConfigMBean));
            ignoring(mockConfig);
        }});

        ObjectName name = new ObjectName(statsManagement.buildName(mockStatsManager, mockKey, DefaultStatsManagement.TYPE_KEYS, DefaultStatsManagement.SUBTYPE_CONFIG, false));

        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
        statsManagement.registerConfigMBean(mockStatsManager, mockKey, mockConfig);
        assertEquals(1, mBeanServer.queryMBeans(name, null).size());
    }

    @Test
    public void testUnregisterConfigMBeanIfNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsConfig mockConfig = mockery.mock(StatsConfig.class);
        final StatsConfigMBean mockConfigMbean = mockery.mock(StatsConfigMBean.class);

        mockery.checking(new Expectations() {{
            ignoring(mockConfig);
        }});

        ObjectName name = new ObjectName(statsManagement.buildName(mockStatsManager, mockKey, DefaultStatsManagement.TYPE_KEYS, DefaultStatsManagement.SUBTYPE_CONFIG, false));

        mBeanServer.registerMBean(mockConfigMbean, name);

        statsManagement.unregisterConfigMBeanIfNecessary(mockStatsManager, mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    @Test
    public void testUnregisterConfigMBeanIfNotNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        ObjectName name = new ObjectName(statsManagement.buildName(mockStatsManager, mockKey, DefaultStatsManagement.TYPE_KEYS, DefaultStatsManagement.SUBTYPE_SESSION, false));

        statsManagement.unregisterConfigMBeanIfNecessary(mockStatsManager, mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    @Test
    public void testRegisterSessionMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsSession mockSession = mockery.mock(StatsSession.class);
        final StatsSessionManager mockSessionManager = mockery.mock(StatsSessionManager.class);
        final StatsSessionMBean mockSessionMBean = mockery.mock(StatsSessionMBean.class);

        mockery.checking(new Expectations() {{
            allowing(mockStatsManager).getSessionManager(); will(returnValue(mockSessionManager));
            one(mockMBeanFactory).createSessionMBean(mockStatsManager, mockSession); will(returnValue(mockSessionMBean));
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        ObjectName name = new ObjectName(statsManagement.buildName(mockStatsManager, mockKey, DefaultStatsManagement.TYPE_KEYS, DefaultStatsManagement.SUBTYPE_SESSION, false));

        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
        statsManagement.registerSessionMBean(mockStatsManager, mockSession);
        assertEquals(1, mBeanServer.queryMBeans(name, null).size());
    }

    @Test
    public void testUnregisterSessionMBeanIfNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final StatsSession mockSession = mockery.mock(StatsSession.class);
        final StatsSessionMBean mockSessionMBean = mockery.mock(StatsSessionMBean.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        ObjectName name = new ObjectName(statsManagement.buildName(mockStatsManager, mockKey, DefaultStatsManagement.TYPE_KEYS, DefaultStatsManagement.SUBTYPE_SESSION, false));

        mBeanServer.registerMBean(mockSessionMBean, name);

        statsManagement.unregisterSessionMBeanIfNecessary(mockStatsManager, mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    @Test
    public void testUnregisterSessionMBeanIfNotNecessary() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        ObjectName name = new ObjectName(statsManagement.buildName(mockStatsManager, mockKey, DefaultStatsManagement.TYPE_KEYS, DefaultStatsManagement.SUBTYPE_SESSION, false));

        statsManagement.unregisterSessionMBeanIfNecessary(mockStatsManager, mockKey);
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
    }

    private void assertValidObjectName() {

        String name = statsManagement.buildName(mockStatsManager, mockKey, TYPE_TEST, SUBTYPE_TEST, true);

        try {
            new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            fail(e.toString());
        }
    }

    private void assertInvalidObjectName() {
        String name = statsManagement.buildName(mockStatsManager, mockKey, TYPE_TEST, SUBTYPE_TEST, true);

        try {
            new ObjectName(name);
            fail("Malformed name is accepted: " + name);

        } catch (MalformedObjectNameException e) {
            // Expected
        }
    }
}

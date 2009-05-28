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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.stajistics.TestUtil.buildStatsKeyExpectations;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagementTest {

    private static final String TYPE_TEST = "test";
    private static final String SUBTYPE_TEST = "test";

    private static final String NORMAL = "normal";

    private Mockery mockery;
    private StatsKey mockKey;

    private StatsManagement statsManagement;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);

        statsManagement = new StatsManagement(MBeanServerFactory.newMBeanServer());
    }

    @After
    public void tearDown() {
        statsManagement = null;
    }

    @Test
    public void testBuildNameDomain() {
        buildStatsKeyExpectations(mockery, mockKey, NORMAL);
        String name = statsManagement.buildName(mockKey, TYPE_TEST, SUBTYPE_TEST, true);
        assertTrue(name.startsWith(StatsManagement.DOMAIN));
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
        ObjectName objectName = new ObjectName(statsManagement.buildSessionManagerName());
        MBeanServer mBeanServer = statsManagement.getMBeanServer();

        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);

        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
        assertTrue(statsManagement.registerSessionManagerMBean(sessionManager));
        assertEquals(1, mBeanServer.queryMBeans(objectName, null).size());
    }

    @Test
    public void testUnregisterSessionManagerMBean() throws Exception {
        ObjectName objectName = new ObjectName(statsManagement.buildSessionManagerName());
        MBeanServer mBeanServer = statsManagement.getMBeanServer();

        StatsSessionManager sessionManager = mockery.mock(StatsSessionManager.class);

        mBeanServer.registerMBean(new SessionManager(sessionManager), objectName);

        assertTrue(statsManagement.unregisterSessionManagerMBean());
        assertTrue(mBeanServer.queryMBeans(objectName, null).isEmpty());
    }

    @Test
    public void testRegisterConfigMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final org.stajistics.StatsConfig mockConfig = mockery.mock(org.stajistics.StatsConfig.class);

        mockery.checking(new Expectations() {{
            ignoring(mockConfig);
        }});

        MBeanServer mBeanServer = statsManagement.getMBeanServer();

        ObjectName name = new ObjectName(statsManagement.buildName(mockKey, StatsManagement.TYPE_KEYS, StatsManagement.SUBTYPE_CONFIG, false));

        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
        assertTrue(statsManagement.registerConfigMBean(mockKey, mockConfig));
        assertEquals(1, mBeanServer.queryMBeans(name, null).size());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testUnregisterConfigMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final org.stajistics.StatsConfig mockConfig = mockery.mock(org.stajistics.StatsConfig.class);

        mockery.checking(new Expectations() {{
            ignoring(mockConfig);
        }});

        MBeanServer mBeanServer = statsManagement.getMBeanServer();

        ObjectName name = new ObjectName(statsManagement.buildName(mockKey, StatsManagement.TYPE_KEYS, StatsManagement.SUBTYPE_CONFIG, false));

        mBeanServer.registerMBean(new StatsConfig(mockKey, mockConfig), name);

        assertTrue(statsManagement.unregisterConfigMBean(mockKey));
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testRegisterSessionMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final org.stajistics.session.StatsSession mockSession = mockery.mock(org.stajistics.session.StatsSession.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        MBeanServer mBeanServer = statsManagement.getMBeanServer();

        ObjectName name = new ObjectName(statsManagement.buildName(mockKey, StatsManagement.TYPE_KEYS, StatsManagement.SUBTYPE_SESSION, false));

        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
        assertTrue(statsManagement.registerSessionMBean(mockSession));
        assertEquals(1, mBeanServer.queryMBeans(name, null).size());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testUnregisterSessionMBean() throws Exception {

        buildStatsKeyExpectations(mockery, mockKey, NORMAL);

        final org.stajistics.session.StatsSession mockSession = mockery.mock(org.stajistics.session.StatsSession.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        MBeanServer mBeanServer = statsManagement.getMBeanServer();

        ObjectName name = new ObjectName(statsManagement.buildName(mockKey, StatsManagement.TYPE_KEYS, StatsManagement.SUBTYPE_SESSION, false));

        mBeanServer.registerMBean(new StatsSession(mockSession), name);

        assertTrue(statsManagement.unregisterSessionMBean(mockKey));
        assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());

        mockery.assertIsSatisfied();
    }

    private void assertValidObjectName() {

        String name = statsManagement.buildName(mockKey, TYPE_TEST, SUBTYPE_TEST, true);

        try {
            new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            fail(e.toString());
        }

        mockery.assertIsSatisfied();
    }

    private void assertInvalidObjectName() {
        String name = statsManagement.buildName(mockKey, TYPE_TEST, SUBTYPE_TEST, true);

        try {
            new ObjectName(name);
            fail("Malformed name is accepted: " + name);

        } catch (MalformedObjectNameException e) {
            // Expected
        }

        mockery.assertIsSatisfied();
    }
}

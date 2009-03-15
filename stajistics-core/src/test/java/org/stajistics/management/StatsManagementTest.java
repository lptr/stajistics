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

import java.util.Collections;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagementTest {

    private static final String TEST_TYPE = "test";

    private static final String NORMAL = "normal";

    private Mockery mockery;
    private StatsKey mockKey;

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
    }

    private StatsManagement getStatsManagement() {
        return StatsManagement.getInstance();
    }

    private void buildStatsKeyExpectations(final String keyName) {
        buildExpectations(keyName, null, null);
    }

    private void buildExpectations(final String keyName,
                                   final String attrName,
                                   final String attrValue) {
        mockery.checking(new Expectations() {{
            ignoring(mockKey).getName(); will(returnValue(keyName));
            ignoring(mockKey).getAttribute(with((String)null)); will(returnValue(null));
            if (attrName == null) {
                ignoring(mockKey).getAttribute((String)with(anything())); will(returnValue(null));
                ignoring(mockKey).getAttributeCount(); will(returnValue(0));
                ignoring(mockKey).getAttributes(); will(returnValue(Collections.emptyMap()));
            } else {
                ignoring(mockKey).getAttribute(with(attrName)); will(returnValue(attrValue));
                ignoring(mockKey).getAttributeCount(); will(returnValue(1));
                ignoring(mockKey).getAttributes(); will(returnValue(Collections.singletonMap(attrName, attrValue)));
            }
        }});
    }

    @Test
    public void testBuildNameDomain() {
        buildStatsKeyExpectations(NORMAL);
        String name = getStatsManagement().buildName(mockKey, TEST_TYPE, true);
        assertTrue(name.startsWith(StatsManagement.DOMAIN));
    }

    @Test
    public void testBuildNameNormal() {
        buildStatsKeyExpectations(NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithQuestion() {
        buildStatsKeyExpectations("with?question");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithAsterisk() {
        buildStatsKeyExpectations("with*asterisk");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithQuote() {
        buildStatsKeyExpectations("with\"quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithTwoQuotes() {
        buildStatsKeyExpectations("with\"two\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithThreeQuotes() {
        buildStatsKeyExpectations("with\"three\"awesome\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithEquals() {
        buildStatsKeyExpectations("with=equals");
        assertValidObjectName();
    }

    @Test
    public void testBuildNameWithComma() {
        buildStatsKeyExpectations("with,comma");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameValNormal() {
        buildExpectations(NORMAL, NORMAL, NORMAL);
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithQuestion() {
        buildExpectations(NORMAL, "with?question", NORMAL);
        assertInvalidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithAsterisk() {
        buildExpectations(NORMAL, "with*asterisk", NORMAL);
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
        buildExpectations(NORMAL, "with=equals", NORMAL);
        assertInvalidObjectName();
    }

    @Test
    public void testBuildNamePropNameWithComma() {
        buildExpectations(NORMAL, "with,comma", NORMAL);
        assertInvalidObjectName();
    }

    @Test
    public void testBuildNamePropValWithQuestion() {
        buildExpectations(NORMAL, NORMAL, "with?question");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithAsterisk() {
        buildExpectations(NORMAL, NORMAL, "with*asterisk");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithQuote() {
        buildExpectations(NORMAL, NORMAL, "with\"quote");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithTwoQuotes() {
        buildExpectations(NORMAL, NORMAL, "with\"two\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithThreeQuotes() {
        buildExpectations(NORMAL, NORMAL, "with\"three\"awesome\"quotes");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithEquals() {
        buildExpectations(NORMAL, NORMAL, "with=equals");
        assertValidObjectName();
    }

    @Test
    public void testBuildNamePropValWithComma() {
        buildExpectations(NORMAL, NORMAL, "with,comma");
        assertValidObjectName();
    }

    @Test
    public void testRegisterSessionManagerMBean() throws Exception {
        StatsManagement sm = getStatsManagement();

        ObjectName name = sm.createSessionManagerObjectName();
        MBeanServer mBeanServer = sm.getMBeanServer();

        try {
            assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
            assertTrue(sm.registerSessionManagerMBean());
            assertEquals(1, mBeanServer.queryMBeans(name, null).size());

        } finally {
            try {
                mBeanServer.unregisterMBean(name);
            } catch (Exception e) {}
        }
    }

    @Test
    public void testUnregisterSessionManagerMBean() throws Exception {
        StatsManagement sm = getStatsManagement();

        ObjectName name = sm.createSessionManagerObjectName();
        MBeanServer mBeanServer = sm.getMBeanServer();

        try {
            mBeanServer.registerMBean(new SessionManager(), name);

            assertTrue(sm.unregisterSessionManagerMBean());
            assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());

        } finally {
            try {
                mBeanServer.unregisterMBean(name);
            } catch (Exception e) {}
        }
    }

    @Test
    public void testRegisterConfigMBean() throws Exception {

        buildStatsKeyExpectations(NORMAL);

        final org.stajistics.StatsConfig mockConfig = mockery.mock(org.stajistics.StatsConfig.class);

        mockery.checking(new Expectations() {{
            ignoring(mockConfig);
        }});

        StatsManagement sm = getStatsManagement();

        MBeanServer mBeanServer = sm.getMBeanServer();

        ObjectName name = new ObjectName(sm.buildName(mockKey, StatsManagement.CONFIG_TYPE, false));

        try {
            assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
            assertTrue(sm.registerConfigMBean(mockKey, mockConfig));
            assertEquals(1, mBeanServer.queryMBeans(name, null).size());

        } finally {
            try {
                mBeanServer.unregisterMBean(name);
            } catch (Exception e) {}
        }

        mockery.assertIsSatisfied();
    }

    @Test
    public void testUnregisterConfigMBean() throws Exception {

        buildStatsKeyExpectations(NORMAL);

        final org.stajistics.StatsConfig mockConfig = mockery.mock(org.stajistics.StatsConfig.class);

        mockery.checking(new Expectations() {{
            ignoring(mockConfig);
        }});

        StatsManagement sm = getStatsManagement();

        MBeanServer mBeanServer = sm.getMBeanServer();

        ObjectName name = new ObjectName(sm.buildName(mockKey, StatsManagement.CONFIG_TYPE, false));

        try {
            mBeanServer.registerMBean(new StatsConfig(mockConfig), name);

            assertTrue(sm.unregisterConfigMBean(mockKey));
            assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());

        } finally {
            try {
                mBeanServer.unregisterMBean(name);
            } catch (Exception e) {}
        }

        mockery.assertIsSatisfied();
    }

    @Test
    public void testRegisterSessionMBean() throws Exception {

        buildStatsKeyExpectations(NORMAL);

        final org.stajistics.session.StatsSession mockSession = mockery.mock(org.stajistics.session.StatsSession.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        StatsManagement sm = getStatsManagement();

        MBeanServer mBeanServer = sm.getMBeanServer();

        ObjectName name = new ObjectName(sm.buildName(mockKey, StatsManagement.SESSION_TYPE, true));

        try {
            assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());
            assertTrue(sm.registerSessionMBean(mockSession));
            assertEquals(1, mBeanServer.queryMBeans(name, null).size());

        } finally {
            try {
                mBeanServer.unregisterMBean(name);
            } catch (Exception e) {}
        }

        mockery.assertIsSatisfied();
    }

    @Test
    public void testUnregisterSessionMBean() throws Exception {

        buildStatsKeyExpectations(NORMAL);

        final org.stajistics.session.StatsSession mockSession = mockery.mock(org.stajistics.session.StatsSession.class);

        mockery.checking(new Expectations() {{
            allowing(mockSession).getKey(); will(returnValue(mockKey));
            ignoring(mockSession);
        }});

        StatsManagement sm = getStatsManagement();

        MBeanServer mBeanServer = sm.getMBeanServer();

        ObjectName name = new ObjectName(sm.buildName(mockKey, StatsManagement.CONFIG_TYPE, false));

        try {
            mBeanServer.registerMBean(new StatsSession(mockSession), name);

            assertTrue(sm.unregisterConfigMBean(mockKey));
            assertTrue(mBeanServer.queryMBeans(name, null).isEmpty());

        } finally {
            try {
                mBeanServer.unregisterMBean(name);
            } catch (Exception e) {}
        }

        mockery.assertIsSatisfied();
    }

    private void assertValidObjectName() {

        String name = getStatsManagement().buildName(mockKey, TEST_TYPE, true);

        try {
            new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            fail(e.toString());
        }

        mockery.assertIsSatisfied();
    }

    private void assertInvalidObjectName() {
        String name = getStatsManagement().buildName(mockKey, TEST_TYPE, true);

        try {
            new ObjectName(name);
            fail("Malformed name is accepted: " + name);

        } catch (MalformedObjectNameException e) {
            // Expected
        }

        mockery.assertIsSatisfied();
    }
}

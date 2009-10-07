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
package org.stajistics.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.aop.StatsProxy;

/**
 * 
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class StatsDriverWrapperTest {

    private static final String VENDOR_DRIVER_CLASS_NAME = "java.lang.Object";
    private static final String VENDOR_URL = "jdbc:vendor";
    private static final String STATS_URL = "jdbc:stajistics:vendor statsDelegateDriver=" + 
                                                VENDOR_DRIVER_CLASS_NAME;

    private Mockery mockery;
    private Driver mockDriver;
    private Connection mockConnection;

    private StatsDriverWrapper driver;

    @Before
    public void setUp() throws SQLException {
        mockery = new Mockery();
        mockDriver = mockery.mock(Driver.class);
        mockConnection = mockery.mock(Connection.class);

        mockery.checking(new Expectations() {{
            allowing(mockDriver).acceptsURL(VENDOR_URL);
            will(returnValue(true));

            allowing(mockDriver).connect(with(VENDOR_URL),
                                         with(aNull(Properties.class)));
            will(returnValue(mockConnection));

            allowing(mockDriver).getPropertyInfo(with(aNonNull(String.class)),
                                                 with(aNull(Properties.class)));
            will(returnValue(new DriverPropertyInfo[0]));
        }});

        DriverManager.registerDriver(mockDriver);

        driver = new StatsDriverWrapper();
    }

    @After
    public void tearDown() throws SQLException {
        DriverManager.deregisterDriver(mockDriver);
    }

    @Test
    public void testAcceptsURL() throws SQLException {
        assertTrue(driver.acceptsURL(STATS_URL));
        assertFalse(driver.acceptsURL(VENDOR_URL));
    }

    @Test
    public void testConnectWithDriverClassNotFound() throws SQLException {
        try {
            driver.connect(STATS_URL, null);
        } catch (SQLException sqle) {
            assertTrue(sqle.getCause() instanceof ClassNotFoundException);

            ClassNotFoundException cnfe = (ClassNotFoundException) sqle.getCause();
            assertEquals("jibberish", cnfe.getMessage());
        }
    }

    @Test
    public void testConnectWithValidDriver() throws SQLException {
        Connection con = driver.connect(STATS_URL, null);
        assertTrue(StatsProxy.isProxy(con));
        assertTrue(StatsProxy.unwrap(con) instanceof StatsConnectionWrapper);
    }

    @Test
    public void testPropertyInfo() throws SQLException {
        DriverPropertyInfo[] info = driver.getPropertyInfo(STATS_URL, null);
        assertEquals(1, info.length);
    }
}

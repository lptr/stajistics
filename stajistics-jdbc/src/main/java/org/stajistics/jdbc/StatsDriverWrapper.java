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
package org.stajistics.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.jdbc.management.DefaultStatsDriverWrapperMBean;
import org.stajistics.jdbc.wrapper.StatsConnectionWrapper;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class StatsDriverWrapper implements Driver {

    private static final Logger logger = LoggerFactory.getLogger(StatsDriverWrapper.class);

    private final ConcurrentMap<Key,Entry> dataBaseURLMap =
        new ConcurrentHashMap<Key,Entry>();

    private volatile boolean enabled = true;

    static {
        Driver driver = new StatsDriverWrapper();
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    protected StatsDriverWrapper() {
        registerMBean();
    }

    private void registerMBean() {

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            ObjectName name = new ObjectName("org.stajistics:type=manager,name=StatsDriverWrapper");

            mBeanServer.registerMBean(new DefaultStatsDriverWrapperMBean(this), name);

        } catch (MalformedObjectNameException e) {
            logger.error("", e);

        } catch (InstanceAlreadyExistsException e) {
            logger.warn("", e);

        } catch (MBeanRegistrationException e) {
            logger.error("", e);

        } catch (NotCompliantMBeanException e) {
            logger.error("", e);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return StatsDataBaseURL.isSupported(url);
    }

    protected StatsDataBaseURL createStatsDataBaseURL(final String url) {
        return new StatsDataBaseURL(url);
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        Entry entry = getEntry(url, info);
        StatsDataBaseURL statsDataBaseURL = entry.statsDataBaseURL;
        Driver driver = entry.driver;

        Connection connection = driver.connect(statsDataBaseURL.getDelegateURL(), info);

        if (enabled) {
            connection = new StatsConnectionWrapper(connection, entry.config);

            connection = entry.connectionProxyFactory.createProxy(connection);
        }

        return connection;
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url,
                                                final Properties info) throws SQLException {

        Entry entry = getEntry(url, info);

        DriverPropertyInfo[] delegatePropertyInfo =
            entry.driver.getPropertyInfo(entry.statsDataBaseURL.getDelegateURL(), info);

        DriverPropertyInfo[] propertyInfo = new DriverPropertyInfo[delegatePropertyInfo.length + 1];
        System.arraycopy(delegatePropertyInfo, 0, propertyInfo, 0, delegatePropertyInfo.length);

        propertyInfo[propertyInfo.length - 1] =
            new DriverPropertyInfo(StatsDataBaseURL.Parameters
                                                   .DELEGATE_DRIVER
                                                   .getParameterName(),
                                   entry.statsDataBaseURL.getDelegateDriverClassName());

        return propertyInfo;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    private Driver getDelegateDriver(final StatsDataBaseURL statsURL) throws SQLException {

        Driver delegateDriver;

        try {
            Class.forName(statsURL.getDelegateDriverClassName());
            delegateDriver = DriverManager.getDriver(statsURL.getDelegateURL());

        } catch (SQLException sqle) {
            throw sqle;

        } catch (Exception e) {
            throw new SQLException(e);
        }

        return delegateDriver;
    }

    private Entry getEntry(final String url,
                           final Properties properties)
            throws SQLException {

        final Key key = new Key(url, properties);

        Entry result = this.dataBaseURLMap.get(key);
        if (result == null) {
            result = createEntry(url, properties);

            Entry existingEntry = this.dataBaseURLMap.putIfAbsent(key, result);
            if (existingEntry != null) {
                result = existingEntry;
            } else {
                logger.debug("Parsed URL: {}", result.statsDataBaseURL);
                logger.debug("Initialized delegate driver: {}", result.driver);
            }
        }

        return result;
    }

    private Entry createEntry(final String url,
                              final Properties properties) throws SQLException {

        StatsDataBaseURL statsURL = new StatsDataBaseURL(url);

        Driver delegateDriver = getDelegateDriver(statsURL);

        // TODO: build the config from statsURL
        StatsJDBCConfig config = DefaultStatsJDBCConfig.createWithDefaults();

        ProxyFactory<Connection> connectionProxyFactory = config.getProxyFactory(Connection.class);

        Entry result = new Entry(statsURL,
                                 delegateDriver,
                                 config,
                                 connectionProxyFactory);

        return result;
    }

    /* NESTED CLASSES */

    private static class Key {

        final String url;
        final Properties properties;

        Key(final String url,
            final Properties properties) {

            this.url = url;
            this.properties = properties;
        }

        @Override
        public int hashCode() {
            int h = url.hashCode();
            h = (h * 31) + ((properties == null) ? 0 : properties.hashCode());
            return h;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof Key)) {
                return false;
            }

            Key other = (Key)obj;

            if (!url.equals(other.url)) {
                return false;
            }

            if (properties == null) {
                return other.properties == null;
            }

            if (other.properties == null) {
                return false;
            }

            if (!properties.equals(other.properties)) {
                return false;
            }

            return true;
        }

    }

    private static class Entry {

        final StatsDataBaseURL statsDataBaseURL;
        final Driver driver;
        final StatsJDBCConfig config;
        final ProxyFactory<Connection> connectionProxyFactory;

        public Entry(final StatsDataBaseURL statsDataBaseURL,
                     final Driver driver,
                     final StatsJDBCConfig config,
                     final ProxyFactory<Connection> connectionProxyFactory) {
            this.statsDataBaseURL = statsDataBaseURL;
            this.driver = driver;
            this.config = config;
            this.connectionProxyFactory = connectionProxyFactory;
        }

    }
}

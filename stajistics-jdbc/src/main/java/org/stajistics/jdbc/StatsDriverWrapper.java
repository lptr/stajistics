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

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.jdbc.management.DefaultStatsDriverWrapperMBean;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsDriverWrapper implements Driver {

    private static final Logger logger = LoggerFactory.getLogger(StatsDriverWrapper.class);

    private final ConcurrentMap<String,StatsDataBaseURL> dataBaseURLMap = 
        new ConcurrentHashMap<String,StatsDataBaseURL>();

    private volatile boolean initialized = false;
    private volatile boolean enabled = true;

    private ProxyFactory<Connection> connectionProxyFactory; 

    public StatsDriverWrapper() {
        connectionProxyFactory = new ConnectionProxyFactory();

    }
 
    private void registerMBean() {

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        try {
            ObjectName name = new ObjectName("org.stajistics.jdbc:type=manager,name=StatsDriverWrapper");

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

    private StatsDataBaseURL getStatsDataBaseURL(final String url) {

        StatsDataBaseURL statsURL = dataBaseURLMap.get(url);
        if (statsURL == null) {
            statsURL = createStatsDataBaseURL(url);
            if (dataBaseURLMap.putIfAbsent(url, statsURL) == null) {
                logger.info("Wrapping Driver: {}, delegate URL: {}",
                            statsURL.getDelegateDriverClassName(),
                            statsURL.getDelegateURL());
            }
        }

        return statsURL;
    }

    protected StatsDataBaseURL createStatsDataBaseURL(final String url) {
        return new StatsDataBaseURL(url);
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        StatsDataBaseURL statsURL = getStatsDataBaseURL(url);
        Driver delegateDriver = getDelegateDriver(statsURL);

        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    logger.info("Initializing StatsDriverWrapper from URL: {}", url);

                    Map<String,String> params = statsURL.getParameters();

                    boolean mgmtEnabled = isTrue(params.get(StatsDataBaseURL.Parameters
                                                                            .MANAGEMENT_ENABLED),
                                                 true);
                    if (mgmtEnabled) {
                        registerMBean();
                    }

                    logger.info("Management enabled: {} ", mgmtEnabled);

                    enabled = isTrue(params.get(StatsDataBaseURL.Parameters
                                                                .DRIVER_WRAPPER_ENABLED),
                                     true);
                    
                    logger.info("{} enabled: {}",
                                getClass(),
                                enabled);

                    initialized = true;
                }
            }
        }

        Connection connection = delegateDriver.connect(statsURL.getDelegateURL(), info);

        if (enabled) {
            connection = new StatsConnectionWrapper(connection);

            if (isTrue(statsURL.getParameters()
                    .get(StatsDataBaseURL.Parameters
                                         .PROXY_ENABLED), true)) {

                connection = connectionProxyFactory.createProxy(connection);
            }
        }

        return connection;
    }

    private boolean isTrue(final String str,
                           final boolean defaultValue) {
        boolean value = defaultValue;

        if (str != null) {
            value = Boolean.parseBoolean(str);
        }

        return value;
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

        StatsDataBaseURL statsURL = new StatsDataBaseURL(url);
        Driver delegateDriver = getDelegateDriver(statsURL);

        DriverPropertyInfo[] delegatePropertyInfo = 
            delegateDriver.getPropertyInfo(statsURL.getDelegateURL(), info);

        DriverPropertyInfo[] propertyInfo = new DriverPropertyInfo[delegatePropertyInfo.length + 1];
        System.arraycopy(delegatePropertyInfo, 0, propertyInfo, 0, delegatePropertyInfo.length);

        propertyInfo[propertyInfo.length - 1] = 
            new DriverPropertyInfo(StatsDataBaseURL.Parameters
                                                   .DELEGATE_DRIVER
                                                   .getParameterName(),
                                   statsURL.getDelegateDriverClassName());

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
}

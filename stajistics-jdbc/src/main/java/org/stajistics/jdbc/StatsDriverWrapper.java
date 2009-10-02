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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsDriverWrapper implements Driver {

    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return StatsDataBaseURL.isSupported(url);
    }

    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        StatsDataBaseURL statsURL = new StatsDataBaseURL(url);
        Driver delegateDriver = getDelegateDriver(statsURL);

        Connection connection = delegateDriver.connect(statsURL.getDelegateURL(), info);
        connection = new StatsConnectionWrapper(connection);

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

        return delegatePropertyInfo;
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

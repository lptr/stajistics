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

import org.stajistics.jdbc.decorator.AbstractWrapper;
import org.stajistics.jdbc.wrapper.StatsConnectionWrapper;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class StatsDataSourceWrapper extends AbstractWrapper implements DataSource {

    private DataSource delegate;
    private StatsJDBCConfig config;

    public StatsDataSourceWrapper() {}

    public StatsDataSourceWrapper(final DataSource delegate,
                                  final StatsJDBCConfig config) {
        setDelegate(delegate);
        setConfig(config);
    }

    public void setDelegate(final DataSource delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    public StatsJDBCConfig getConfig() {
        return config;
    }

    public void setConfig(final StatsJDBCConfig config) {
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.config = config;
    }

    @Override
    protected final DataSource delegate() {
        if (delegate == null) {
            throw new IllegalStateException("no delegate supplied");
        }

        return delegate;
    }

    protected StatsJDBCConfig requireConfig() {
        if (config == null) {
            throw new IllegalStateException("no config supplied");
        }

        return config;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = new StatsConnectionWrapper(delegate().getConnection(),
                                                           requireConfig());

        connection = config.getProxyFactory(Connection.class)
                           .createProxy(connection);

        return connection;
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        Connection connection = new StatsConnectionWrapper(delegate().getConnection(username, password),
                                                           requireConfig());

        connection = config.getProxyFactory(Connection.class)
                           .createProxy(connection);

        return connection;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate().getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate().getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate().setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate().setLoginTimeout(seconds);
    }

}

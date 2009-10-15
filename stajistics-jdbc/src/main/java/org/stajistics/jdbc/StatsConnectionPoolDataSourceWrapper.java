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

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.stajistics.jdbc.wrapper.StatsPooledConnectionWrapper;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class StatsConnectionPoolDataSourceWrapper implements ConnectionPoolDataSource {

    private ConnectionPoolDataSource delegate;
    private StatsJDBCConfig config;

    public StatsConnectionPoolDataSourceWrapper() {}

    public StatsConnectionPoolDataSourceWrapper(final ConnectionPoolDataSource delegate,
                                                final StatsJDBCConfig config) {
        setDelegate(delegate);
        setConfig(config);
    }

    public ConnectionPoolDataSource getDelegate() {
        return delegate;
    }

    public void setDelegate(final ConnectionPoolDataSource delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    protected ConnectionPoolDataSource delegate() {
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

    public StatsJDBCConfig getConfig() {
        return config;
    }

    public void setConfig(final StatsJDBCConfig config) {
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.config = config;
    }

    public StatsConnectionPoolDataSourceWrapper(final ConnectionPoolDataSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        PooledConnection pc = new StatsPooledConnectionWrapper(delegate().getPooledConnection(), 
                                                               requireConfig());

        return pc;
    }

    @Override
    public PooledConnection getPooledConnection(final String user, 
                                                final String password)
            throws SQLException {
        PooledConnection pc = new StatsPooledConnectionWrapper(delegate().getPooledConnection(user, password), 
                                                               requireConfig());

        return pc;
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

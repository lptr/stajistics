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

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;

import org.stajistics.jdbc.wrapper.StatsXAConnectionWrapper;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class StatsXADataSourceWrapper implements XADataSource {

    private XADataSource delegate;
    private StatsJDBCConfig config;

    public StatsXADataSourceWrapper() {}

    public StatsXADataSourceWrapper(final XADataSource delegate,
                                    final StatsJDBCConfig config) {
        setDelegate(delegate);
        setConfig(config);
    }

    public XADataSource getDelegate() {
        return delegate;
    }

    public void setDelegate(final XADataSource delegate) {
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

    protected XADataSource delegate() {
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
    
    public int getLoginTimeout() throws SQLException {
        return delegate().getLoginTimeout();
    }

    public PrintWriter getLogWriter() throws SQLException {
        return delegate().getLogWriter();
    }

    public XAConnection getXAConnection() throws SQLException {
        XAConnection xac = new StatsXAConnectionWrapper(delegate().getXAConnection(),
                                                        requireConfig());

        return xac;
    }

    public XAConnection getXAConnection(final String user, 
                                        final String password)
            throws SQLException {
        XAConnection xac = new StatsXAConnectionWrapper(delegate().getXAConnection(user, password),
                                                        requireConfig());

        return xac;
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        delegate().setLoginTimeout(seconds);
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate().setLogWriter(out);
    }
    
}

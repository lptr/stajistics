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
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsDataSourceWrapper implements DataSource {

    private final DataSource delegate;

    public StatsDataSourceWrapper(final DataSource delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new StatsConnectionWrapper(delegate.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password)
            throws SQLException {
        return new StatsConnectionWrapper(delegate.getConnection(username, password));
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return delegate.getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return delegate.getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        delegate.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        delegate.setLoginTimeout(seconds);
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        if (iface.isAssignableFrom(delegate.getClass())) {
            return true;
        }

        return delegate.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(delegate.getClass())) {
            return iface.cast(delegate);
        }

        return delegate.unwrap(iface);
    }
}

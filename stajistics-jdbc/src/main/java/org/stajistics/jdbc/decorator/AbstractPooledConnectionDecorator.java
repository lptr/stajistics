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
package org.stajistics.jdbc.decorator;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

/**
 * 
 * @author The Stajistics Project
 *
 */
public abstract class AbstractPooledConnectionDecorator implements PooledConnection {

    private final PooledConnection delegate;

    public AbstractPooledConnectionDecorator(final PooledConnection delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    protected final PooledConnection delegate() {
        return delegate;
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        delegate().addConnectionEventListener(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        delegate().addStatementEventListener(listener);
    }

    @Override
    public void close() throws SQLException {
        delegate().close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return delegate().getConnection();
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        delegate().removeConnectionEventListener(listener);
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        delegate().removeStatementEventListener(listener);
    }

}

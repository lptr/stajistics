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
package org.stajistics.jdbc.decorator;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class AbstractXAConnectionDecorator implements XAConnection {

    private final XAConnection delegate;

    public AbstractXAConnectionDecorator(final XAConnection delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    protected XAConnection delegate() {
        return delegate;
    }

    public void addConnectionEventListener(ConnectionEventListener listener) {
        delegate().addConnectionEventListener(listener);
    }

    public void addStatementEventListener(StatementEventListener listener) {
        delegate().addStatementEventListener(listener);
    }

    public void close() throws SQLException {
        delegate().close();
    }

    public Connection getConnection() throws SQLException {
        return delegate().getConnection();
    }

    public XAResource getXAResource() throws SQLException {
        return delegate().getXAResource();
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        delegate().removeConnectionEventListener(listener);
    }

    public void removeStatementEventListener(StatementEventListener listener) {
        delegate().removeStatementEventListener(listener);
    }

}

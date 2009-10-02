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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsConnectionWrapper implements Connection {

    private final Connection delegate;

    public StatsConnectionWrapper(final Connection delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;
    }

    @Override
    public void clearWarnings() throws SQLException {
        delegate.clearWarnings();
    }

    @Override
    public void close() throws SQLException {
        delegate.close();
    }

    @Override
    public void commit() throws SQLException {
        delegate.commit();
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return delegate.createArrayOf(typeName, elements);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return delegate.createBlob();
    }

    @Override
    public Clob createClob() throws SQLException {
        return delegate.createClob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return delegate.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return delegate.createSQLXML();
    }

    @Override
    public Statement createStatement() throws SQLException {
        return delegate.createStatement();
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) 
            throws SQLException {
        return delegate.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public Statement createStatement(final int resultSetType, 
                                     final int resultSetConcurrency, 
                                     final int resultSetHoldability) 
            throws SQLException {
        return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return delegate.createStruct(typeName, attributes);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return delegate.getAutoCommit();
    }

    @Override
    public String getCatalog() throws SQLException {
        return delegate.getCatalog();
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return delegate.getClientInfo();
    }

    @Override
    public String getClientInfo(final String name) throws SQLException {
        return delegate.getClientInfo(name);
    }

    @Override
    public int getHoldability() throws SQLException {
        return delegate.getHoldability();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return delegate.getMetaData();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return delegate.getTransactionIsolation();
    }

    @Override
    public Map<String,Class<?>> getTypeMap() throws SQLException {
        return delegate.getTypeMap();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return delegate.getWarnings();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return delegate.isReadOnly();
    }

    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return delegate.isValid(timeout);
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return delegate.nativeSQL(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return delegate.prepareCall(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) 
            throws SQLException {
        return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql,
                                         int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new StatsPreparedStatementWrapper(delegate.prepareStatement(sql), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return new StatsPreparedStatementWrapper(delegate.prepareStatement(sql, autoGeneratedKeys), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return new StatsPreparedStatementWrapper(delegate.prepareStatement(sql, columnIndexes), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return new StatsPreparedStatementWrapper(delegate.prepareStatement(sql, columnNames), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) 
            throws SQLException {
        return new StatsPreparedStatementWrapper(delegate.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return new StatsPreparedStatementWrapper(delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        delegate.releaseSavepoint(savepoint);
    }

    @Override
    public void rollback() throws SQLException {
        delegate.rollback();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        delegate.rollback(savepoint);
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        delegate.setAutoCommit(autoCommit);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        delegate.setCatalog(catalog);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        delegate.setClientInfo(properties);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        delegate.setClientInfo(name, value);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        delegate.setHoldability(holdability);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        delegate.setReadOnly(readOnly);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return delegate.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return delegate.setSavepoint(name);
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        delegate.setTransactionIsolation(level);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        delegate.setTypeMap(map);
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

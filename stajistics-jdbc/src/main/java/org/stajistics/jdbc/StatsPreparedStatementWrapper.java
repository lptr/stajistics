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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
public class StatsPreparedStatementWrapper implements PreparedStatement {

    private final PreparedStatement delegate;
    private final String sql;

    public StatsPreparedStatementWrapper(final PreparedStatement delegate,
                                         final String sql) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }
        if (sql == null) {
            throw new NullPointerException("sql");
        }

        this.delegate = delegate;
        this.sql = sql;
    }

    public String getSQL() {
        return sql;
    }

    @Override
    public void addBatch() throws SQLException {
        delegate.addBatch();
    }

    @Override
    public void clearParameters() throws SQLException {
        delegate.clearParameters();
    }

    @Override
    public boolean execute() throws SQLException {
        return delegate.execute();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return delegate.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return delegate.executeUpdate();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader,
            long length) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value,
            long length) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNString(int parameterIndex, String value)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType,
            int scaleOrLength) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length)
            throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void addBatch(String sql) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearBatch() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearWarnings() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames)
            throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Connection getConnection() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFetchSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxRows() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isClosed() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub

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

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
package org.stajistics.jdbc.wrapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.stajistics.Stats;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.jdbc.StatsJDBCConfig;
import org.stajistics.jdbc.decorator.AbstractStatementDecorator;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 * @author The Stajistics Project
 *
 */
public class StatsStatementWrapper extends AbstractStatementDecorator {

    private static StatsFactory statsFactory = Stats.getFactory(StatsStatementWrapper.class);

    private final StatsJDBCConfig config;

    private final Connection connection;
    private final List<String> batchSQL;

    private final ProxyFactory<ResultSet> resultSetProxyFactory;

    private final SpanTracker openClosedTracker;

    public StatsStatementWrapper(final Statement delegate,
                                 final Connection connection,
                                 final StatsJDBCConfig config) {
        super(delegate);

        if (connection == null) {
            throw new NullPointerException("connection");
        }
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.connection = connection;
        this.config = config;

        batchSQL = new LinkedList<String>();

        this.resultSetProxyFactory = config.getProxyFactory(ResultSet.class);

        StatsKey openClosedKey = statsFactory.buildKey(Statement.class.getName())
                                             .withNameSuffix("open")
                                             .newKey();

        openClosedTracker = statsFactory.track(openClosedKey);
    }

    private void handleSQL(final String sql) {
        config.getSQLAnalyzer()
              .analyzeSQL(sql);
    }

    private void handleSQL(final List<String> batchSQL) {
        config.getSQLAnalyzer()
              .analyzeSQL(batchSQL);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        try {
            super.close();
        } finally {
            openClosedTracker.commit();
        }
    }

    @Override
    public boolean execute(final String sql) throws SQLException {
        boolean result = delegate().execute(sql);

        handleSQL(sql);

        return result;
    }

    @Override
    public boolean execute(final String sql,
                           final int autoGeneratedKeys) throws SQLException {
        boolean result = delegate().execute(sql, autoGeneratedKeys);

        handleSQL(sql);

        return result;
    }

    @Override
    public boolean execute(final String sql,
                           final int[] columnIndexes) throws SQLException {
        boolean result = delegate().execute(sql, columnIndexes);

        handleSQL(sql);

        return result;
    }

    @Override
    public boolean execute(final String sql,
                           final String[] columnNames) throws SQLException {
        boolean result = execute(sql, columnNames);

        handleSQL(sql);

        return result;
    }

    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        ResultSet rs = new StatsResultSetWrapper(delegate().executeQuery(sql), config);

        handleSQL(sql);

        rs = resultSetProxyFactory.createProxy(rs);

        return rs;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        ResultSet rs = new StatsResultSetWrapper(delegate().getGeneratedKeys(), config);

        rs = resultSetProxyFactory.createProxy(rs);

        return rs;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        ResultSet rs = new StatsResultSetWrapper(delegate().getResultSet(), config);

        rs = resultSetProxyFactory.createProxy(rs);

        return rs;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        handleSQL(sql);
        return delegate().executeUpdate(sql);
    }

    @Override
    public int executeUpdate(final String sql,
                             final int autoGeneratedKeys) throws SQLException {
        int result = delegate().executeUpdate(sql, autoGeneratedKeys);

        handleSQL(sql);

        return result;
    }

    @Override
    public int executeUpdate(final String sql,
                             final int[] columnIndexes) throws SQLException {
        int result = delegate().executeUpdate(sql, columnIndexes);

        handleSQL(sql);

        return result;
    }

    @Override
    public int executeUpdate(final String sql,
                             final String[] columnNames) throws SQLException {
        int result = delegate().executeUpdate(sql, columnNames);

        handleSQL(sql);

        return result;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        int[] result = delegate().executeBatch();

        handleSQL(batchSQL);

        return result;
    }

    @Override
    public void addBatch(final String sql) throws SQLException {
        delegate().addBatch(sql);
        batchSQL.add(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        delegate().clearBatch();
        batchSQL.clear();
    }
}

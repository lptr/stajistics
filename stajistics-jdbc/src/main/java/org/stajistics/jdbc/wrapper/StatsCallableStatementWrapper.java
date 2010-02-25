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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.jdbc.StatsJDBCConfig;
import org.stajistics.jdbc.decorator.AbstractCallableStatementDecorator;
import org.stajistics.tracker.span.SpanTracker;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class StatsCallableStatementWrapper extends AbstractCallableStatementDecorator {

    private final StatsJDBCConfig config;

    private final Connection connection;
    private final String sql;
    private final List<String> batchSQL;

    private final SpanTracker openClosedTracker;

    private final ProxyFactory<ResultSet> resultSetProxyFactory;

    public StatsCallableStatementWrapper(final CallableStatement delegate,
                                         final Connection connection,
                                         final String sql,
                                         final StatsJDBCConfig config) {
        super(delegate);

        if (connection == null) {
            throw new NullPointerException("connection");
        }
        if (sql == null) {
            throw new NullPointerException("sql");
        }
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.connection = connection;
        this.sql = sql;
        this.config = config;

        batchSQL = new LinkedList<String>();

        resultSetProxyFactory = config.getProxyFactory(ResultSet.class);

        StatsKey openClosedKey = Stats.buildKey(CallableStatement.class.getName())
                                      .withNameSuffix("open")
                                      .newKey();

        openClosedTracker = Stats.track(openClosedKey);
    }

    public String getSQL() {
        return sql;
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
    public void close() throws SQLException {
        try {
            delegate().close();
        } finally {
            openClosedTracker.commit();
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        ResultSet rs = new StatsResultSetWrapper(delegate().executeQuery(), config);

        handleSQL(this.sql);

        rs = resultSetProxyFactory.createProxy(rs);

        return rs;
    }

    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        ResultSet rs = new StatsResultSetWrapper(delegate().executeQuery(sql), config);

        handleSQL(sql);

        rs = resultSetProxyFactory.createProxy(rs);

        return rs;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        int[] result = delegate().executeBatch();

        handleSQL(batchSQL);

        return result;
    }

    @Override
    public void addBatch() throws SQLException {
        delegate().addBatch();
        batchSQL.add(this.sql);
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

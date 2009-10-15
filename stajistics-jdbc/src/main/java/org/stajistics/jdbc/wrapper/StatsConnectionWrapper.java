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
package org.stajistics.jdbc.wrapper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.jdbc.StatsJDBCConfig;
import org.stajistics.jdbc.decorator.AbstractConnectionDecorator;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsConnectionWrapper extends AbstractConnectionDecorator {

    private final StatsJDBCConfig config;

    private final ProxyFactory<CallableStatement> callableStatementProxyFactory;
    private final ProxyFactory<PreparedStatement> preparedStatementProxyFactory;
    private final ProxyFactory<Statement> statementProxyFactory;

    private final StatsTracker openClosedTracker;

    public StatsConnectionWrapper(final Connection delegate,
                                  final StatsJDBCConfig config) {
        super(delegate);

        if (config == null) {
            throw new NullPointerException("config");
        }

        this.config = config; 

        callableStatementProxyFactory = config.getProxyFactory(CallableStatement.class);
        preparedStatementProxyFactory = config.getProxyFactory(PreparedStatement.class);
        statementProxyFactory = config.getProxyFactory(Statement.class);

        StatsKey openClosedKey = Stats.buildKey(Connection.class.getName())
                                      .withNameSuffix("open")
                                      .newKey();

        openClosedTracker = Stats.track(openClosedKey);
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
    public Statement createStatement() throws SQLException {
        Statement s = new StatsStatementWrapper(delegate().createStatement(), this, config);

        s = statementProxyFactory.createProxy(s);

        return s;
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) 
            throws SQLException {
        Statement s = new StatsStatementWrapper(delegate().createStatement(resultSetType, resultSetConcurrency), this, config);

        s = statementProxyFactory.createProxy(s);

        return s;
    }

    @Override
    public Statement createStatement(final int resultSetType, 
                                     final int resultSetConcurrency, 
                                     final int resultSetHoldability) 
            throws SQLException {
        Statement s = new StatsStatementWrapper(delegate().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability), this, config);

        s = statementProxyFactory.createProxy(s);

        return s;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        CallableStatement cs = new StatsCallableStatementWrapper(delegate().prepareCall(sql), this, sql, config);

        cs = callableStatementProxyFactory.createProxy(cs);

        return cs;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) 
            throws SQLException {
        CallableStatement cs = new StatsCallableStatementWrapper(delegate().prepareCall(sql, resultSetType, resultSetConcurrency), this, sql, config);

        cs = callableStatementProxyFactory.createProxy(cs);

        return cs;
    }

    @Override
    public CallableStatement prepareCall(String sql,
                                         int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        CallableStatement cs = new StatsCallableStatementWrapper(delegate().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), this, sql, config);

        cs = callableStatementProxyFactory.createProxy(cs);

        return cs;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        PreparedStatement ps = new StatsPreparedStatementWrapper(delegate().prepareStatement(sql), this, sql, config);

        ps = preparedStatementProxyFactory.createProxy(ps);

        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        PreparedStatement ps = new StatsPreparedStatementWrapper(delegate().prepareStatement(sql, autoGeneratedKeys), this, sql, config);

        ps = preparedStatementProxyFactory.createProxy(ps);

        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        PreparedStatement ps = new StatsPreparedStatementWrapper(delegate().prepareStatement(sql, columnIndexes), this, sql, config);

        ps = preparedStatementProxyFactory.createProxy(ps);

        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        PreparedStatement ps = new StatsPreparedStatementWrapper(delegate().prepareStatement(sql, columnNames), this, sql, config);

        ps = preparedStatementProxyFactory.createProxy(ps);

        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) 
            throws SQLException {
        PreparedStatement ps = new StatsPreparedStatementWrapper(delegate().prepareStatement(sql, resultSetType, resultSetConcurrency), this, sql, config);

        ps = preparedStatementProxyFactory.createProxy(ps);

        return ps;
    }

    @Override
    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        PreparedStatement ps = new StatsPreparedStatementWrapper(delegate().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), this, sql, config);

        ps = preparedStatementProxyFactory.createProxy(ps);

        return ps;
    }

}

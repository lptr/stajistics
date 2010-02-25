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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import org.stajistics.aop.ProxyFactory;
import org.stajistics.jdbc.sql.SQLAnalyzer;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultStatsJDBCConfig implements StatsJDBCConfig {

    private SQLAnalyzer sqlAnalyzer;

    @SuppressWarnings("unchecked")
    private Map<Class,ProxyFactory> proxyFactoryMap;

    public DefaultStatsJDBCConfig() {
        this.sqlAnalyzer = SQLAnalyzer.NoOp.instance();
        this.proxyFactoryMap = Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    public DefaultStatsJDBCConfig(final SQLAnalyzer sqlAnalyzer,
                                  final Map<Class,ProxyFactory> proxyFactoryMap) {
        setSQLAnalyzer(sqlAnalyzer);
        setProxyFactoryMap(proxyFactoryMap);
    }

    public static DefaultStatsJDBCConfig createWithDefaults() {

        SQLAnalyzer sqlAnalyzer = SQLAnalyzer.NoOp.instance();

        @SuppressWarnings("unchecked")
        Map<Class,ProxyFactory> proxyFactoryMap = new HashMap<Class,ProxyFactory>();

        proxyFactoryMap.put(CallableStatement.class, 
                            DefaultJDBCProxyFactory.createWithDefaults(CallableStatement.class));
        proxyFactoryMap.put(Connection.class,
                            DefaultJDBCProxyFactory.createWithDefaults(Connection.class));
        proxyFactoryMap.put(PooledConnection.class,
                            DefaultJDBCProxyFactory.createWithDefaults(PooledConnection.class));
        proxyFactoryMap.put(PreparedStatement.class,
                            DefaultJDBCProxyFactory.createWithDefaults(PreparedStatement.class));
        proxyFactoryMap.put(ResultSet.class,
                            DefaultJDBCProxyFactory.createWithDefaults(ResultSet.class));
        proxyFactoryMap.put(Statement.class,
                            DefaultJDBCProxyFactory.createWithDefaults(Statement.class));
        proxyFactoryMap.put(XAConnection.class,
                            DefaultJDBCProxyFactory.createWithDefaults(XAConnection.class));
        proxyFactoryMap.put(XAResource.class,
                            DefaultJDBCProxyFactory.createWithDefaults(XAResource.class));

        DefaultStatsJDBCConfig config = new DefaultStatsJDBCConfig(sqlAnalyzer, proxyFactoryMap);

        return config;
    }

    public void setSQLAnalyzer(final SQLAnalyzer sqlAnalyzer) {
        if (sqlAnalyzer == null) {
            this.sqlAnalyzer = SQLAnalyzer.NoOp.instance();
        } else {
            this.sqlAnalyzer = sqlAnalyzer;
        }
    }

    @Override
    public SQLAnalyzer getSQLAnalyzer() {
        return sqlAnalyzer;
    }

    @SuppressWarnings("unchecked")
    public void setProxyFactoryMap(final Map<Class,ProxyFactory> proxyFactoryMap) {
        if (proxyFactoryMap == null) {
            this.proxyFactoryMap = Collections.emptyMap();
        } else {
            this.proxyFactoryMap = new HashMap<Class,ProxyFactory>(proxyFactoryMap);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<Class,ProxyFactory> getProxyFactoryMap() {
        return proxyFactoryMap;
    }

    @Override
    public <T> ProxyFactory<T> getProxyFactory(final Class<T> type) {

        @SuppressWarnings("unchecked")
        ProxyFactory<T> proxyFactory = (ProxyFactory<T>) proxyFactoryMap.get(type);
        if (proxyFactory == null) {
            proxyFactory = ProxyFactory.NoOp.instance();
        }

        return proxyFactory;
    }

}

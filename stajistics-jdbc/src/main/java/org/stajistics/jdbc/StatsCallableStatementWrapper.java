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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.stajistics.jdbc.decorator.AbstractCallableStatementDecorator;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class StatsCallableStatementWrapper extends AbstractCallableStatementDecorator {

    private final Connection connection;
    private final String sql;

    public StatsCallableStatementWrapper(final CallableStatement delegate,
                                         final Connection connection,
                                         final String sql) {
        super(delegate);

        if (connection == null) {
            throw new NullPointerException("connection");
        }
        if (sql == null) {
            throw new NullPointerException("sql");
        }

        this.connection = connection;
        this.sql = sql;
    }


    @Override
    public ResultSet executeQuery() throws SQLException {
        return new StatsResultSetWrapper(delegate().executeQuery());
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

}

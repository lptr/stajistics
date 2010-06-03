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

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.jdbc.StatsJDBCConfig;
import org.stajistics.jdbc.decorator.AbstractPooledConnectionDecorator;
import org.stajistics.tracker.span.SpanTracker;

import javax.sql.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author The Stajistics Project
 *
 */
public class StatsPooledConnectionWrapper extends AbstractPooledConnectionDecorator {

    private final StatsJDBCConfig config;

    private final SpanTracker openClosedTracker;
    //private final StatsTracker checkInCheckOutTracker;

    public StatsPooledConnectionWrapper(final PooledConnection delegate,
                                        final StatsJDBCConfig config) {
        super(delegate);

        if (config == null) {
            throw new NullPointerException("config");
        }

        this.config = config;

        StatsKey openClosedKey = Stats.buildKey(PooledConnection.class.getName())
                                      .withNameSuffix("open")
                                      .newKey();

        StatsKey checkInCheckOutKey = Stats.buildKey(PooledConnection.class.getName())
                                           .withNameSuffix("checkedOut")
                                           .newKey();

        openClosedTracker = Stats.track(openClosedKey);
        //checkInCheckOutTracker = null;
    }

    @Override
    public void close() throws SQLException {
        try {
            delegate().close();
        } finally {

        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection con = new StatsConnectionWrapper(delegate().getConnection(), config);

        con = config.getProxyFactory(Connection.class)
                    .createProxy(con);

        return con;
    }

    /* NESTED CLASSES */

    private class ConnectionEventHandler implements ConnectionEventListener {

        @Override
        public void connectionClosed(final ConnectionEvent event) {

        }

        @Override
        public void connectionErrorOccurred(final ConnectionEvent event) {

        }
    }

    private class StatementEventHandler implements StatementEventListener {

        @Override
        public void statementClosed(final StatementEvent event) {

        }

        @Override
        public void statementErrorOccurred(final StatementEvent event) {

        }

    }

}

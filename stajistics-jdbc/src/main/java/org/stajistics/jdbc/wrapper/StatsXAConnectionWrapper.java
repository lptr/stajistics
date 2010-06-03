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

import org.stajistics.jdbc.StatsJDBCConfig;
import org.stajistics.jdbc.decorator.AbstractXAConnectionDecorator;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author The Stajistics Project
 *
 */
public class StatsXAConnectionWrapper extends AbstractXAConnectionDecorator {

    private StatsJDBCConfig config;

    public StatsXAConnectionWrapper(final XAConnection delegate,
                                    final StatsJDBCConfig config) {
        super(delegate);

        if (config == null) {
            throw new NullPointerException("config");
        }

        this.config = config;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection con = new StatsConnectionWrapper(delegate().getConnection(), config);

        con = config.getProxyFactory(Connection.class)
                    .createProxy(con);

        return con;
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        XAResource xar = new StatsXAResourceWrapper(delegate().getXAResource());

        xar = config.getProxyFactory(XAResource.class)
                    .createProxy(xar);

        return xar;
    }
}

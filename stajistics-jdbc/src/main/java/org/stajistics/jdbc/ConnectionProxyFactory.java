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

import java.sql.Connection;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.aop.StatsProxy;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class ConnectionProxyFactory implements ProxyFactory<Connection> {

    
    private final StatsKey proxyKey;

    public ConnectionProxyFactory() {
        this(JDBCStatsKeyConstants.CONNECTION
                                  .buildCopy()
                                  .withNameSuffix("proxy")
                                  .newKey());
    }

    public ConnectionProxyFactory(final StatsKey proxyKey) {
        if (proxyKey == null) {
            throw new NullPointerException("proxyKey");
        }

        this.proxyKey = proxyKey;
    }

    @Override
    public Connection createProxy(final Connection instance) {
        return StatsProxy.wrap(Stats.getManager(), 
                               proxyKey, 
                               instance, 
                               Connection.class);
    }

}

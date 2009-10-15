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

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.aop.StatsProxy;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultJDBCProxyFactory<T> implements ProxyFactory<T> {

    private final StatsManager manager;
    private final Class<T> proxyClass;
    private final StatsKey proxyKey;

    public DefaultJDBCProxyFactory(final StatsManager manager,
                                   final Class<T> proxyClass,
                                   final StatsKey proxyKey) {
        if (proxyKey == null) {
            throw new NullPointerException("proxyKey");
        }
        if (proxyClass == null) {
            throw new NullPointerException("proxyClass");
        }

        if (manager == null) {
            this.manager = Stats.getManager();
        } else {
            this.manager = manager;
        }

        this.proxyClass = proxyClass;
        this.proxyKey = proxyKey;
    }

    public static <T> DefaultJDBCProxyFactory<T> createWithDefaults(final Class<T> proxyClass) {
        if (proxyClass == null) {
            throw new NullPointerException("proxyClass");
        }

        StatsManager manager = Stats.getManager();
        StatsKey key = manager.getKeyFactory()
                              .createKeyBuilder(proxyClass.getName())
                              .withNameSuffix("proxy")
                              .newKey();

        DefaultJDBCProxyFactory<T> factory = new DefaultJDBCProxyFactory<T>(manager,
                                                                            proxyClass,
                                                                            key);

        return factory;
    }

    @Override
    public T createProxy(final T instance) {
        return StatsProxy.wrap(manager, 
                               proxyKey, 
                               instance, 
                               proxyClass);
    }

}

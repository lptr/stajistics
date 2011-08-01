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

import org.stajistics.Stats;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.aop.ProxyFactory;
import org.stajistics.aop.StatsProxy;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultJDBCProxyFactory<T> implements ProxyFactory<T> {

    private final StatsFactory factory;
    private final Class<T> proxyClass;
    private final StatsKey proxyKey;

    public DefaultJDBCProxyFactory(final StatsFactory factory,
                                   final Class<T> proxyClass,
                                   final StatsKey proxyKey) {
        if (proxyKey == null) {
            throw new NullPointerException("proxyKey");
        }
        if (proxyClass == null) {
            throw new NullPointerException("proxyClass");
        }

        if (factory == null) {
            this.factory = Stats.getFactory(getClass());
        } else {
            this.factory = factory;
        }

        this.proxyClass = proxyClass;
        this.proxyKey = proxyKey;
    }

    public static <T> DefaultJDBCProxyFactory<T> createWithDefaults(final Class<T> proxyClass) {
        if (proxyClass == null) {
            throw new NullPointerException("proxyClass");
        }

        StatsFactory factory = Stats.getFactory(DefaultJDBCProxyFactory.class);
        StatsKey key = factory.buildKey(proxyClass.getName())
                              .withNameSuffix("proxy")
                              .newKey();

        DefaultJDBCProxyFactory<T> proxyFactory = 
            new DefaultJDBCProxyFactory<T>(factory, proxyClass, key);

        return proxyFactory;
    }

    @Override
    public T createProxy(final T instance) {
        return StatsProxy.wrap(factory, 
                               proxyKey, 
                               instance, 
                               proxyClass);
    }

}

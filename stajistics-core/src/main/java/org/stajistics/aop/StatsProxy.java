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
package org.stajistics.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsProxy implements InvocationHandler {

    private static final Method EQUALS_METHOD;
    static {
        try {
            EQUALS_METHOD = Object.class.getMethod("equals",
                                                   new Class[] { Object.class });
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final StatsManager statsManager;
    private final StatsKey key;
    private final Object target;

    private StatsProxy(final StatsManager statsManager,
                       final StatsKey key,
                       final Object target) {

        if (statsManager == null) {
            this.statsManager = Stats.getManager();
        } else {
            this.statsManager = statsManager;
        }

        if (key == null) {
            throw new NullPointerException("key");
        }
        if (target == null) {
            throw new NullPointerException("target");
        }

        this.key = key;
        this.target = target;
    }

    /**
     * The type which receives the result of this method call must be an interface.
     * 
     * @param <T>
     * @param key
     * @param target
     * @return 
     */
    @SuppressWarnings("unchecked")
    public static <T> T wrap(final StatsManager statsManager,
                             final StatsKey key,
                             final T target) {
        Class<? super T>[] ifaces = (Class<? super T>[])target.getClass()
                                                              .getInterfaces();
        return wrap(statsManager, key, target, ifaces);
    }

    @SuppressWarnings("unchecked")
    public static <T,U extends T> T wrap(final StatsManager statsManager,
                                         final StatsKey key,
                                         final U target,
                                         final Class<T> iface) {
        return wrap(statsManager, key, target, (Class<T>[])new Class[] { iface });
    }

    @SuppressWarnings("unchecked")
    public static <T,U extends T> T wrap(final StatsManager statsManager,
                                         final StatsKey key,
                                         final U target,
                                         final Class<?>[] ifaces) {
        ClassLoader classLoader = Thread.currentThread()
                                        .getContextClassLoader();

        T proxy = (T) Proxy.newProxyInstance(classLoader, 
                                             ifaces, 
                                             new StatsProxy(statsManager, key, target));
        return proxy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T unwrap(final T proxy) {
        if (!Proxy.isProxyClass(proxy.getClass())) {
            return proxy;
        }

        InvocationHandler ih = Proxy.getInvocationHandler(proxy);
        if (!(ih instanceof StatsProxy)) {
            return proxy;
        }

        StatsProxy wrapper = (StatsProxy)ih;
        return (T) wrapper.target;
    }

    public static boolean isProxy(final Object object) {
        if (!Proxy.isProxyClass(object.getClass())) {
            return false;
        }

        if (!(Proxy.getInvocationHandler(object) instanceof StatsProxy)) {
            return false;
        }

        return true;
    }

    protected static String getMethodString(final Method method) {

        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();

        if (params.length == 0) {
            return methodName;
        }

        StringBuilder buf = new StringBuilder(methodName.length() + (params.length * 16));

        for (int i = 0; i < params.length; i++) {
            buf.append('_');
            buf.append(params[i].getClass().getSimpleName());
        }

        return buf.toString();
    }

    @Override
    public Object invoke(final Object proxy, 
                         final Method method, 
                         final Object[] args) throws Throwable {
        StatsKey methodKey = key.buildCopy()
                                .withAttribute("method", getMethodString(method))
                                .newKey();

        final StatsTracker tracker = statsManager.getTracker(methodKey).track();

        try {
            if (method.equals(EQUALS_METHOD)) {
                return target.equals(unwrap(args[0]));
            }

            return method.invoke(target, args);

        } catch (Throwable t) {
            Throwable cause;
            if (t instanceof InvocationTargetException) {
                cause = t.getCause();
            } else {
                cause = t;
            }

            StatsKey exceptionKey = methodKey.buildCopy()
                                             .withAttribute("threw", cause.getClass().getName())
                                             .newKey();

            statsManager.getTracker(exceptionKey).track().commit();

            throw cause;

        } finally {
            tracker.commit();
        }
    }
}

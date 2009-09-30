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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.stajistics.StatsKey;
import org.stajistics.StatsManager;

/**
 * 
 * @author The Stajistics Project
 * 
 */
public class StatsSelectiveProxy extends StatsProxy {

    protected final SelectionCriteria criteria;

    protected StatsSelectiveProxy(final StatsManager statsManager,
                                  final StatsKey key,
                                  final Object target,
                                  final SelectionCriteria criteria) {
        super(statsManager, key, target);

        if (criteria == null) {
            throw new NullPointerException("criteria");
        }

        this.criteria = criteria;
    }

    /**
     * The type which receives the result of this method call must be an
     * interface.
     * 
     * @param <T>
     * @param key
     * @param target
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T wrap(final StatsManager statsManager,
                             final StatsKey key,
                             final T target,
                             final SelectionCriteria criteria) {
        Class<? super T>[] ifaces = (Class<? super T>[]) target.getClass()
                                                               .getInterfaces();
        return wrap(statsManager, key, target, criteria, ifaces);
    }

    @SuppressWarnings("unchecked")
    public static <T,U extends T> T wrap(final StatsManager statsManager,
                                         final StatsKey key,
                                         final U target,
                                         final SelectionCriteria criteria,
                                         final Class<T> iface) {
        return wrap(statsManager, key, target, criteria,
                    (Class<T>[]) new Class[] { iface });
    }

    @SuppressWarnings("unchecked")
    public static <T,U extends T> T wrap(final StatsManager statsManager,
                                         final StatsKey key,
                                         final U target,
                                         final SelectionCriteria criteria,
                                         final Class<?>[] ifaces) {
        ClassLoader classLoader = Thread.currentThread()
                                        .getContextClassLoader();

        T proxy = (T) Proxy.newProxyInstance(classLoader, ifaces,
                                             new StatsSelectiveProxy(statsManager, key, target, criteria));
        return proxy;
    }

    public static <T> T unwrap(final T proxy) {
        return StatsProxy.unwrap(proxy);
    }

    public static boolean isProxy(final Object object) {
        return StatsProxy.isProxy(object);
    }

    @Override
    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {

        if (!criteria.select(method, args)) {
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

                throw cause;
            }
        }

        return super.invoke(proxy, method, args);
    }

    /* NESTED CLASSES */

    public static interface SelectionCriteria {

        boolean select(Method method, Object[] args);

    }

    public static class CompositeSelectionCriteria implements SelectionCriteria {

        public enum Op {
            AND, OR
        }

        private final SelectionCriteria[] criteriaList;
        private final Op operation;

        public CompositeSelectionCriteria(final List<SelectionCriteria> criteriaList,
                                          final Op operation) {
            if (criteriaList == null) {
                throw new NullPointerException("null criteriaList");
            }
            if (criteriaList.isEmpty()) {
                throw new IllegalArgumentException("empty criteriaList");
            }

            this.criteriaList = criteriaList.toArray(new SelectionCriteria[criteriaList.size()]);
            this.operation = operation;
        }

        @Override
        public boolean select(final Method method, final Object[] args) {
            if (operation == Op.AND) {

                for (int i = 0; i < criteriaList.length; i++) {
                    if (!criteriaList[i].select(method, args)) {
                        return false;
                    }
                }

                return true;

            } else if (operation == Op.OR) {

                for (int i = 0; i < criteriaList.length; i++) {
                    if (criteriaList[i].select(method, args)) {
                        return true;
                    }
                }

                return false;

            } else {
                throw new InternalError("Unsupported operation: " + operation);
            }
        }
    }
    
    public static class EnabledCriteria implements SelectionCriteria {

        private final AtomicBoolean enabled;

        public EnabledCriteria() {
            this(true);
        }

        public EnabledCriteria(final boolean enabled) {
            this(new AtomicBoolean(enabled));
        }

        public EnabledCriteria(final AtomicBoolean enabled) {
            if (enabled == null) {
                throw new NullPointerException("enabled");
            }
            this.enabled = enabled;
        }

        @Override
        public boolean select(final Method method, final Object[] args) {
            return enabled.get();
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(final boolean enabled) {
            this.enabled.set(enabled);
        }
    }

    public static class MethodCriteria implements SelectionCriteria {

        private final Method method;
        private final boolean include;

        public MethodCriteria(final Method method) {
            this(method, true);
        }

        public MethodCriteria(final Method method,
                              final boolean include) {
            if (method == null) {
                throw new NullPointerException("method");
            }

            this.method = method;
            this.include = include;
        }
        
        @Override
        public boolean select(final Method method, final Object[] args) {
            return (this.method.equals(method) == include);
        }
    }

    public static class MethodSetCriteria implements SelectionCriteria {

        private final Set<Method> methodSet;
        private final boolean include;

        public MethodSetCriteria(final Set<Method> methodSet) {
            this(methodSet, true);
        }

        public MethodSetCriteria(final Set<Method> methodSet,
                                 final boolean include) {
            if (methodSet == null) {
                throw new NullPointerException("methodSet");
            }

            this.methodSet = methodSet;
            this.include = include;
        }

        @Override
        public boolean select(final Method method, final Object[] args) {
            return methodSet.contains(method) == include;
        }

    }

    public static class MethodModifierCriteria implements SelectionCriteria {

        private final int modifierMask;
        private final boolean any;

        public MethodModifierCriteria(final int modifierMask) {
            this(modifierMask, true);
        }

        public MethodModifierCriteria(final int modifierMask, final boolean any) {
            this.modifierMask = modifierMask;
            this.any = any;
        }

        @Override
        public boolean select(final Method method, final Object[] args) {
            int maskedModifiers = method.getModifiers() & modifierMask;

            if (any) {
                if (maskedModifiers != 0) {
                    return true;
                }
            } else {
                if (maskedModifiers == modifierMask) {
                    return true;
                }
            }

            return false;
        }
    }

}

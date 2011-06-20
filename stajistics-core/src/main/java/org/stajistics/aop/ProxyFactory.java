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
package org.stajistics.aop;


/**
 * 
 * @author The Stajistics Project
 *
 */
public interface ProxyFactory<T> {

    T createProxy(T instance);

    public static final class NoOp<T> implements ProxyFactory<T> {

        @SuppressWarnings("rawtypes")
        private static final NoOp instance = new NoOp();

        private NoOp() {}

        @SuppressWarnings("unchecked")
        public static <T> NoOp<T> instance() {
            return instance;
        }

        public T createProxy(final T instance) {
            return instance;
        }
    }
}

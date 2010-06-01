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
package org.stajistics;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public final class Util {

    private Util() {}

    public static boolean isValidKeyAttributeName(final String name) {
        if (name.length() == 0) {
            return false;
        }

        if (name.indexOf(',') > -1) {
            return false;
        }

        if (name.indexOf('=') > -1) {
            return false;
        }

        if (name.indexOf(':') > -1) {
            return false;
        }

        if (name.indexOf('*') > -1) {
            return false;
        }

        if (name.indexOf('?') > -1) {
            return false;
        }

        return true;
    }

    public static boolean equalsNullAware(final Object obj1, final Object obj2) {
        if (obj1 == null) {
            return obj2 == null;

        } else if (obj2 == null) {
            return false;
        }

        return obj1.equals(obj2);
    }

}

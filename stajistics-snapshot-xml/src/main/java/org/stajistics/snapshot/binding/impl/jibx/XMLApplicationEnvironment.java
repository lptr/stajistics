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
package org.stajistics.snapshot.binding.impl.jibx;

import java.util.HashMap;
import java.util.Map;

import org.stajistics.snapshot.binding.ApplicationEnvironment;

/**
 * 
 * @author The Stajistics Project
 */
public class XMLApplicationEnvironment implements ApplicationEnvironment {

    private HashMap<String,String> properties;

    public XMLApplicationEnvironment() {
        properties = new HashMap<String,String>();
    }

    private XMLApplicationEnvironment(final boolean dummy) {}

    public static XMLApplicationEnvironment factory() {
        return new XMLApplicationEnvironment(false);
    }

    @Override
    public Map<String,String> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof XMLApplicationEnvironment) && equals((XMLApplicationEnvironment)obj);
    }

    public boolean equals(final XMLApplicationEnvironment other) {
        if ((properties == null || properties.isEmpty()) &&
            (other.properties == null || other.properties.isEmpty())) {
            return true;
        }
        return properties.equals(other.properties);
    }
}

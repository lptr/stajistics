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
package org.stajistics;


/**
 * Provides information on the Stajistics API.
 *
 * @author The Stajistics Project
 */
public final class Stajistics {

    protected static final String PROPS_FILE = "library.properties";
    protected static final String PROP_NAME = "library.name";
    protected static final String PROP_VERSION = "library.version";

    private static final String name = replaceNull(Stajistics.class
                                                             .getPackage()
                                                             .getImplementationTitle(), 
                                                   "Stajistics");
    private static final String version = replaceNull(Stajistics.class
                                                                .getPackage()
                                                                .getImplementationTitle(), 
                                                      "0.0");

    private static String replaceNull(String value, String defaultValue) {
    	return value == null ? defaultValue : value;
    }

    private Stajistics() {}

    /**
     * Get the name of the Stajistics API.
     * @return The name.
     */
    public static String getName() {
        return name;
    }

    /**
     * Get the version of the Stajistics API.
     * @return The version.
     */
    public static String getVersion() {
        return version;
    }
}

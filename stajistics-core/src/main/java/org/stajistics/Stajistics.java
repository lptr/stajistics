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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides information on the Stajistics API.
 *
 * @author The Stajistics Project
 */
public final class Stajistics {

    protected static final String PROPS_FILE = "library.properties";
    protected static final String PROP_NAME = "library.name";
    protected static final String PROP_VERSION = "library.version";

    private static String name;
    private static String version;
    static {
        try {
            loadProperties(PROPS_FILE);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Stajistics() {}

    protected static void loadProperties(final String fileName) throws IOException {
        Properties props = new Properties();

        ClassLoader classLoader = Stajistics.class.getClassLoader();
        InputStream in = classLoader.getResourceAsStream(fileName);

        String name = null;
        String version = null;
        
        if (in != null) {
            props.load(in);

            name = props.getProperty(PROP_NAME);
            version = props.getProperty(PROP_VERSION);
        }

        Stajistics.name = (name == null) ? ""
                                         : name;
        Stajistics.version = (version == null) ? ""
                                               : version;
    }

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

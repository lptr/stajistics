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
 * 
 * @author The Stajistics Project
 *
 */
public abstract class StatsProperties {

    private static StatsProperties instance;

    protected StatsProperties() {}

    public static StatsProperties getInstance() {
        if (instance == null) {
            synchronized (StatsProperties.class) {
                if (instance == null) {
                    instance = new SystemStatsProperties();
                }
            }
        }

        return instance;
    }

    public static void load(final StatsProperties instance) {
        StatsProperties.instance = instance;
    }

    public static Boolean getBooleanProperty(final String key) {
        return getInstance().getBooleanPropertyImpl(key, null);
    }

    public static Boolean getBooleanProperty(final String key,
                                             final Boolean defaultValue) {
        return getInstance().getBooleanPropertyImpl(key, defaultValue);
    }

    protected Boolean getBooleanPropertyImpl(final String key,
                                             final Boolean defaultValue) {
        Boolean value = defaultValue;
        String strValue = getProperty(key);
        if (strValue != null) {
            value = Boolean.valueOf(strValue);
        }
        return value;
    }

    public static Double getDoubleProperty(final String key) {
        return getInstance().getDoublePropertyImpl(key, null);
    }

    public static Double getDoubleProperty(final String key,
                                           final Double defaultValue) {
        return getInstance().getDoublePropertyImpl(key, defaultValue);
    }

    protected Double getDoublePropertyImpl(final String key,
                                           final Double defaultValue) {
        Double value = defaultValue;
        String strValue = getProperty(key);
        if (strValue != null) {
            try {
                value = Double.parseDouble(strValue);
            } catch (NumberFormatException nfe) {}
        }
        return value;
    }

    public static Integer getIntegerProperty(final String key) {
        return getInstance().getIntegerPropertyImpl(key, null);
    }

    public static Integer getIntegerProperty(final String key,
                                             final Integer defaultValue) {
        return getInstance().getIntegerPropertyImpl(key, defaultValue);
    }

    protected Integer getIntegerPropertyImpl(final String key,
                                             final Integer defaultValue) {
        Integer value = defaultValue;
        String strValue = getProperty(key);
        if (strValue != null) {
            try {
                value = Integer.parseInt(strValue);
            } catch (NumberFormatException nfe) {}
        }
        return value;
    }

    public static Long getLongProperty(final String key) {
        return getInstance().getLongPropertyImpl(key, null);
    }

    public static Long getLongProperty(final String key,
                                       final Long defaultValue) {
        return getInstance().getLongPropertyImpl(key, defaultValue);
    }

    protected Long getLongPropertyImpl(final String key,
                                       final Long defaultValue) {
        Long value = defaultValue;
        String strValue = getProperty(key);
        if (strValue != null) {
            try {
                value = Long.parseLong(strValue);
            } catch (NumberFormatException nfe) {}
        }
        return value;
    }

    public static String getProperty(final String key) {
        return getInstance().getPropertyImpl(key, null);
    }

    public static String getProperty(final String key,
                                     final String defaultValue) {
        return getInstance().getPropertyImpl(key, defaultValue);
    }

    protected abstract String getPropertyImpl(String key, String defaultValue);

    /* NESTED CLASSES */

    private static final class SystemStatsProperties extends StatsProperties {

        @Override
        public String getPropertyImpl(final String key,
                                      final String defaultValue) {
            return System.getProperty(key, defaultValue);
        }

    }
}

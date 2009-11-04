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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author The Stajistics Project
 */
public abstract class StatsProperties {

    private static final Logger logger = LoggerFactory.getLogger(StatsProperties.class);

    private static StatsProperties instance;

    public StatsProperties() {}

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

    protected abstract Object getPropertyImpl(String key, Object defaultValue);

    /* STRING */

    public static String getProperty(final String key) {
        return getProperty(key, null);
    }

    public static String getProperty(final String key,
                                     final String defaultValue) {
        Object value = getInstance().getPropertyImpl(key, defaultValue);
        return (value != null) ? value.toString() : null;
    }

    /* BOOLEAN */

    public static Boolean getBooleanProperty(final String key) {
        return getBooleanProperty(key, null);
    }

    public static Boolean getBooleanProperty(final String key,
                                             final Boolean defaultValue) {
        Boolean value = defaultValue;
        Object objectValue = getInstance().getPropertyImpl(key, defaultValue);

        if (objectValue != null) {
            if (objectValue instanceof Boolean) {
                value = (Boolean)objectValue;
            } else if (objectValue instanceof String) {
                value = Boolean.valueOf((String)objectValue);
            } else {
                logger.warn("Failed to coerce property {}={} into a boolean", key, objectValue);
            }
        }

        return value;
    }

    /* INTEGER */

    public static Integer getIntegerProperty(final String key) {
        return getIntegerProperty(key, null);
    }

    public static Integer getIntegerProperty(final String key,
                                             final Integer defaultValue) {
        Integer value = defaultValue;
        Object objectValue = getInstance().getPropertyImpl(key, defaultValue);

        if (objectValue != null) {
            if (objectValue instanceof Number) {
                value = ((Number)objectValue).intValue();
            } else if (objectValue instanceof String) {
                try {
                    value = Integer.parseInt((String)objectValue);
                } catch (NumberFormatException nfe) {
                    logger.warn("Failed to parse string property {}={} into an integer", key, objectValue);
                }
            } else {
                logger.warn("Failed to coerce property {}={} into an integer", key, objectValue);
            }
        }

        return value;
    }

    /* LONG */

    public static Long getLongProperty(final String key) {
        return getLongProperty(key, null);
    }

    public static Long getLongProperty(final String key,
                                       final Long defaultValue) {
        Long value = defaultValue;
        Object objectValue = getInstance().getPropertyImpl(key, defaultValue);

        if (objectValue != null) {
            if (objectValue instanceof Number) {
                value = ((Number)objectValue).longValue();
            } else if (objectValue instanceof String) {
                try {
                    value = Long.parseLong((String)objectValue);
                } catch (NumberFormatException nfe) {
                    logger.warn("Failed to parse string property {}={} into a long", key, objectValue);
                }
            } else {
                logger.warn("Failed to coerce property {}={} into a long", key, objectValue);
            }
        }

        return value;
    }

    /* FLOAT */

    public static Double getFloatProperty(final String key) {
        return getDoubleProperty(key, null);
    }

    public static Float getFloatProperty(final String key,
                                         final Float defaultValue) {
        Float value = defaultValue;
        Object objectValue = getInstance().getPropertyImpl(key, defaultValue);

        if (objectValue != null) {
            if (objectValue instanceof Number) {
                value = ((Number)objectValue).floatValue();
            } else if (objectValue instanceof String) {
                try {
                    value = Float.parseFloat((String)objectValue);
                } catch (NumberFormatException nfe) {
                    logger.warn("Failed to parse string property {}={} into a float", key, objectValue);
                }
            } else {
                logger.warn("Failed to coerce property {}={} into a float", key, objectValue);
            }
        }

        return value;
    }

    /* DOUBLE */

    public static Double getDoubleProperty(final String key) {
        return getDoubleProperty(key, null);
    }

    public static Double getDoubleProperty(final String key,
                                           final Double defaultValue) {
        Double value = defaultValue;
        Object objectValue = getInstance().getPropertyImpl(key, defaultValue);

        if (objectValue != null) {
            if (objectValue instanceof Number) {
                value = ((Number)objectValue).doubleValue();
            } else if (objectValue instanceof String) {
                try {
                    value = Double.parseDouble((String)objectValue);
                } catch (NumberFormatException nfe) {
                    logger.warn("Failed to parse string property {}={} into a double", key, objectValue);
                }
            } else {
                logger.warn("Failed to coerce property {}={} into a double", key, objectValue);
            }
        }

        return value;
    }

    /* NESTED CLASSES */

    public static final class SystemStatsProperties extends StatsProperties {

        @Override
        public Object getPropertyImpl(final String key,
                                      final Object defaultValue) {
            String strDefaultValue = (defaultValue == null) ? null : defaultValue.toString();
            return System.getProperty(key, strDefaultValue);
        }
    }

    public static final class MapStatsProperties extends StatsProperties {

        private final Map<String,?> propertyMap;

        public MapStatsProperties(final Map<String,?> propertyMap) {
            if (propertyMap == null) {
                throw new NullPointerException("propertyMap");
            }
            this.propertyMap = propertyMap;
        }

        @Override
        protected Object getPropertyImpl(final String key, 
                                         final Object defaultValue) {
            Object value = propertyMap.get(key);
            if (value == null) {
                value = defaultValue;
            }

            return value;
        }
    }
}

package org.stajistics.data;

public final class FieldUtils {
    private FieldUtils() {
        // Utility class
    }
    
    public static long longDefaultValue(Field field) {
        return ((Number) field.defaultValue()).longValue();
    }
    
    public static long doubleDefaultValue(Field field) {
        return ((Number) field.defaultValue()).longValue();
    }

    public static boolean booleanDefaultValue(Field field) {
        return ((Number) field.defaultValue()).longValue() != 0;
    }
}

package org.stajistics.data;

public final class FieldUtils {
    private FieldUtils() {
        // Utility class
    }
    
    public static long longDefault(Field field) {
        return ((Number) field.defaultValue()).longValue();
    }
    
    public static double doubleDefault(Field field) {
        return ((Number) field.defaultValue()).doubleValue();
    }

    public static boolean booleanDefault(Field field) {
        return ((Number) field.defaultValue()).longValue() != 0;
    }
}

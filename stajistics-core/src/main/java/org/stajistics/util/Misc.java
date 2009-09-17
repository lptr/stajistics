package org.stajistics.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author The Stajistics Project
 *
 */
public final class Misc {

    private Misc() {}

    @SuppressWarnings("unchecked")
    public static <T> Set<T> getStaticFieldValues(final Class<?> target, 
                                                  final Class<T> fieldType) {
        Set<T> result = new HashSet<T>();

        for (Field field : target.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.STATIC) != 0 && 
                    field.getType().equals(fieldType)) {
                try {
                    result.add((T)field.get(null));
                    
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

}

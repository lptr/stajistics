package org.stajistics.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

/**
 * Defines various static utility methods.
 *
 * @author The Stajistics Project
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

    /**
     * Log an error message to indicate that an Exception has been swallowed.
     * This always logs a brief ERROR level message, but if DEBUG level is 
     * on for <tt>logger</tt> also log the full stack trace at DEBUG level.
     * The purpose of this behaviour is to avoid clogging the logs with repetitive
     * large stack traces if they are not necessary.
     *
     * @param logger The logger to which to write the error message. Must not be <tt>null</tt>.
     * @param message The message to log, possibly containing "{}" argument substitution tokens.
     *                 Must not be <tt>null</tt>.
     * @param error The Throwable that was swallowed.
     *              Must not be <tt>null</tt>.
     * @param args The arguments to insert into the <tt>message</tt>. May be <tt>null</tt> or empty.
     */
    public static void logSwallowedException(final Logger logger,
                                             final Throwable error,
                                             final String message,
                                             final Object... args) {
        if (logger == null || error == null || message == null) {
            // Do not throw an Exception ourselves, because this would defeat the purpose
            // of "swallowing" an Exception.
            System.err.println("Passed null parameter(s): org.stajistics.util.Misc.logSwallowedException(" +
                               logger + ", " +
                               error + ", " +
                               message + ", " +
                               args + ")");
            return;
        }

        try {
            // If debug is on, include a full stack trace
            if (logger.isDebugEnabled()) {
                logger.error(message, args);
                logger.debug("...", error);
    
            } else {
                if (args == null || args.length == 0) {
                    // Log a simple error message without a stack trace
                    logger.error(message + ": " + error.toString());
    
                } else {
                    // Log a formatted error message without a stack trace
                    Object[] argsWithError = new Object[args.length + 1];
                    System.arraycopy(args, 0, argsWithError, 0, args.length);
                    argsWithError[argsWithError.length - 1] = error.toString();
    
                    logger.error(message + ": {}", argsWithError);
                }
            }
        } catch (Exception e) { // Paranoia?
            e.printStackTrace(System.err);
        }
    }

}

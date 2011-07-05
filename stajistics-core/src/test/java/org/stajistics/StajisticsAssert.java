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

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Formatter;

/**
 * @author The Stajistics Project
 */
public class StajisticsAssert {

    private static String formatMessage(final String suppliedMessage,
                                        final String defaultMessage,
                                        final Object... defaultMessageArgs) {
        if (suppliedMessage != null && suppliedMessage.length() > 0) {
            return suppliedMessage;
        }

        if (defaultMessage != null && defaultMessage.length() > 0) {
            if (defaultMessageArgs == null || defaultMessageArgs.length == 0) {
                return defaultMessage;
            }

            return new Formatter().format(defaultMessage, defaultMessageArgs).toString();
        }

        return "<Unspecified failure message>";
    }

    private static void fail(final String suppliedMessage,
                             final String defaultMessage,
                             final Object... defaultMessageArgs) {
        throw new StajisticsAssertionError(formatMessage(suppliedMessage, defaultMessage, defaultMessageArgs));
    }

    private static void fail(final Throwable cause,
                             String suppliedMessage,
                             String defaultMessage,
                             final Object... defaultMessageArgs) {
        throw new StajisticsAssertionError(formatMessage(suppliedMessage, defaultMessage, defaultMessageArgs), cause);
    }

    private static void compoundFail(final String suppliedOutterMessage,
                                     final StajisticsAssertionError assertionError) {
        if (suppliedOutterMessage != null && suppliedOutterMessage.length() > 0) {
            throw new StajisticsAssertionError(suppliedOutterMessage, assertionError);
        }

        throw assertionError;
    }

    public static void assertInstanceOf(final Class<?> iface, final Object test) {
        assertInstanceOf(null, iface, test);
    }

    public static void assertInstanceOf(final String message, final Class<?> clazz, final Object test) {
        if (!clazz.isInstance(test)) {
            fail(message, "Object is not an instance of %s: %s", clazz.getName(), test);
        }
    }

    public static void assertNotInstanceOf(final Class<?> iface, final Object test) {
        assertNotInstanceOf(null, iface, test);
    }

    public static void assertNotInstanceOf(final String message, final Class<?> clazz, final Object test) {
        if (clazz.isInstance(test)) {
            fail(message, "Object is an instance of %s: %s", clazz.getName(), test);
        }
    }

    private static byte[] serialize(final Object object) throws IOException {

        ByteArrayOutputStream baos;
        ObjectOutputStream out = null;

        try {
            baos = new ByteArrayOutputStream(8 * 1024);
            out = new ObjectOutputStream(baos);
            out.writeObject(object);

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {}
            }
        }

        return baos.toByteArray();
    }

    private static Object deserialize(final byte[] bytes) throws IOException, ClassNotFoundException {

        Object result;
        ObjectInputStream in = null;

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(bais);
            result = in.readObject();

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }

        return result;
    }

    public static void assertSerializable(final Object test) {
        assertSerializable(null, test);
    }

    public static void assertSerializable(final String message, final Object test) {
        try {
            assertInstanceOf(Serializable.class, test);
        } catch (StajisticsAssertionError e) {
            compoundFail(message, e);
        }

        byte[] bytes = null;
        try {
            bytes = serialize(test);
        } catch (IOException e) {
            fail(e, message, "Failed to serialize object: %s", test);
        }

        try {
            Object deserializedTest = deserialize(bytes);
            assertNotNull("Deserialized object is null", deserializedTest);

        } catch (Exception e) {
            fail(e, message, "Failed to deserialize object: %s", test);
        }
    }
}

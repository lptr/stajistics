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
package org.stajistics.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsDataBaseURL {

    public static final String PREFIX = "jdbc:stajistics:";

    public static final Set<String> DEFAULT_PARAMETER_NAMES = new HashSet<String>(
        Arrays.asList(
            new String[] {
                Parameters.DELEGATE_DRIVER.getParameterName() 
            }
        )
    );

    private final String originalURL;
    private final String delegateURL;
    private final String delegateDriverClassName;

    private final Map<String,String> parameterMap;

    public StatsDataBaseURL(final String originalURL) {
        this(originalURL, DEFAULT_PARAMETER_NAMES);
    }

    public StatsDataBaseURL(final String originalURL,
                            final Set<String> parameterNames) {
        if (originalURL == null) {
            throw new NullPointerException("originalURL");
        }
        if (!isSupported(originalURL)) {
            throw new FormatException("Unsupported URL: " + originalURL);
        }

        this.originalURL = originalURL;

        // Create a buffer, removing "stajistics:" from the URL
        final StringBuilder buf = new StringBuilder(originalURL.length());
        buf.append("jdbc:");
        buf.append(originalURL.substring(PREFIX.length()));

        this.delegateDriverClassName = extractDelegateDriverClassName(buf);

        this.parameterMap = extractParameters(parameterNames, buf);

        this.delegateURL = buf.toString();
    }

    public static boolean isSupported(final String dataBaseURL) {
        if (dataBaseURL == null) {
            return false;
        }

        return dataBaseURL.toLowerCase()
                          .startsWith(PREFIX);
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public String getDelegateURL() {
        return delegateURL;
    }

    public String getDelegateDriverClassName() {
        return delegateDriverClassName;
    }

    public Map<String,String> getParameters() {
        return parameterMap;
    }

    private String extractDelegateDriverClassName(final StringBuilder buf) {
        String delegateDriverClassName = removeParameter(buf, Parameters.DELEGATE_DRIVER
                                                                        .getParameterName());

        if (delegateDriverClassName == null || delegateDriverClassName.length() < 0) {
            throw new FormatException("Must supply parameter: " + Parameters.DELEGATE_DRIVER
                                                                            .getParameterName());
        }

        return delegateDriverClassName;
    }

    private Map<String,String> extractParameters(final Set<String> parameterNames,
                                                 final StringBuilder buf) {
        final Map<String,String> parameterMap = new HashMap<String,String>();

        for (String parameterName : parameterNames) {
            String parameterValue = removeParameter(buf, parameterName);
            if (parameterValue != null) {
                parameterMap.put(parameterName, parameterValue);
            }
        }

        return Collections.unmodifiableMap(parameterMap);
    }
    
    /**
     * Removes a parameter name-value pair from the URL contained in <tt>buf</tt>
     * for the given <tt>paramName</tt>. Modifies <tt>buf</tt> to recreate the format of 
     * the URL as if the parameter not been defined in the first place. 
     * Does nothing if <tt>paramName</tt> is not found in <tt>buf</tt>.
     *
     * @param buf The StringBuilder containing the data base URL.
     * @param paramName The parameter name for which the name-value pair should be removed.
     *
     * @return The value of the parameter, or <tt>null</tt> if not found.
     */
    private String removeParameter(final StringBuilder buf,
                                   final String paramName) {
        String paramValue = null;

        // Check if the parameter name is found
        int start = buf.indexOf(paramName);
        if (start > -1) {

            // Check if the character preceding the parameter name is a space
            boolean startsWithSpace = isChar(buf, start - 1, ' ');

            // Check if the character preceding the parameter name is an ampersand
            boolean startsWithAmpersand = isChar(buf, start - 1, '&');

            // Check if the character preceding the parameter name is a question mark
            boolean startsWithQuestionMark = isChar(buf, start - 1, '?');

            // Scan from the end of the parameter name
            int i = start + paramName.length();

            // Skip spaces
            while (isChar(buf, i, ' ')) { 
                i++;
            }

            // Check for '='
            if (isChar(buf, i, '=')) {

                // pass the '='
                i++; 

                // Skip spaces
                while (isChar(buf, i, ' ')) {
                    i++;
                }

                // Mark the start of the value
                int valueStart = i;

                // Skip to space/ampersand/end
                while (i < buf.length() && 
                        !isChar(buf, i, ' ') &&
                        !isChar(buf, i, '&')) { 
                    i++; 
                }

                // Mark the end of the value
                int valueEnd = i;

                // Extract the value
                paramValue = buf.substring(valueStart, valueEnd);

                // Ensure we don't end up with double ampersands
                if (isChar(buf, i, '&')) {
                    valueEnd++;
                } else if (startsWithAmpersand) {
                    start--;
                }

                // Ensure we don't end up with double spaces
                if (startsWithSpace) {
                    start--;
                }

                // Remove the name-value pair
                buf.delete(start, valueEnd);

                // If we deleted the last parameter in the URL
                if (start == buf.length()) {
                    // Trim trailing spaces
                    while (buf.charAt(buf.length() - 1) == ' ') {
                        buf.deleteCharAt(buf.length() - 1);
                    }

                    // Delete a remaining '?'
                    if (startsWithQuestionMark) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                }
            }
        }

        return paramValue;
    }

    private boolean isChar(final StringBuilder buf, 
                           final int index, 
                           final char test) {
        if (index < 0 || index >= buf.length()) {
            return false;
        }

        char c = buf.charAt(index);

        return c == test;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(512);

        buf.append(getClass().getSimpleName());
        buf.append("[originalURL=");
        buf.append(originalURL);
        buf.append(",delegateURL=");
        buf.append(delegateURL);
        buf.append(",delegateDriverClassName=");
        buf.append(delegateDriverClassName);
        buf.append(",parameters=");
        buf.append(parameterMap);
        buf.append(']');

        return buf.toString();
    }
    
    /* NESTED CLASSES */

    public enum Parameters {
        DELEGATE_DRIVER("statsDelegateDriver"),
        KEY_PREFIX("statsKeyPrefix"),
        DRIVER_WRAPPER_ENABLED("statsDriverWrapperEnabled"),
        MANAGEMENT_ENABLED("statsManagementEnabled"),
        PROXY_ENABLED("statsProxyEnabled");

        private final String parameterName;

        Parameters(final String parameterName) {
            if (parameterName == null) {
                throw new NullPointerException("parameterName");
            }
            this.parameterName = parameterName;
        }

        public String getParameterName() {
            return parameterName;
        }
    }

    public static class FormatException extends RuntimeException {

        private static final long serialVersionUID = 5948270015734158089L;

        public FormatException(final String msg) {
            super(msg);
        }

    }
}

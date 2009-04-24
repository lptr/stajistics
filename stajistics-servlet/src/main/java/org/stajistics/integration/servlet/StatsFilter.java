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
package org.stajistics.integration.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(StatsFilter.class);

    private static final String INIT_PARAM_KEY_NAME = "keyName";
    private static final String INIT_PARAM_BIND_PARAMS = "bindParameters";
    private static final String INIT_PARAM_BIND_HEADERS = "bindHeaders";
    private static final String INIT_PARAM_EXCEPTION_INCIDENTS = "exceptionIncidents";
    private static final String INIT_PARAM_EXCEPTION_KEY_NAME_SUFFIX = "exceptionKeyNameSuffix";

    private static final String DEFAULT_PARAM_EXCEPTION_KEY_NAME_SUFFIX = "exception";

    private static final String KEY_ATTR_BINDING = "binding";
    private static final String KEY_ATTR_BINDING_PARAM = "parameter";
    private static final String KEY_ATTR_BINDING_HEADER = "header";

    private StatsKey key;
    private String[] bindParams;
    private String[] bindHeaders;

    private StatsKey exceptionKey;

    @Override
    public void init(final FilterConfig config) throws ServletException {
        String keyName = config.getInitParameter(INIT_PARAM_KEY_NAME);
        if (keyName == null) {
            keyName = getClass().getName();
        }

        key = Stats.newKey(keyName);

        // Binding parameters
        bindParams = parseBindings(config, INIT_PARAM_BIND_PARAMS);
        bindHeaders = parseBindings(config, INIT_PARAM_BIND_HEADERS);

        // Exception parameters
        boolean exceptionIncidents = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_EXCEPTION_INCIDENTS));
        if (exceptionIncidents) {
            String exceptionKeyNameSuffix = config.getInitParameter(INIT_PARAM_EXCEPTION_KEY_NAME_SUFFIX);
            if (exceptionKeyNameSuffix == null || exceptionKeyNameSuffix.length() == 0) {
                exceptionKeyNameSuffix = DEFAULT_PARAM_EXCEPTION_KEY_NAME_SUFFIX;
            }

            exceptionKey = key.buildCopy()
                              .withNameSuffix(exceptionKeyNameSuffix)
                              .newKey();

        }

        if (logger.isInfoEnabled()) {
            logger.info(getClass().getSimpleName() + " initialized");
        }
    }

    private String[] parseBindings(final FilterConfig config,
                                   final String initParamName) {
        String[] result = null;

        String bindString = config.getInitParameter(initParamName);
        if (bindString != null) {
            result = bindString.split("[,]");
            if (result.length == 0) {
                result = null;
            }
        }

        return result;
    }

    @Override
    public void destroy() {
        key = null;
        bindParams = null;
        bindHeaders = null;

        exceptionKey = null;

        if (logger.isInfoEnabled()) {
            logger.info(getClass().getSimpleName() + " destroyed");
        }
    }

    private StatsTracker getTracker(final ServletRequest request) {

        StatsTracker tracker;
        if (bindParams == null && bindHeaders == null) {
            tracker = Stats.getTracker(key);
        } else {
            tracker = Stats.getTracker(getStatsKeys(request));
        }

        return tracker;
    }

    private StatsKey[] getStatsKeys(final ServletRequest request) {
        List<StatsKey> keyList = new LinkedList<StatsKey>();
        keyList.add(key);

        if (bindParams != null) {
            addParamBoundStatsKeys(request, keyList);
        }
        if (bindHeaders != null) {
            addHeaderBoundStatsKeys(request, keyList);
        }

        return keyList.toArray(new StatsKey[keyList.size()]);
    }

    private void addParamBoundStatsKeys(final ServletRequest request,
                                        final List<StatsKey> keyList) {
        String paramValue;

        for (String bindParam : bindParams) {
            paramValue = request.getParameter(bindParam);
            if (paramValue != null) {
                StatsKey paramKey = key.buildCopy()
                                       .withAttribute(KEY_ATTR_BINDING, KEY_ATTR_BINDING_PARAM)
                                       .withAttribute(bindParam, paramValue)
                                       .newKey();
                keyList.add(paramKey);
            }
        }
    }

    private void addHeaderBoundStatsKeys(final ServletRequest request,
                                         final List<StatsKey> keyList) {
        if (!(request instanceof HttpServletRequest)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Header bindings specified in filter init-params but not processing HTTP request: " + 
                            Arrays.asList(bindHeaders));
            }

            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest)request; 

        String headerValue;

        for (String bindHeader : bindHeaders) {
            headerValue = httpRequest.getHeader(bindHeader);
            if (headerValue != null) {
                StatsKey headerKey = key.buildCopy()
                                        .withAttribute(KEY_ATTR_BINDING, KEY_ATTR_BINDING_HEADER)
                                        .withAttribute(bindHeader, headerValue)
                                        .newKey();
                keyList.add(headerKey);
            }
        }
    }

    @Override
    public void doFilter(final ServletRequest request, 
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {

        StatsTracker tracker = getTracker(request);
        tracker.track();

        try {
            chain.doFilter(request, response);

        } catch (Throwable t) {
            if (exceptionKey != null) {
                Stats.incident(exceptionKey);
                Stats.incident(exceptionKey.buildCopy()
                                           .withAttribute("className", t.getClass().getName())
                                           .newKey());
            }

            if (t instanceof IOException) {
                throw (IOException)t;
            }
            if (t instanceof ServletException) {
                throw (ServletException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }

            throw new RuntimeException(t);

        } finally {
            tracker.commit();
        }
    }

}

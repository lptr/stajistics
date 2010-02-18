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
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.Stats;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigBuilder;
import org.stajistics.StatsKey;
import org.stajistics.tracker.incident.DefaultIncidentTracker;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(StatsFilter.class);

    static final String INIT_PARAM_KEY_NAME = "keyName";
    static final String INIT_PARAM_BIND_PARAMS = "bindParameters";
    static final String INIT_PARAM_BIND_HEADERS = "bindHeaders";
    static final String INIT_PARAM_TRACK_EXCEPTION_INCIDENTS = "trackExceptionIncidents";
    static final String INIT_PARAM_EXCEPTION_KEY_NAME_SUFFIX = "exceptionKeyNameSuffix";
    static final String INIT_PARAM_TRACK_RESPONSE_CODES = "trackResponseCodes";
    static final String INIT_PARAM_RESPONSE_CODE_KEY_NAME_SUFFIX = "responseCodeKeyNameSuffix";

    static final String DEFAULT_PARAM_EXCEPTION_KEY_NAME_SUFFIX = "exception";
    static final String DEFAULT_PARAM_RESPONSE_CODE_KEY_NAME_SUFFIX = "responseCode";

    static final String KEY_ATTR_BINDING = "binding";
    static final String KEY_ATTR_BINDING_PARAM = "parameter";
    static final String KEY_ATTR_BINDING_HEADER = "header";

    private StatsKey key;
    private String[] bindParams;
    private String[] bindHeaders;

    private StatsKey exceptionKey;
    private StatsKey responseCodeKey;

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
        boolean exceptionIncidents = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_EXCEPTION_INCIDENTS));
        if (exceptionIncidents) {
            String exceptionKeyNameSuffix = config.getInitParameter(INIT_PARAM_EXCEPTION_KEY_NAME_SUFFIX);
            if (exceptionKeyNameSuffix == null || exceptionKeyNameSuffix.length() == 0) {
                exceptionKeyNameSuffix = DEFAULT_PARAM_EXCEPTION_KEY_NAME_SUFFIX;
            }

            exceptionKey = key.buildCopy()
                              .withNameSuffix(exceptionKeyNameSuffix)
                              .newKey();
            configureIncidentTracker(exceptionKey);
        }

        // Response codes
        boolean responseCodes = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_RESPONSE_CODES));
        if (responseCodes) {
            String responseCodeKeyNameSuffix = config.getInitParameter(INIT_PARAM_RESPONSE_CODE_KEY_NAME_SUFFIX);
            if (responseCodeKeyNameSuffix == null || responseCodeKeyNameSuffix.length() == 0) {
                responseCodeKeyNameSuffix = DEFAULT_PARAM_RESPONSE_CODE_KEY_NAME_SUFFIX;
            }

            responseCodeKey = key.buildCopy()
                                 .withNameSuffix(responseCodeKeyNameSuffix)
                                 .newKey();
            configureIncidentTracker(responseCodeKey);
        }

        logger.info("{} initialized", getClass().getSimpleName());
    }

    private void configureIncidentTracker(StatsKey key) {
        StatsConfig originalConfig = Stats.getConfigManager().getConfig(key);
        // Do we have an incident tracker configured already?
        if (originalConfig != null
                && IncidentTracker.class
                        .isAssignableFrom(originalConfig.getTrackerFactory().getTrackerType())) {
            return;
        }
        
        // If not, configure one based on the (possibly) existing config
        StatsConfigBuilder newConfigBuilder = Stats.getManager().getConfigFactory().createConfigBuilder(
                originalConfig);
        newConfigBuilder.withTrackerFactory(DefaultIncidentTracker.FACTORY);
        newConfigBuilder.setConfigFor(key);
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

        logger.info("{} destroyed", getClass().getSimpleName());
    }

    private SpanTracker getSpanTracker(final ServletRequest request) {

        SpanTracker tracker;
        if (bindParams == null && bindHeaders == null) {
            tracker = Stats.getSpanTracker(key);
        } else {
            tracker = Stats.getSpanTracker(getStatsKeys(request));
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
            logger.warn("Header bindings specified in filter init-params but not processing HTTP request: {}", 
                           Arrays.toString(bindHeaders));

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
                         ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {

        SpanTracker tracker = getSpanTracker(request);
        tracker.track();

        try {
            StatsHttpServletResponse statsResponse = null;
            if (responseCodeKey != null && response instanceof HttpServletResponse) {
                statsResponse = new StatsHttpServletResponse((HttpServletResponse)response);
                response = statsResponse;
            }

            chain.doFilter(request, response);

            if (statsResponse != null) {
                Stats.incident(responseCodeKey.buildCopy()
                                              .withAttribute("code", statsResponse.getStatus())
                                              .newKey());
            }

        } catch (Throwable t) {
            if (exceptionKey != null) {
                Stats.failure(t, exceptionKey);
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

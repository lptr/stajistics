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
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigBuilder;
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

    static final String INIT_PARAM_NAMESPACE = "namespace";
    static final String INIT_PARAM_KEY_NAME = "keyName";
    static final String INIT_PARAM_BIND_PARAMS = "bindParameters";
    static final String INIT_PARAM_BIND_HEADERS = "bindHeaders";
    static final String INIT_PARAM_TRACK_REQUEST_URI = "trackRequestURI";
    static final String INIT_PARAM_TRACK_RESPONSE_CODE = "trackResponseCode";
    static final String INIT_PARAM_TRACK_REQUEST_STREAM = "trackRequestStream";
    static final String INIT_PARAM_TRACK_RESPONSE_STREAM = "trackResponseStream";
    static final String INIT_PARAM_TRACK_EXCEPTION = "trackException";

    static final String KEY_NAME_SUFFIX_REQUEST_URI = "requestURI";
    static final String KEY_NAME_SUFFIX_REQUEST_STREAM = "requestStream";
    static final String KEY_NAME_SUFFIX_RESPONSE_STREAM = "responseStream";
    static final String KEY_NAME_SUFFIX_RESPONSE_CODE = "responseCode";
    static final String KEY_NAME_SUFFIX_EXCEPTION = "exception";

    static final String KEY_ATTR_BINDING = "binding";
    static final String KEY_ATTR_BINDING_PARAM = "parameter";
    static final String KEY_ATTR_BINDING_HEADER = "header";
    static final String KEY_ATTR_REQUEST_URI = "requestURI";

    private StatsFactory statsFactory;

    private StatsKey key;
    private String[] bindParams;
    private String[] bindHeaders;

    private StatsKey exceptionKey;
    private StatsKey requestURIKey;
    private StatsKey responseCodeKey;
    private StatsKey requestStreamKey;
    private StatsKey responseStreamKey;

    @Override
    public void init(final FilterConfig config) throws ServletException {

        String namespace = config.getInitParameter(INIT_PARAM_NAMESPACE);
        if (namespace != null) {
        	statsFactory = StatsFactory.forNamespace(namespace);
        } else {
        	statsFactory = StatsFactory.forClass(StatsFilter.class);
        }

        String keyName = config.getInitParameter(INIT_PARAM_KEY_NAME);
        if (keyName == null) {
            keyName = getClass().getName();
        }

        key = statsFactory.newKey(keyName);

        // Binding parameters
        bindParams = parseBindings(config, INIT_PARAM_BIND_PARAMS);
        bindHeaders = parseBindings(config, INIT_PARAM_BIND_HEADERS);

        // Request URI
        boolean requestURI = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_REQUEST_URI));
        if (requestURI) {
        	requestURIKey = key.buildCopy()
        					   .withNameSuffix(KEY_NAME_SUFFIX_REQUEST_URI)
        					   .newKey();
        }

        // Request stream
        boolean requestStream = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_REQUEST_STREAM));
        if (requestStream) {
        	requestStreamKey = key.buildCopy()
        						  .withNameSuffix(KEY_NAME_SUFFIX_REQUEST_STREAM)
        						  .newKey();
        }

        // Response stream
        boolean responseStream = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_RESPONSE_STREAM));
        if (responseStream) {
        	responseStreamKey = key.buildCopy()
        						   .withNameSuffix(KEY_NAME_SUFFIX_RESPONSE_STREAM)
        						   .newKey();
        }

        // Response code
        boolean responseCode = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_RESPONSE_CODE));
        if (responseCode) {
            responseCodeKey = key.buildCopy()
                                 .withNameSuffix(KEY_NAME_SUFFIX_RESPONSE_CODE)
                                 .newKey();
            configureIncidentTracker(responseCodeKey);
        }

        // Exceptions
        boolean exceptionIncidents = Boolean.parseBoolean(config.getInitParameter(INIT_PARAM_TRACK_EXCEPTION));
        if (exceptionIncidents) {
            exceptionKey = key.buildCopy()
                              .withNameSuffix(KEY_NAME_SUFFIX_EXCEPTION)
                              .newKey();
            configureIncidentTracker(exceptionKey);
        }

        logger.info("{} initialized", getClass().getSimpleName());
    }

    private void configureIncidentTracker(StatsKey key) {
        StatsConfig originalConfig = statsFactory.getManager().getConfigManager().getConfig(key);
        // Do we have an incident tracker configured already?
        if (originalConfig != null
                && IncidentTracker.class
                        .isAssignableFrom(originalConfig.getTrackerFactory().getTrackerType())) {
            return;
        }

        // If not, configure one based on the (possibly) existing config
        StatsConfigBuilder newConfigBuilder = statsFactory.getManager()
                                                          .getConfigBuilderFactory()
                                                          .createConfigBuilder(originalConfig);
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
        logger.info("{} destroyed", getClass().getSimpleName());
    }

    private SpanTracker getSpanTracker(final ServletRequest request) {

        SpanTracker tracker;
        if (bindParams == null && bindHeaders == null && requestURIKey == null) {
            tracker = statsFactory.getSpanTracker(key);
        } else {
            tracker = statsFactory.getSpanTracker(getStatsKeys(request));
        }

        return tracker;
    }

    private StatsKey[] getStatsKeys(final ServletRequest request) {
        List<StatsKey> keyList = new LinkedList<StatsKey>();
        keyList.add(key);

        if (requestURIKey != null && request instanceof HttpServletRequest) {
        	keyList.add(requestURIKey.buildCopy()
        							 .withAttribute(KEY_ATTR_REQUEST_URI, 
        									        ((HttpServletRequest)request).getRequestURI())
        							 .newKey());
        }
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

    protected ServletRequest wrapRequest(final ServletRequest request) {
        if (request instanceof HttpServletRequest) {
        	if (requestStreamKey != null) {
        		StatsHttpServletRequest statsRequest =
        			new StatsHttpServletRequest((HttpServletRequest)request,
        										statsFactory,
        										requestStreamKey);
        		return statsRequest;
        	}
        }
        return request;
    }

    protected ServletResponse wrapResponse(final ServletResponse response) {
        if (response instanceof HttpServletResponse) {
            if (responseCodeKey != null || responseStreamKey != null) {
                StatsHttpServletResponse statsResponse = 
                    new StatsHttpServletResponse((HttpServletResponse)response,
                                                 statsFactory,
                                                 responseStreamKey);
                return statsResponse;
            }
        }
        return response;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {

        SpanTracker tracker = getSpanTracker(request);
        tracker.track();

        try {
            request = wrapRequest(request);
            response = wrapResponse(response);

            chain.doFilter(request, response);

            if (response != null && response.getClass() == StatsHttpServletResponse.class) {
                StatsHttpServletResponse statsResponse = (StatsHttpServletResponse) response;
                statsFactory.incident(responseCodeKey.buildCopy()
                                                     .withAttribute("code", statsResponse.getStatus())
                                                     .newKey());
            }

        } catch (Throwable t) {
            if (exceptionKey != null) {
                statsFactory.failure(t, exceptionKey);
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

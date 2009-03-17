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
import org.stajistics.tracker.CompositeStatsTracker;
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
    private static final String INIT_PARAM_BIND_PARAMS = "bindParams";
    private static final String INIT_PARAM_BIND_HEADERS = "bindHeaders";

    private static final String KEY_ATTR_BINDING = "binding";
    private static final String KEY_ATTR_BINDING_PARAM = "param";
    private static final String KEY_ATTR_BINDING_HEADER = "header";

    private StatsKey key;
    private String[] bindParams;
    private String[] bindHeaders;

    @Override
    public void init(final FilterConfig config) throws ServletException {
        String keyName = config.getInitParameter(INIT_PARAM_KEY_NAME);
        if (keyName == null) {
            throw new ServletException("Missing required init parameter: " + INIT_PARAM_KEY_NAME); 
        }

        key = Stats.newKey(keyName);

        bindParams = parseBindings(config, INIT_PARAM_BIND_PARAMS);
        bindHeaders = parseBindings(config, INIT_PARAM_BIND_HEADERS);

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

        if (logger.isInfoEnabled()) {
            logger.info(getClass().getSimpleName() + " destroyed");
        }
    }

    protected StatsTracker getTracker(final ServletRequest request) {

        StatsTracker tracker;
        if (bindParams == null && bindHeaders == null) {
            tracker = Stats.getTracker(key);

        } else {
            List<StatsTracker> subTrackerList = new LinkedList<StatsTracker>();
            subTrackerList.add(Stats.getTracker(key));

            if (bindParams != null) {
                addParamBoundTrackers(request, subTrackerList);
            }
            if (bindHeaders != null) {
                addHeaderBoundTrackers(request, subTrackerList);
            }

            tracker = new CompositeStatsTracker(subTrackerList);
        }

        return tracker;
    }

    private void addParamBoundTrackers(final ServletRequest request,
                                       final List<StatsTracker> trackerList) {
        String paramValue;

        for (String bindParam : bindParams) {
            paramValue = request.getParameter(bindParam);
            if (paramValue != null) {
                StatsKey paramKey = key.buildCopy()
                                       .withAttribute(KEY_ATTR_BINDING, KEY_ATTR_BINDING_PARAM)
                                       .withAttribute(bindParam, paramValue)
                                       .newKey();
                trackerList.add(Stats.getTracker(paramKey));
            }
        }
    }

    private void addHeaderBoundTrackers(final ServletRequest request,
                                        final List<StatsTracker> trackerList) {
        if (!(request instanceof HttpServletRequest)) {
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
                trackerList.add(Stats.getTracker(headerKey));
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

        } finally {
            //TODO: ignore certain response codes? i.e. 302
            tracker.commit();
        }
    }

}

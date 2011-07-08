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
package org.stajistics.integration.servlet.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 * @author The Stajistics Project
 */
public class StatsHttpSessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(StatsHttpSessionListener.class);

    private static final String INIT_PARAM_NAMESPACE = StatsHttpSessionListener.class.getSimpleName() + ".namespace";
    private static final String INIT_PARAM_KEY_NAME = StatsHttpSessionListener.class.getSimpleName() + ".keyName";

    private static final String ATTR_TRACKER = StatsHttpSessionListener.class.getName() + "_tracker";

    private final StatsFactory statsFactory = StatsFactory.forClass(StatsHttpSessionListener.class);

    @Override
    public void sessionCreated(final HttpSessionEvent event) {

        final HttpSession session = event.getSession();
        final ServletContext servletContext = session.getServletContext();
        final String servletContextName = servletContext.getServletContextName();

        StatsFactory f = statsFactory;

        String namespace = session.getServletContext().getInitParameter(INIT_PARAM_NAMESPACE);
        if (namespace != null) {
        	f = StatsFactory.forNamespace(namespace);
        }

        String keyName = servletContext.getInitParameter(INIT_PARAM_KEY_NAME);
        if (keyName == null) {
        	keyName = getClass().getSimpleName();
        }

        StatsKey key = f.newKey(keyName);
        StatsKey ctxKey = key.buildCopy()
                             .withAttribute("servletContext", servletContextName)
                             .newKey();

        SpanTracker tracker = statsFactory.track(key, ctxKey);

        event.getSession()
             .setAttribute(ATTR_TRACKER, tracker);
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        SpanTracker tracker = (SpanTracker)event.getSession()
                                                .getAttribute(ATTR_TRACKER);
        if (tracker != null) {
            tracker.commit();

        } else {
            logger.warn("Missing request attribute; cannot track statistics: {}", ATTR_TRACKER);
        }
    }
}

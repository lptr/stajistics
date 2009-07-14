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
package org.stajistics.integration.servlet.http;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.tracker.StatsTracker;

/**
 *
 * @author The Stajistics Project
 */
public class StatsHttpSessionListener implements HttpSessionListener {

    private static final Logger logger = Logger.getLogger(StatsHttpSessionListener.class.getName());

    private static final String ATTR_TRACKER = StatsHttpSessionListener.class.getName() + "_tracker";

    private final StatsKey key;

    public StatsHttpSessionListener() {
        key = Stats.newKey(getClass().getSimpleName());
    }

    @Override
    public void sessionCreated(final HttpSessionEvent event) {

        HttpSession session = event.getSession();

        String servletContextName = session.getServletContext()
                                           .getServletContextName();

        StatsKey ctxKey = key.buildCopy()
                             .withAttribute("servletContext", servletContextName)
                             .newKey();

        StatsTracker tracker = Stats.track(key, ctxKey);

        event.getSession()
             .setAttribute(ATTR_TRACKER, tracker);
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        StatsTracker tracker = (StatsTracker)event.getSession()
                                                  .getAttribute(ATTR_TRACKER);
        if (tracker != null) {
            tracker.commit();

        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning("Missing request attribute; cannot track statistics: " + ATTR_TRACKER);
            }
        }
    }
}

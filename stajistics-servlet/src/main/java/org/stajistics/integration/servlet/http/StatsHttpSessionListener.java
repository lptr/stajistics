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

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

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
public class StatsHttpSessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(StatsHttpSessionListener.class);

    private static final String ATTR_TRACKER = StatsHttpSessionListener.class.getName() + "_tracker";

    private StatsKey key = Stats.newKey(getKeyName());

    protected String getKeyName() {
        return getClass().getSimpleName();
    }

    @Override
    public void sessionCreated(final HttpSessionEvent event) {
        StatsTracker tracker = Stats.track(key);
        event.getSession().setAttribute(ATTR_TRACKER, tracker);
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        StatsTracker tracker = (StatsTracker)event.getSession().getAttribute(ATTR_TRACKER);
        if (tracker != null) {
            tracker.commit();

        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("Missing request attribute; cannot track statistics: " + ATTR_TRACKER);
            }
        }
    }
}

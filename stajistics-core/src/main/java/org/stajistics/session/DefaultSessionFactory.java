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
package org.stajistics.session;

import org.stajistics.StatsKey;
import org.stajistics.StatsProperties;
import org.stajistics.session.recorder.DataRecorder;

/**
 * The default factory for creating {@link StatsSession} instances.
 *
 * @author The Stajistics Project
 */
public class DefaultSessionFactory implements StatsSessionFactory {

    private static DefaultSessionFactory INSTANCE = new DefaultSessionFactory();

    public static final String PROP_DEFAULT_SESSION_IMPL =
        StatsSessionFactory.class.getName() + ".defaultSessionImpl";

    private DefaultSessionFactory() {}

    public static DefaultSessionFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public StatsSession createSession(final StatsKey key,
                                      final DataRecorder[] dataRecorders) {

        StatsSessionFactory sessionFactory = ConcurrentSession.FACTORY;

        String sessionImpl = StatsProperties.getProperty(PROP_DEFAULT_SESSION_IMPL);
        if (sessionImpl != null && sessionImpl.length() > 0) {
            sessionImpl = sessionImpl.intern();

            if (sessionImpl == "concurrent") {
                sessionFactory = ConcurrentSession.FACTORY;
            } else if (sessionImpl == "asynchronous") {
                sessionFactory = AsynchronousSession.FACTORY;
            } else if (sessionImpl == "immutable") {
                sessionFactory = ImmutableSession.FACTORY;
            }
        }

        return sessionFactory.createSession(key, dataRecorders);
    }
}

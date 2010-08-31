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
import org.stajistics.StatsManager;
import org.stajistics.data.FieldSetFactory;
import org.stajistics.session.recorder.DataRecorder;

/**
 * The default factory for creating {@link StatsSession} instances.
 *
 * @author The Stajistics Project
 */
public class DefaultSessionFactory implements StatsSessionFactory {

    private static DefaultSessionFactory INSTANCE = new DefaultSessionFactory();

    private DefaultSessionFactory() {}

    public static DefaultSessionFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public StatsSession createSession(final StatsKey key,
                                      final StatsManager manager,
                                      final FieldSetFactory fieldSetFactory,
                                      final DataRecorder[] dataRecorders) {
        return new ConcurrentSession(key, 
                                          manager.getEventManager(), 
                                          fieldSetFactory,
                                          dataRecorders);
    }
}

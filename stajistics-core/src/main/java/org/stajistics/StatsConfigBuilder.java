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
package org.stajistics;

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface StatsConfigBuilder extends StatsKeyBuilder {

    @Override
    StatsConfigBuilder withAttribute(String name, Object value);

    StatsConfigBuilder withUnit(String unit);

    StatsConfigBuilder withTracker(Class<? extends StatsTracker> trackerClass);

    StatsConfigBuilder withSession(Class<? extends StatsSession> sessionClass);

}

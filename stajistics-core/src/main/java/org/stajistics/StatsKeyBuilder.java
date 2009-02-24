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

import java.util.Map;

import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface StatsKeyBuilder {

    StatsKeyBuilder withName(String name);

    StatsKeyBuilder withUnit(String unit);

    StatsKeyBuilder withAttributes(Map<String,Object> attributes);

    StatsKeyBuilder withAttribute(String name, Object value);

    StatsKeyBuilder withTracker(Class<? extends StatsTracker> trackerClass);

    StatsKeyBuilder withSession(Class<? extends StatsSession> sessionClass);

    StatsKey newKey();

}

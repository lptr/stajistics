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
package org.stajistics.management;

import java.io.Serializable;

import org.stajistics.StatsConfig;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.session.StatsSession;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public interface StatsMBeanFactory extends Serializable {

    StatsConfigManagerMBean createConfigManagerMBean(StatsManager statsManager);

    StatsConfigMBean createConfigMBean(StatsManager statsManager,
                                       StatsKey key,
                                       StatsConfig config);

    StatsSessionManagerMBean createSessionManagerMBean(StatsManager statsManager);

    StatsSessionMBean createSessionMBean(StatsManager statsManager,
                                         StatsSession session);
}

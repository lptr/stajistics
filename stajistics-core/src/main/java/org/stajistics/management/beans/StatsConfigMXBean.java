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
package org.stajistics.management.beans;

import javax.management.MXBean;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@MXBean
public interface StatsConfigMXBean {

    boolean getEnabled();

    void setEnabled(boolean enabled);

    String getUnit();

    void setUnit(String unit);

    String getDescription();

    void setDescription(String description);

    String getTrackerFactory();

    String getSessionFactory();

    void enableTree(boolean enabled);
}

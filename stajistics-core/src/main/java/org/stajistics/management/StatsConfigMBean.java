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
package org.stajistics.management;

import java.io.IOException;

import javax.management.MXBean;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
@MXBean
public interface StatsConfigMBean {

    boolean getEnabled() throws IOException;

    void setEnabled(boolean enabled) throws IOException;

    String getUnit() throws IOException;

    void setUnit(String unit) throws IOException;

    String getDescription() throws IOException;

    void setDescription(String description) throws IOException;

    String getTrackerFactory() throws IOException;

    String getSessionFactory() throws IOException;

}

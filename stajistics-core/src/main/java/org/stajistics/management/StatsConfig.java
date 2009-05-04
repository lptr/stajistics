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

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsConfig implements StatsConfigMBean {

    private final org.stajistics.StatsConfig config;

    public StatsConfig(final org.stajistics.StatsConfig config) {
        if (config == null) {
            throw new NullPointerException("config");
        }

        this.config = config;
    }

    @Override
    public boolean getEnabled() throws IOException {
        return config.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getUnit() throws IOException {
        return config.getUnit();
    }

    @Override
    public void setUnit(final String unit) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getDescription() throws IOException {
        return config.getDescription();
    }

    @Override
    public void setDescription(String description) throws IOException {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public String getTrackerFactory() throws IOException {
        return config.getTrackerFactory().toString();
    }

    @Override
    public String getSessionFactory() throws IOException {
        return config.getSessionFactory().toString();
    }

}

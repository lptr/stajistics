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
package org.stajistics;

import java.io.Serializable;

import org.stajistics.session.StatsSessionFactory;
import org.stajistics.session.recorder.DataRecorderFactory;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * A builder for immutable {@link StatsConfig} instances.
 *
 * @author The Stajistics Project
 */
public interface StatsConfigBuilder extends Serializable {

    /**
     * Build the configuration with the given enabled state.
     *
     * @param enabled The enabled state to use.
     * @return <tt>this</tt>
     */
    StatsConfigBuilder withEnabledState(boolean enabled);

    /**
     * Build the configuration with the given {@link StatsTrackerFactory}.
     *
     * @param trackerFactory The {@link StatsTrackerFactory} instance to use.
     * @return <tt>this</tt>.
     */
    StatsConfigBuilder withTrackerFactory(StatsTrackerFactory<?> trackerFactory);

    /**
     * Build the configuration with the given {@link StatsSessionFactory}.
     *
     * @param sessionFactory The {@link StatsSessionFactory} instance to use.
     * @return <tt>this</tt>.
     */
    StatsConfigBuilder withSessionFactory(StatsSessionFactory sessionFactory);

    /**
     * Build the configuration with the given {@link DataRecorderFactory}.
     *
     * @param dataRecorderFactory The {@link DataRecorderFactory} instance to use.
     * @return <tt>this</tt>.
     */
    StatsConfigBuilder withDataRecorderFactory(DataRecorderFactory dataRecorderFactory);

    /**
     * Build the configuration with the given <tt>unit</tt>.
     *
     * @param unit The unit to use.
     * @return <tt>this</tt>.
     */
    StatsConfigBuilder withUnit(String unit);

    /**
     * Build the configuration with the given <tt>description</tt>.
     *
     * @param description The description to use.
     * @return <tt>this</tt>.
     */
    StatsConfigBuilder withDescription(String description);

    /**
     * Create a new immutable {@link StatsConfig} instance based on the builders currently
     * configured state. Can be invoked multiple times and the state can be modified further
     * in between invocations.
     *
     * @return A {@link StatsConfig} instance, never <tt>null</tt>.
     */
    StatsConfig newConfig();

    /**
     * Set the result of {@link #newConfig()} with the given <tt>key</tt> on the
     * {@link StatsConfigManager} that is associated with this builder instance.
     *
     * @param key The {@link StatsKey} to be associated with the new {@link StatsConfig}.
     */
    void setConfigFor(StatsKey key);

}

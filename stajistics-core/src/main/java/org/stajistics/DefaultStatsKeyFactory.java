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


/**
 * The default implementation of {@link StatsKeyFactory}. Do not
 * instantiate this class directly. Instead use {@link StatsManager#getKeyFactory()}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsKeyFactory implements StatsKeyFactory {

    private static final long serialVersionUID = 1231029297050473946L;

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKey createKey(final String name) {
        return new SimpleStatsKey(name, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder createKeyBuilder(final String name) {
        return new DefaultStatsKeyBuilder(name, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsKeyBuilder createKeyBuilder(final StatsKey template) {
        return new DefaultStatsKeyBuilder(template, this);
    }

}

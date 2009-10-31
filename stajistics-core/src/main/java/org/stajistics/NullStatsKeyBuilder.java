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
 * A singleton {@link StatsKeyBuilder} implementation conforming to the null object pattern.
 *
 * @author The Stajistics Project
 */
public final class NullStatsKeyBuilder implements StatsKeyBuilder {

    private static final long serialVersionUID = 8942085184706941947L;

    private static final NullStatsKeyBuilder INSTANCE = new NullStatsKeyBuilder();

    private NullStatsKeyBuilder() {}

    public static NullStatsKeyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public StatsKey newKey() {
        return NullStatsKey.getInstance();
    }

    @Override
    public StatsKeyBuilder withAttribute(String name, String value) {
        return this;
    }

    @Override
    public StatsKeyBuilder withAttribute(String name, Boolean value) {
        return this;
    }

    @Override
    public StatsKeyBuilder withAttribute(String name, Integer value) {
        return this;
    }

    @Override
    public StatsKeyBuilder withAttribute(String name, Long value) {
        return this;
    }

    @Override
    public StatsKeyBuilder withNameSuffix(String nameSuffix) {
        return this;
    }

    @Override
    public StatsKeyBuilder withoutAttribute(String name) {
        return this;
    }
}

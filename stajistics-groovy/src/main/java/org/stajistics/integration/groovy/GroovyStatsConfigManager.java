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
package org.stajistics.integration.groovy;

import java.util.Map;
import java.util.Set;

import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyMatcher;

/**
 * 
 * @author The Stajistics Project
 */
public class GroovyStatsConfigManager implements StatsConfigManager {

    private final StatsConfigManager delegate;

    private final StatsConfigDSLInterpreter interp = new StatsConfigDSLInterpreter();

    public GroovyStatsConfigManager(final StatsConfigManager delegate) {
        this(delegate, null);
    }

    public GroovyStatsConfigManager(final StatsConfigManager delegate,
                                    final String dsl) {
        if (delegate == null) {
            throw new NullPointerException("delegate");
        }

        this.delegate = delegate;

        if (dsl != null) {
            setConfigs(dsl);
        }
    }

    public void setConfigs(final CharSequence dsl) {
        Map<String,StatsConfig> configMap = interp.eval(dsl);
        for (Map.Entry<String,StatsConfig> entry : configMap.entrySet()) {
            delegate.setConfig(entry.getKey(), entry.getValue());
        }
    }

    public void clearConfigs() {
        delegate.clearConfigs();
    }

    public StatsConfig getConfig(final StatsKey key) {
        return delegate.getConfig(key);
    }

    public Set<String> getKeyNames() {
        return delegate.getKeyNames();
    }

    public StatsConfig getOrCreateConfig(final StatsKey key) {
        return delegate.getOrCreateConfig(key);
    }

    public StatsConfig getRootConfig() {
        return delegate.getRootConfig();
    }

    public StatsConfig removeConfig(final String keyName) {
        return delegate.removeConfig(keyName);
    }

    public StatsConfig removeConfig(final StatsKey key) {
        return delegate.removeConfig(key);
    }

    public void setConfig(final String keyName, final String dsl) {
        delegate.setConfig(keyName, interp.evalConfig(dsl));
    }

    public void setConfig(final StatsKey key, final String dsl) {
        delegate.setConfig(key, interp.evalConfig(dsl));
    }

    public void setConfig(final String keyName, final StatsConfig config) {
        delegate.setConfig(keyName, config);
    }

    public void setConfig(final StatsKey key, final StatsConfig config) {
        delegate.setConfig(key, config);
    }

    public void setRootConfig(final StatsConfig config) {
        delegate.setRootConfig(config);
    }

    public int getConfigCount() {
        return delegate.getConfigCount();
    }

    public Map<StatsKey, StatsConfig> getConfigs() {
        return delegate.getConfigs();
    }

    public Map<StatsKey,StatsConfig> getConfigs(final StatsKeyMatcher matcher) {
        return delegate.getConfigs(matcher);
    }
}

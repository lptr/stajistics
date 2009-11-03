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
package org.stajistics.tracker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class CompositeStatsTrackerFactory implements StatsTrackerFactory<StatsTracker> {

    private static final long serialVersionUID = -423223606572241980L;

    private final Map<String,StatsTrackerFactory<? extends StatsTracker>> factoryMap;
    private final String[] nameSuffixes;
    private final StatsTrackerFactory<StatsTracker>[] factories;

    @SuppressWarnings("unchecked")
    public CompositeStatsTrackerFactory(final Map<String,StatsTrackerFactory<? extends StatsTracker>> factoryMap) {
        if (factoryMap == null) {
            throw new NullPointerException("factoryMap");
        }
        if (factoryMap.isEmpty()) {
            throw new IllegalArgumentException("factoryMap is empty");
        }

        this.factoryMap = factoryMap;

        int size = factoryMap.size();

        nameSuffixes = new String[size];
        factories = new StatsTrackerFactory[size];

        int i = 0;

        for (Map.Entry<String,StatsTrackerFactory<? extends StatsTracker>> entry : factoryMap.entrySet()) {
            nameSuffixes[i] = entry.getKey();
            factories[i] = (StatsTrackerFactory)entry.getValue();

            if (factories[i] == null) {
                throw new IllegalArgumentException("null factory for nameSuffix: " + nameSuffixes[i]);
            }

            i++;
        }
    }

    public Map<String,StatsTrackerFactory<? extends StatsTracker>> getFactoryMap() {
        return Collections.unmodifiableMap(factoryMap);
    }

    public static Builder build() {
        return new Builder();
    }

    @Override
    public StatsTracker createTracker(final StatsKey key,
                                      final StatsSessionManager sessionManager) {

        StatsTracker[] trackers = new StatsTracker[factories.length];

        for (int i = 0; i < trackers.length; i++) {
            StatsKey childKey = key.buildCopy()
                                   .withNameSuffix(nameSuffixes[i])
                                   .newKey();

            trackers[i] = factories[i].createTracker(childKey, sessionManager);
        }

        throw new UnsupportedOperationException("not yet implemented");

        //return new CompositeStatsTracker(trackers);
    }

    public static class Builder {

        private final Map<String,StatsTrackerFactory<? extends StatsTracker>> factoryMap = 
            new HashMap<String,StatsTrackerFactory<? extends StatsTracker>>();

        protected Builder() {}

        public Builder withFactory(final String nameSuffix,
                                   final StatsTrackerFactory<? extends StatsTracker> factory) {
            if (nameSuffix == null) {
                throw new NullPointerException("nameSuffix");
            }
            if (nameSuffix.length() == 0) {
                throw new IllegalArgumentException("nameSuffix is empty");
            }
            if (factory == null) {
                throw new NullPointerException("factory");
            }

            factoryMap.put(nameSuffix, factory);

            return this;
        }

        public StatsTrackerFactory<?> build() {
            return new CompositeStatsTrackerFactory(factoryMap);
        }
    }

}

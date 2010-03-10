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
package org.stajistics.tracker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.stajistics.StatsKey;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.incident.IncidentCompositeStatsTracker;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.manual.ManualCompositeStatsTracker;
import org.stajistics.tracker.manual.ManualTracker;
import org.stajistics.tracker.span.SpanCompositeStatsTracker;
import org.stajistics.tracker.span.SpanTracker;
import org.stajistics.util.Composite;

/**
 *
 *
 * @author The Stajistics Project
 */
public class CompositeStatsTrackerFactory<T extends StatsTracker> 
        implements StatsTrackerFactory<T>,Composite<StatsTrackerFactory<T>> {

    private final Map<String,StatsTrackerFactory<T>> factoryMap;
    private final String[] nameSuffixes;
    private final StatsTrackerFactory<T>[] factories;

    @SuppressWarnings("unchecked")
    public CompositeStatsTrackerFactory(final Map<String,StatsTrackerFactory<T>> factoryMap) {
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

        for (Map.Entry<String,StatsTrackerFactory<T>> entry : factoryMap.entrySet()) {
            nameSuffixes[i] = entry.getKey();
            factories[i] = (StatsTrackerFactory)entry.getValue();

            if (factories[i] == null) {
                throw new IllegalArgumentException("null factory for nameSuffix: " + nameSuffixes[i]);
            }

            i++;
        }
    }

    public Map<String,StatsTrackerFactory<T>> getFactoryMap() {
        return Collections.unmodifiableMap(factoryMap);
    }

    @Override
    public Collection<StatsTrackerFactory<T>> composites() {
        return Collections.unmodifiableCollection(factoryMap.values());
    }

    public static <T extends StatsTracker> Builder<T> build() {
        return new Builder<T>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createTracker(final StatsKey key,
                           final StatsSessionManager sessionManager) {

        StatsTracker[] trackers = new StatsTracker[factories.length];

        for (int i = 0; i < trackers.length; i++) {
            StatsKey childKey = key.buildCopy()
                                   .withNameSuffix(nameSuffixes[i])
                                   .newKey();

            trackers[i] = factories[i].createTracker(childKey, sessionManager);
        }

        Class<T> trackerType = factories[0].getTrackerType();
        if (trackerType == SpanTracker.class) {
            return (T) new SpanCompositeStatsTracker((SpanTracker[])trackers);
        }
        if (trackerType == IncidentTracker.class) {
            return (T) new IncidentCompositeStatsTracker((IncidentTracker[])trackers);
        }
        if (trackerType == ManualTracker.class) {
            return (T) new ManualCompositeStatsTracker((ManualTracker[])trackers);
        }

        throw new UnsupportedOperationException("Unsupported tracker type: " + trackerType);
    }

    @Override
    public Class<T> getTrackerType() {
        return factories[0].getTrackerType();
    }

    public static class Builder<T extends StatsTracker> {

        private final Map<String,StatsTrackerFactory<T>> factoryMap =
            new HashMap<String,StatsTrackerFactory<T>>();

        public Builder<T> withFactory(final String nameSuffix,
                                      final StatsTrackerFactory<T> factory) {
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

        public CompositeStatsTrackerFactory<T> createCompositeTrackerFactory() {
            return new CompositeStatsTrackerFactory<T>(factoryMap);
        }
    }

}

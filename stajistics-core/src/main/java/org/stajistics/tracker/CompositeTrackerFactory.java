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
import org.stajistics.tracker.incident.CompositeIncidentTracker;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.span.CompositeSpanTracker;
import org.stajistics.tracker.span.SpanTracker;
import org.stajistics.util.Composite;

/**
 *
 *
 * @author The Stajistics Project
 */
public class CompositeTrackerFactory<T extends Tracker> 
        implements TrackerFactory<T>,Composite<TrackerFactory<T>> {

    private final Map<String,TrackerFactory<T>> factoryMap;
    private final String[] nameSuffixes;
    private final TrackerFactory<T>[] factories;

    @SuppressWarnings("unchecked")
    public CompositeTrackerFactory(final Map<String,TrackerFactory<T>> factoryMap) {
        if (factoryMap == null) {
            throw new NullPointerException("factoryMap");
        }
        if (factoryMap.isEmpty()) {
            throw new IllegalArgumentException("factoryMap is empty");
        }

        this.factoryMap = factoryMap;

        int size = factoryMap.size();

        nameSuffixes = new String[size];
        factories = new TrackerFactory[size];

        int i = 0;

        for (Map.Entry<String,TrackerFactory<T>> entry : factoryMap.entrySet()) {
            nameSuffixes[i] = entry.getKey();
            factories[i] = (TrackerFactory)entry.getValue();

            if (factories[i] == null) {
                throw new IllegalArgumentException("null factory for nameSuffix: " + nameSuffixes[i]);
            }

            i++;
        }
    }

    public Map<String,TrackerFactory<T>> getFactoryMap() {
        return Collections.unmodifiableMap(factoryMap);
    }

    @Override
    public Collection<TrackerFactory<T>> composites() {
        return Collections.unmodifiableCollection(factoryMap.values());
    }

    public static <T extends Tracker> Builder<T> build() {
        return new Builder<T>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createTracker(final StatsKey key,
                           final StatsSessionManager sessionManager) {

        Tracker[] trackers = new Tracker[factories.length];

        for (int i = 0; i < trackers.length; i++) {
            StatsKey childKey = key.buildCopy()
                                   .withNameSuffix(nameSuffixes[i])
                                   .newKey();

            trackers[i] = factories[i].createTracker(childKey, sessionManager);
        }

        Class<T> trackerType = factories[0].getTrackerType();
        if (trackerType == SpanTracker.class) {
            return (T) new CompositeSpanTracker((SpanTracker[])trackers);
        }
        if (trackerType == IncidentTracker.class) {
            return (T) new CompositeIncidentTracker((IncidentTracker[])trackers);
        }

        throw new UnsupportedOperationException("Unsupported tracker type: " + trackerType);
    }

    @Override
    public Class<T> getTrackerType() {
        return factories[0].getTrackerType();
    }

    public static class Builder<T extends Tracker> {

        private final Map<String,TrackerFactory<T>> factoryMap =
            new HashMap<String,TrackerFactory<T>>();

        public Builder<T> withFactory(final String nameSuffix,
                                      final TrackerFactory<T> factory) {
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

        public CompositeTrackerFactory<T> createCompositeTrackerFactory() {
            return new CompositeTrackerFactory<T>(factoryMap);
        }
    }

}

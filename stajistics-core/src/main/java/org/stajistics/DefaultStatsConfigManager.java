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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.stajistics.event.StatsEventManager;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.tracker.TimeDurationTracker;

/**
 * The default implementation of {@link StatsConfigManager}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManager implements StatsConfigManager {

    private ConcurrentMap<String,KeyEntry> keyMap = 
        new ConcurrentHashMap<String,KeyEntry>(128, 0.75f, 32);

    private final KeyEntry rootKeyEntry;

    private final Lock updateLock = new ReentrantLock();

    public DefaultStatsConfigManager() {
        this(createDefaultConfig(), null);
    }

    public DefaultStatsConfigManager(final StatsConfig rootConfig,
                                     final Map<String,StatsConfig> configMap) {
        if (rootConfig == null) {
            throw new NullPointerException("rootConfig");
        }

        rootKeyEntry = new KeyEntry(new SimpleStatsKey(""),
                                    null, 
                                    rootConfig);

        if (configMap != null) {
            for (Map.Entry<String,StatsConfig> entry : configMap.entrySet()) {
                setConfig(new SimpleStatsKey(entry.getKey()), entry.getValue());
            }
        }
    }

    private static StatsConfig createDefaultConfig() {
        return new DefaultStatsConfig(true,
                                      TimeDurationTracker.FACTORY,
                                      DefaultSessionFactory.getInstance(), 
                                      StatsConstants.DEFAULT_UNIT,
                                      null);
    }

    @Override
    public StatsConfig getRootConfig() {
        return rootKeyEntry.getConfig();
    }

    @Override
    public void setRootConfig(StatsConfig config) {
        if (config == null) {
            config = createDefaultConfig();
        }

        updateEntry(rootKeyEntry, config, false);
    }

    @Override
    public Set<String> getKeyNames() {
        return keyMap.keySet();
    }

    @Override
    public void setConfig(final StatsKey key, final StatsConfig config) {
        if (key == null) {
            throw new NullPointerException("key");
        }

        updateLock.lock();

        KeyEntry entry = entryFor(key);
        if (entry == null) {
            createEntry(key, config, true, true);
        } else {
            updateEntry(entry, config, true);
        }

        // updateLock is unlock()ed by createEntry or updateEntry (before events are fired)
    }

    @Override
    public StatsConfig getConfig(final StatsKey key) {
        KeyEntry entry = entryFor(key);
        if (entry == null) {
            return null;
        }

        return entry.getConfig();
    }

    @Override
    public StatsConfig getOrCreateConfig(StatsKey key) {
        KeyEntry entry = entryFor(key);
        if (entry == null) {
            entry = createEntry(key, null, false, false);
        }

        return entry.getConfig();
    }

    @Override
    public StatsConfig removeConfig(final StatsKey key) {
        KeyEntry entry = entryFor(key);
        if (entry != null) {
            StatsConfig config = entry.getConfig();
            destroyEntry(entry);
            return config;
        }

        return null;
    }

    @Override
    public void clearConfigs() {
        destroyEntry(rootKeyEntry);
    }

    /* PRIVATE METHODS */

    private KeyEntry entryFor(final StatsKey key) {
        return keyMap.get(key.getName());
    }

    private String parentKeyName(final String keyName) {

        String parentKeyName = null;

        int i = keyName.lastIndexOf('.');
        if (i > -1) {
            parentKeyName = keyName.substring(0, i);
        }

        return parentKeyName;
    }

    private KeyEntry createEntry(final StatsKey key, 
                                 final StatsConfig config,
                                 final boolean updateConfig, 
                                 final boolean updateLocked) {

        boolean created = false;
        boolean goingUp = true;

        String keyName = key.getName();
        KeyEntry entry = null;

        LinkedList<String> stack = new LinkedList<String>();

        if (!updateLocked) {
            updateLock.lock();
        }

        try {
            for (;;) {
                if (goingUp) {
                    entry = keyMap.get(keyName);
                    if (entry == null) {
                        String parentKeyName = parentKeyName(keyName);
                        stack.addLast(keyName);
                        keyName = parentKeyName;
                        if (keyName == null) {
                            entry = rootKeyEntry;
                            goingUp = false;
                        }

                    } else {
                        goingUp = false;
                    }

                } else {

                    keyName = stack.pollLast();
                    if (keyName == null) {
                        break;
                    }

                    StatsConfig theConfig = null;
                    if (stack.isEmpty()) {
                        // Only set the real config on the deepest entry
                        theConfig = config;
                    }

                    KeyEntry parentEntry = entry;
                    entry = new KeyEntry(new SimpleStatsKey(keyName), parentEntry, theConfig);
                    parentEntry.childEntries.add(entry);

                    keyMap.put(keyName, entry);
                    created = true;
                }
            }
        } finally {
            updateLock.unlock();
        }

        if (created) {
            Stats.getEventManager()
                 .fireEvent(StatsEventType.CONFIG_CREATED, key, entry.getConfig());
        }

        return entry;
    }

    private void updateEntry(KeyEntry entry, 
                             final StatsConfig config,
                             final boolean updateLocked) {
        Iterator<KeyEntry> entryItr = null;

        if (!updateLocked) {
            updateLock.lock();
        }

        try {
            KeyEntryConfigUpdater configUpdater = new KeyEntryConfigUpdater(config);
            entry.visit(configUpdater);
            entryItr = configUpdater.iterator();

        } finally {
            updateLock.unlock();
        }

        if (entryItr != null) {
            StatsEventManager eventManager = Stats.getEventManager();

            while (entryItr.hasNext()) {
                entry = entryItr.next();

                eventManager.fireEvent(StatsEventType.CONFIG_CHANGED,
                                       entry.getKey(), 
                                       entry.getConfig());
            }
        }
    }

    private void destroyEntry(KeyEntry entry) {
        Iterator<KeyEntry> entryItr;

        updateLock.lock();
        try {
            KeyEntryDestroyer entryDestroyer = new KeyEntryDestroyer(keyMap);
            entry.visit(entryDestroyer);
            entryItr = entryDestroyer.iterator();

        } finally {
            updateLock.unlock();
        }

        StatsEventManager eventManager = Stats.getEventManager();

        while (entryItr.hasNext()) {
            entry = entryItr.next();

            eventManager.fireEvent(StatsEventType.CONFIG_DESTROYED, 
                                   entry.getKey(), 
                                   entry.getConfig());
        }
    }

}


final class KeyEntry {

    private final StatsKey key;

    KeyEntry parentEntry;

    final List<KeyEntry> childEntries = new LinkedList<KeyEntry>();

    private boolean configInherited;
    private final AtomicReference<StatsConfig> config = new AtomicReference<StatsConfig>(null);

    KeyEntry(final StatsKey key,
             final KeyEntry parentEntry, 
             final StatsConfig config) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        this.key = key;
        this.parentEntry = parentEntry;

        setConfig(config);
    }

    StatsKey getKey() {
        return key;
    }

    void visit(final KeyEntry.Visitor visitor) {
        if (visitor.visit(this)) {
            for (KeyEntry childEntry : new ArrayList<KeyEntry>(childEntries)) {
                childEntry.visit(visitor);
            }
        }
    }

    boolean isRoot() {
        return parentEntry == null;
    }

    boolean isConfigInherited() {
        return configInherited;
    }

    /**
     * 
     * @param config
     * @return <tt>true</tt> if the config changed;
     */
    boolean setConfig(final StatsConfig config) {
        StatsConfig existingConfig = this.config.get();

        if (config == null) {
            this.config.set(findInheritedConfig());
            configInherited = true;
        } else {
            this.config.set(config);
            configInherited = false;
        }

        boolean changed = false;
        if (existingConfig != null && !existingConfig.equals(this.config.get())) {
            changed = true;
        }

        return changed;
    }

    StatsConfig getConfig() {
        return this.config.get();
    }

    private StatsConfig findInheritedConfig() {
        StatsConfig result = null;

        KeyEntry entry = this.parentEntry;
        while (entry != null) {
            result = entry.config.get();
            if (result != null) {
                break;
            }

            entry = entry.parentEntry;
        }

        if (result == null) {
            throw new Error();
        }

        return result;
    }

    static interface Visitor {
        boolean visit(KeyEntry entry);
    }

}


final class KeyEntryDestroyer implements KeyEntry.Visitor {

    private final Map<String,KeyEntry> keyMap;
    private final List<KeyEntry> entryList = new LinkedList<KeyEntry>();

    KeyEntryDestroyer(final Map<String,KeyEntry> keyMap) {
        if (keyMap == null) {
            throw new NullPointerException("keyMap");
        }
        this.keyMap = keyMap;
    }

    @Override
    public boolean visit(final KeyEntry entry) {

        keyMap.remove(entry.getKey().getName());

        entryList.add(entry);

        if (entry.parentEntry != null) {
            entry.parentEntry.childEntries.remove(entry);
        }

        entry.parentEntry = null;

        return true;
    }

    public Iterator<KeyEntry> iterator() {
        return entryList.iterator();
    }
}


final class KeyEntryConfigUpdater implements KeyEntry.Visitor {

    private final List<KeyEntry> entryList = new LinkedList<KeyEntry>();
    private final StatsConfig config;
    private boolean first = true;

    KeyEntryConfigUpdater(final StatsConfig config) {
        this.config = config;
    }

    @Override
    public boolean visit(final KeyEntry entry) {

        // Only set the new config on the most shallow entry
        if (first) {
            if (!entry.setConfig(config)) {
                // If the config hasn't changed, don't bother scanning/updating the children
                return false;
            }

            entryList.add(entry);
            first = false;

            return true;
        }

        if (entry.isConfigInherited()) {
            // Bubble down inherited config
            if (entry.setConfig(null)) {
                entryList.add(entry);
            }
        }

        return true;
    }

    public Iterator<KeyEntry> iterator() {
        return entryList.iterator();
    }
}


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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.stajistics.event.StatsEventType;

/**
 * 
 * TODO: The current implementation is functional but does not support child key update notification
 * (i.e. if root config changes, all keys inheriting from it should have config_changed events fired.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManager implements StatsConfigManager {
    private ConcurrentMap<String,KeyEntry> keyMap = 
        new ConcurrentHashMap<String,KeyEntry>(128, 0.75f, 32);

    private final KeyEntry rootKeyEntry = new KeyEntry(new SimpleStatsKey(""),
                                                       null, 
                                                       DefaultStatsConfig.createDefaultConfig());

    private final Lock updateLock = new ReentrantLock();

    public DefaultStatsConfigManager() {

    }

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

    @Override
    public void clearConfigs() {
        updateLock.lock();
        try {
            keyMap.clear();

        } finally {
            updateLock.unlock();
        }
    }

    @Override
    public StatsConfig getRootConfig() {
        return rootKeyEntry.getConfig();
    }

    @Override
    public void setRootConfig(final StatsConfig config) {
        rootKeyEntry.setConfig(config);

        Stats.getEventManager()
             .fireEvent(StatsEventType.CONFIG_CHANGED, rootKeyEntry.key, config);
    }

    @Override
    public Set<String> getKeyNames() {
        return keyMap.keySet();
    }

    @Override
    public void register(final StatsKey key, final StatsConfig config) {
        KeyEntry entry = keyMap.get(key.getName());
        if (entry == null) {
            doRegister(key, true, config);
        } else {
            if (entry.setConfig(config)) {
                Stats.getEventManager()
                     .fireEvent(StatsEventType.CONFIG_CHANGED, key, config);
            }
        }
    }

    private KeyEntry doRegister(final StatsKey key, final boolean updateConfig, final StatsConfig config) {

        StatsConfig theConfig = config;

        boolean created = false;
        boolean updated = false;

        boolean goingUp = true;

        String keyName = key.getName();
        KeyEntry entry = null;

        LinkedList<String> stack = new LinkedList<String>();

        updateLock.lock();
        try {
            for (;;) {
                if (goingUp) {
                    if (entry == null) {
                        String parentKeyName = parentKeyName(keyName);
                        stack.addLast(keyName);
                        keyName = parentKeyName;
                        if (keyName == null) {
                            goingUp = false;
                        }

                    } else {
                        if (stack.isEmpty() && updateConfig) {
                            // deepest entry exists, update it
                            updated = entry.setConfig(theConfig);
                        }

                        goingUp = false;
                    }

                } else {

                    keyName = stack.pollLast();
                    if (keyName == null) {
                        break;
                    }

                    if (entry == null) {
                        entry = rootKeyEntry;
                    }

                    // replace current entry with new child
                    entry = new KeyEntry(new SimpleStatsKey(keyName), entry, theConfig);
                    keyMap.put(keyName, entry);

                    if (updateConfig) {
                        created = true;
                    }

                    theConfig = null; // only set the config on the deepest entry
                }
            }
        } finally {
            updateLock.unlock();
        }

        if (created) {
            Stats.getEventManager()
                 .fireEvent(StatsEventType.CONFIG_CREATED, key, config);
        }

        if (updated) {
            Stats.getEventManager()
                 .fireEvent(StatsEventType.CONFIG_CHANGED, key, config);
        }

        return entry;
    }

    @Override
    public StatsConfig getConfig(final StatsKey key) {
        KeyEntry entry = entryFor(key);
        if (entry == null) {
            entry = doRegister(key, false, null);
        }

        return entry.getConfig();
    }

}

class KeyEntry {

    final StatsKey key;
    private KeyEntry parentEntry;

    List<StatsKey> childKeys; //TODO: currently unused

    // null means inherit parent config
    private final AtomicReference<StatsConfig> config = new AtomicReference<StatsConfig>(null);

    KeyEntry(final StatsKey key,
             final KeyEntry parentEntry, 
             final StatsConfig config) {

        if (key == null) {
            throw new NullPointerException("key");
        }

        this.key = key;
        this.parentEntry = parentEntry;
        this.config.set(config);
    }

    boolean isRoot() {
        return parentEntry == null;
    }

    /**
     * 
     * @param config
     * @return <tt>true</tt> if the config changed;
     */
    boolean setConfig(final StatsConfig config) {
        return (this.config.getAndSet(config).equals(config));
    }

    StatsConfig getConfig() {
        StatsConfig config = this.config.get();

        if (config == null) {
            config = findParentConfig();
        }

        return config;
    }

    private StatsConfig findParentConfig() {
        StatsConfig result = null;

        KeyEntry entry = this.parentEntry;
        while (entry != null) {
            result = entry.config.get();
            if (result != null) {
                break;
            }

            entry = entry.parentEntry;
        }

        return result;
    }

}

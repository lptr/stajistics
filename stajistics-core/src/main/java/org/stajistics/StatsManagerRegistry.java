package org.stajistics;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagerRegistry {

    private static Map<String,StatsManager> registry = new ConcurrentHashMap<String,StatsManager>();

    private static final StatsManagerRegistry INSTANCE = new StatsManagerRegistry();

    private StatsManagerRegistry() {}

    public static StatsManagerRegistry getInstance() {
        return INSTANCE;
    }

    public int getStatsManagerCount() {
        return registry.size();
    }

    public Collection<StatsManager> getStatsManagers() {
        return Collections.unmodifiableCollection(registry.values());
    }

    public void registerStatsManager(final StatsManager manager) {
        registry.put(manager.getNamespace(), manager);
    }

    public void removeStatsManager(final StatsManager manager) {
        removeStatsManager(manager.getNamespace());
    }

    public void removeStatsManager(final String namespace) {
        if (namespace == null) {
            throw new NullPointerException("namespace");
        }
        registry.remove(namespace);
    }

    public boolean isStatsManagerDefined(final String namespace) {
        return registry.containsKey(namespace);
    }

    public StatsManager getStatsManager(final String namespace) {
        StatsManager statsManager = registry.get(namespace);
        if (statsManager == null) {
            throw new StatsNamespaceNotFoundException(namespace);
        }
        return statsManager;
    }

    public void clear() { 
        registry.clear();
    }
}

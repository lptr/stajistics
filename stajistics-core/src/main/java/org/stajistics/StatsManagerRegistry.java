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

    public static int getStatsManagerCount() {
        return registry.size();
    }

    public static Collection<StatsManager> getStatsManagers() {
        return Collections.unmodifiableCollection(registry.values());
    }

    public static void registerStatsManager(final StatsManager manager) {
        registry.put(manager.getNamespace(), manager);
    }

    public static void removeStatsManager(final StatsManager manager) {
        registry.remove(manager.getNamespace());
    }

    public static StatsManager getStatsManager(final String namespace) {
        return registry.get(namespace);
    }
}

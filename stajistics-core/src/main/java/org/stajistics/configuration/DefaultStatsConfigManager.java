package org.stajistics.configuration;

import static org.stajistics.Util.assertNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.stajistics.NullStatsKey;
import org.stajistics.StatsConstants;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyAssociation;
import org.stajistics.StatsKeyFactory;
import org.stajistics.StatsKeyMatcher;
import org.stajistics.StatsKeyUtil;
import org.stajistics.StatsProperties;
import org.stajistics.event.EventManager;
import org.stajistics.event.EventType;
import org.stajistics.session.DefaultSessionFactory;
import org.stajistics.session.recorder.DefaultDataRecorderFactory;
import org.stajistics.tracker.span.TimeDurationTracker;
import org.stajistics.util.ServiceLifeCycle;

/**
 * The default implementation of {@link org.stajistics.configuration.StatsConfigManager}. Clients typically do not instantiate
 * this class directly. Instead use {@link org.stajistics.Stats#getConfigManager()}.
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManager implements StatsConfigManager {

    public static final String PROP_INITIAL_CAPACITY =
        StatsConfigManager.class.getName() + ".initialCapacity";
    public static final String PROP_LOAD_FACTOR =
        StatsConfigManager.class.getName() + ".loadFactor";
    public static final String PROP_CONCURRENCY_LEVEL =
        StatsConfigManager.class.getName() + ".concurrencyLevel";

    private final ConcurrentMap<String,KeyEntry> keyMap = createKeyEntryMap();

    private final KeyEntry rootKeyEntry;

    private final Lock updateLock = new ReentrantLock();

    private final EventManager eventManager;
    private final StatsKeyFactory keyFactory;

    private final ServiceLifeCycle.Support lifeCycleSupport = new Support();

    /**
     * Create a new DefaultStatsConfigManager instance containing no initial configurations.
     *
     * @param eventManager The {@link org.stajistics.event.EventManager} with which to fire configuration events.
     * @param keyFactory The {@link org.stajistics.StatsKeyFactory} with which to create keys.
     *
     * @throws NullPointerException If <tt>eventManager</tt> or <tt>keyFactory</tt> is <tt>null</tt>.
     */
    public DefaultStatsConfigManager(final EventManager eventManager,
                                     final StatsKeyFactory keyFactory) {
        this(eventManager, keyFactory, createDefaultConfig(), null);
    }

    /**
     * Create a new DefaultStatsConfigManager instance supplying initial configuration.
     * If the passed <tt>configMap</tt> is neither a {@link java.util.SortedMap} nor a {@link java.util.LinkedHashMap},
     * it is transferred into a {@link java.util.TreeMap} in order to ensure a predictable entry extraction order.
     *
     * @param eventManager The {@link org.stajistics.event.EventManager} with which to fire configuration events.
     * @param keyFactory The {@link org.stajistics.StatsKeyFactory} with which to create keys.
     * @param rootConfig The root {@link org.stajistics.configuration.StatsConfig} from which to inherit in the absence of configuration.
     * @param configMap A Map of key names to configurations. May be <tt>null</tt>.
     *
     * @throws NullPointerException If <tt>eventManager</tt>, <tt>keyFactory</tt>,
     *                              or <tt>rootConfig</tt> is <tt>null</tt>.
     */
    public DefaultStatsConfigManager(final EventManager eventManager,
                                     final StatsKeyFactory keyFactory,
                                     final StatsConfig rootConfig,
                                     Map<String,StatsConfig> configMap) {
        assertNotNull(eventManager, "eventManager");
        assertNotNull(keyFactory, "keyFactory");
        assertNotNull(rootConfig, "rootConfig");

        this.eventManager = eventManager;
        this.keyFactory = keyFactory;

        rootKeyEntry = new KeyEntry(NullStatsKey.getInstance(),
                                    null,
                                    rootConfig);

        if (configMap != null) {
            // If the configMap is not already intentionally ordered, sort it to ensure
            // predictable extraction. This is important because it affects the kinds of events
            // generated (i.e. CONFIG_CREATED vs. CONFIG_CHANGED)
            if (!(configMap instanceof SortedMap<?,?>) && !(configMap instanceof LinkedHashMap<?,?>)) {
                configMap = new TreeMap<String,StatsConfig>(configMap);
            }

            for (Map.Entry<String,StatsConfig> entry : configMap.entrySet()) {
                setConfig(keyFactory.createKey(entry.getKey()), entry.getValue());
            }
        }
    }

    private static StatsConfig createDefaultConfig() {
        return new DefaultStatsConfig(true,
                                      TimeDurationTracker.FACTORY,
                                      DefaultSessionFactory.getInstance(),
                                      DefaultDataRecorderFactory.getInstance(),
                                      StatsConstants.DEFAULT_UNIT,
                                      null);
    }

    /**
     * A factory method hook for subclasses to override the {@link java.util.concurrent.ConcurrentMap} implementation
     * to be used for storing entries.
     *
     * @return A {@link java.util.concurrent.ConcurrentMap}, never <tt>null</tt>.
     */
    protected ConcurrentMap<String,KeyEntry> createKeyEntryMap() {
        int initialCapacity = StatsProperties.getIntegerProperty(PROP_INITIAL_CAPACITY, 256);
        float loadFactor = StatsProperties.getFloatProperty(PROP_LOAD_FACTOR, 0.6f);
        int concurrencyLevel = StatsProperties.getIntegerProperty(PROP_CONCURRENCY_LEVEL, 64);

        return new ConcurrentHashMap<String,KeyEntry>(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public void initialize() {
        lifeCycleSupport.initialize(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                eventManager.fireEvent(EventType.CONFIG_MANAGER_INITIALIZED, null, DefaultStatsConfigManager.this);
                return null;
            }
        });
    }

    @Override
    public boolean isRunning() {
        return lifeCycleSupport.isRunning();
    }

    @Override
    public synchronized void shutdown() {
        lifeCycleSupport.shutdown(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                eventManager.fireEvent(EventType.CONFIG_MANAGER_SHUTTING_DOWN, null, DefaultStatsConfigManager.this);
                clearConfigs();
                return null;
            }
        });
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
    public int getConfigCount() {
        return keyMap.size();
    }

    @Override
    public void setConfig(final String keyName, final StatsConfig config) {
        setConfig(keyFactory.createKey(keyName), config);
    }

    @Override
    public void setConfig(final StatsKey key, final StatsConfig config) {
        assertNotNull(key, "key");
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
    public StatsConfig removeConfig(String keyName) {
        return removeConfig(keyFactory.createKey(keyName));
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
    public Map<StatsKey,StatsConfig> getConfigs() {
        return getConfigs(StatsKeyMatcher.all());
    }

    @Override
    public Map<StatsKey,StatsConfig> getConfigs(final StatsKeyMatcher matcher) {
        return matcher.filterToMap(keyMap.values());
    }

    @Override
    public void clearConfigs() {
        destroyEntry(rootKeyEntry);
    }

    /* PRIVATE METHODS */

    private KeyEntry entryFor(final StatsKey key) {
        return keyMap.get(key.getName());
    }

    /*
     * This is kind of a scary method. Needs some refactoring.
     */
    private KeyEntry createEntry(final StatsKey key,
                                 StatsConfig config,
                                 final boolean updateConfig,
                                 final boolean updateLocked) {

        boolean goingUp = true;

        String keyName = key.getName();
        KeyEntry entry = null;

        LinkedList<KeyEntry> newEntries = new LinkedList<KeyEntry>();
        LinkedList<String> stack = new LinkedList<String>();

        if (!updateLocked) {
            updateLock.lock();
        }

        try {
            for (;;) {
                if (goingUp) {
                    entry = keyMap.get(keyName);
                    if (entry == null) {
                        String parentKeyName = StatsKeyUtil.parentKeyName(keyName);
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
                    StatsKey newKey = keyFactory.createKey(keyName);
                    entry = new KeyEntry(newKey, parentEntry, theConfig);
                    parentEntry.childEntries.add(entry);

                    newEntries.add(entry);
                    keyMap.put(keyName, entry);
                }
            }
        } finally {
            updateLock.unlock();
        }

        if (!newEntries.isEmpty()) {
            for (KeyEntry e : newEntries) {
                eventManager.fireEvent(EventType.CONFIG_CREATED,
                                       e.getKey(),
                                       e.getConfig());
            }
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
            while (entryItr.hasNext()) {
                entry = entryItr.next();

                if (!entry.equals(rootKeyEntry)) {
                    eventManager.fireEvent(EventType.CONFIG_CHANGED,
                                           entry.getKey(),
                                           entry.getConfig());
                }
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

        while (entryItr.hasNext()) {
            entry = entryItr.next();

            eventManager.fireEvent(EventType.CONFIG_DESTROYED,
                                   entry.getKey(),
                                   entry.getConfig());
        }
    }

    /**
     * Created by IntelliJ IDEA.
     * User: troy
     * Date: 29-May-2010
     * Time: 11:09:40 AM
     * To change this template use File | Settings | File Templates.
     */
    static final class KeyEntry implements StatsKeyAssociation<StatsConfig>, Serializable {

        private final StatsKey key;

        KeyEntry parentEntry;

        final List<KeyEntry> childEntries = new LinkedList<KeyEntry>();

        private boolean configInherited;
        private final AtomicReference<StatsConfig> config = new AtomicReference<StatsConfig>(null);

        KeyEntry(final StatsKey key,
                 final KeyEntry parentEntry,
                 final StatsConfig config) {

            assertNotNull(key, "key");

            this.key = key;
            this.parentEntry = parentEntry;

            setConfig(config);
        }

        @Override
        public StatsKey getKey() {
            return key;
        }

        @Override
        public StatsConfig getValue() {
            return getConfig();
        }

        void visit(final Visitor visitor) {
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

    /**
     * Created by IntelliJ IDEA.
     * User: troy
     * Date: 29-May-2010
     * Time: 11:09:40 AM
     * To change this template use File | Settings | File Templates.
     */
    static final class KeyEntryDestroyer implements KeyEntry.Visitor {

        private final Map<String, KeyEntry> keyMap;

        private final List<KeyEntry> entryList = new LinkedList<KeyEntry>();

        KeyEntryDestroyer(final Map<String, KeyEntry> keyMap) {
            assertNotNull(keyMap, "keyMap");
            this.keyMap = keyMap;
        }

        @Override
        public boolean visit(final KeyEntry entry) {
            if (entry.isRoot()) {
                // Don't destroy the root entry
                return true;
            }

            keyMap.remove(entry.getKey().getName());

            entryList.add(entry);

            entry.parentEntry.childEntries.remove(entry);
            entry.parentEntry = null;

            return true;
        }

        public Iterator<KeyEntry> iterator() {
            return entryList.iterator();
        }
    }
}

final class KeyEntryConfigUpdater implements DefaultStatsConfigManager.KeyEntry.Visitor {

    private final List<DefaultStatsConfigManager.KeyEntry> entryList = new LinkedList<DefaultStatsConfigManager.KeyEntry>();
    private final StatsConfig config;
    private boolean first = true;

    KeyEntryConfigUpdater(final StatsConfig config) {
        this.config = config;
    }

    @Override
    public boolean visit(final DefaultStatsConfigManager.KeyEntry entry) {

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

    public Iterator<DefaultStatsConfigManager.KeyEntry> iterator() {
        return entryList.iterator();
    }
}
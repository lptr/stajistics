package org.stajistics;

import static org.stajistics.Util.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.bootstrap.StatsManagerFactory;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class Stats {

    private static final Logger logger = LoggerFactory.getLogger(Stats.class);

    protected static final String PROP_MANAGER_FACTORY = StatsManagerFactory.class.getName();
    protected static final String PROP_AUTO_INIT_DEFAULT_MANAGER = StatsFactory.class.getName() + ".autoInitDefaultManager";

    private Stats() {}

    public static StatsFactory getFactory(final Class<?> aClass) {
        assertNotNull(aClass, "aClass");

        final StatsManagerRegistry reg = StatsManagerRegistry.getInstance();
        String pkg = aClass.getPackage().getName();

        do {
            String namespace = pkg;
            if (reg.isStatsManagerDefined(namespace)) {
                StatsManager statsManager = reg.getStatsManager(namespace);
                if (statsManager != null) {
                    logger.debug("Found StatsManager at namespace '{}' for class '{}'.", namespace, aClass);
                    return new DefaultStatsFactory(statsManager);
                }
            }

            int i = pkg.lastIndexOf('.');
            if (i > -1) {
                pkg = pkg.substring(0, i);
            } else {
                pkg = null;
            }
        } while (pkg != null);

        logger.debug("No matching namespaces defined for class '{}'. Attemping to use default namespace.", aClass);

        return getFactory(StatsConstants.DEFAULT_NAMESPACE);
    }

    public static StatsFactory getFactory(final String namespace) {
        assertNotNull(namespace, "namespace");
        final StatsManagerRegistry reg = StatsManagerRegistry.getInstance();

        if (reg.isStatsManagerDefined(namespace)) {
            StatsManager statsManager = reg.getStatsManager(namespace);
            return new DefaultStatsFactory(statsManager);
        }

        // Namespace not found

        if (namespace.equals(StatsConstants.DEFAULT_NAMESPACE)) {
            boolean autoInitDefaultManager = Boolean.parseBoolean(System.getProperty(PROP_AUTO_INIT_DEFAULT_MANAGER, 
                                                                                     Boolean.TRUE.toString()));
            if (autoInitDefaultManager) {
                loadDefaultStatsManager();
                StatsManager statsManager = reg.getStatsManager(namespace);
                return new DefaultStatsFactory(statsManager);
            }
        }

        throw new StatsNamespaceNotFoundException(namespace);
    }

    protected static StatsManager loadDefaultStatsManager() {

        StatsManager manager = null;

        try {
            StatsManagerFactory managerFactory = loadStatsManagerFactoryFromProperties();
            if (managerFactory != null) {
                manager = managerFactory.createManager(StatsConstants.DEFAULT_NAMESPACE);
                if (manager == null) {
                    logger.error(StatsManagerFactory.class.getSimpleName() + " created null " + 
                                 StatsManager.class.getSimpleName() + ": " + managerFactory.getClass());
                }
            }

        } catch (Exception e) {
            logger.error("Failed to load " + StatsManager.class.getSimpleName() + ": " + e.toString(), e);
        }

        if (manager == null) {
            manager = new DefaultStatsManagerFactory().createManager(StatsConstants.DEFAULT_NAMESPACE);
        }

        return manager;
    }

    protected static StatsManagerFactory loadStatsManagerFactoryFromProperties() throws Exception {
        StatsManagerFactory managerFactory = null;

        String managerFactoryClassName = System.getProperty(PROP_MANAGER_FACTORY);
        if (managerFactoryClassName != null) {
            @SuppressWarnings("unchecked")
            Class<StatsManagerFactory> managerFactoryClass =
                    (Class<StatsManagerFactory>)Class.forName(managerFactoryClassName);

            managerFactory = managerFactoryClass.newInstance();
        }

        return managerFactory;
    }

    
}

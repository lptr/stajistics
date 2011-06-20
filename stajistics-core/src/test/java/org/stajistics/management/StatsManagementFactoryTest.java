package org.stajistics.management;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.DefaultStatsKeyFactory;
import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.configuration.DefaultStatsConfigManager;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.event.EventManager;
import org.stajistics.event.SynchronousEventManager;
import org.stajistics.management.beans.*;
import org.stajistics.session.DefaultSessionManager;
import org.stajistics.session.StatsSessionManager;

import javax.management.ObjectName;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagementFactoryTest extends AbstractJMXTestCase {

    private StatsManagementFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = createStatsManagementFactory();
    }

    protected StatsManagementFactory createStatsManagementFactory() throws Exception {
        return new StatsManagementFactory(getMBeanServerConnection());
    }

    @Test
    public void testGetNamespaces() throws Exception {
        ObjectName objectName = StatsMXBeanUtil.getStatsManagerObjectName("test");
        registerStatsManagerMXBean(objectName);

        Set<String> namespaces = factory.getNamespaces();

        assertNotNull(namespaces);
        assertEquals(1, namespaces.size());
        assertEquals("test", namespaces.iterator().next());
    }

    @Test
    public void testGetStatsManagerObjectNames() throws Exception {
        ObjectName objectName = StatsMXBeanUtil.getStatsManagerObjectName("test");
        registerStatsManagerMXBean(objectName);

        Set<ObjectName> objectNames = factory.getStatsManagerObjectNames();

        assertNotNull(objectNames);
        assertEquals(1, objectNames.size());
        assertEquals(objectName, objectNames.iterator().next());
    }

    @Test
    public void testGetSessionManagerObjectNames() throws Exception {
        ObjectName objectName = StatsMXBeanUtil.getSessionManagerObjectName("test");
        registerSessionManagerMXBean(objectName, null);

        Set<ObjectName> objectNames = factory.getSessionManagerObjectNames();

        assertNotNull(objectNames);
        assertEquals(1, objectNames.size());
        assertEquals(objectName, objectNames.iterator().next());
    }

    @Test
    public void testGetConfigManagerObjectNames() throws Exception {
        ObjectName objectName = StatsMXBeanUtil.getConfigManagerObjectName("test");
        registerConfigManagerMXBean(objectName, null);

        Set<ObjectName> objectNames = factory.getConfigManagerObjectNames();

        assertNotNull(objectNames);
        assertEquals(1, objectNames.size());
        assertEquals(objectName, objectNames.iterator().next());
    }

    @Test
    public void testGetStatsManagerMXBean() throws Exception {
        ObjectName objectName = StatsMXBeanUtil.getStatsManagerObjectName("test");
        registerStatsManagerMXBean(objectName);

        StatsManagerMXBean mBean = factory.getStatsManagerMXBean("test");

        assertNotNull(mBean);
        assertEquals("test", mBean.getNamespace());
    }

    @Test
    public void testGetKeys() throws Exception {
        StatsManager statsManager = registerStatsManagerMXBean(StatsMXBeanUtil.getStatsManagerObjectName("test"));
        registerSessionManagerMXBean(StatsMXBeanUtil.getSessionManagerObjectName("test"),
                                     statsManager.getSessionManager());

        final StatsKey key = statsManager.getKeyFactory().createKey("testKey");
        statsManager.getSessionManager().getOrCreateSession(key);

        Set<StatsKey> keys = factory.getKeys("test");

        assertNotNull(keys);
        assertEquals(1, keys.size());
        assertEquals(key, keys.iterator().next());
    }

    private StatsManager registerStatsManagerMXBean(ObjectName objectName) throws Exception {
        String namespace = objectName.getKeyProperty(StatsMXBeanUtil.OBJECT_NAME_ATTR_NAMESPACE);
        StatsManager statsManager = new DefaultStatsManager.Builder().withNamespace(namespace).newManager();
        StatsManagerMXBean mBean = new DefaultStatsManagerMXBean(statsManager);
        mBean = registerMBean(mBean, objectName, StatsManagerMXBean.class);
        return statsManager;
    }

    private StatsSessionManager registerSessionManagerMXBean(ObjectName objectName, StatsSessionManager sessionManager) throws Exception {

        if (sessionManager == null) {
            EventManager eventManager = new SynchronousEventManager();
            StatsConfigManager configManager = new DefaultStatsConfigManager(eventManager, new DefaultStatsKeyFactory());
            sessionManager = new DefaultSessionManager(configManager, eventManager);
        }

        StatsSessionManagerMXBean mBean = new DefaultStatsSessionManagerMXBean(sessionManager);
        mBean = registerMBean(mBean, objectName, StatsSessionManagerMXBean.class);

        return sessionManager;
    }

    private StatsConfigManager registerConfigManagerMXBean(ObjectName objectName, StatsConfigManager configManager) throws Exception {

        if (configManager == null) {
            EventManager eventManager = new SynchronousEventManager();
            configManager = new DefaultStatsConfigManager(eventManager, new DefaultStatsKeyFactory());
        }

        StatsConfigManagerMXBean mBean = new DefaultStatsConfigManagerMXBean(configManager);
        mBean = registerMBean(mBean, objectName, StatsConfigManagerMXBean.class);

        return configManager;
    }
}

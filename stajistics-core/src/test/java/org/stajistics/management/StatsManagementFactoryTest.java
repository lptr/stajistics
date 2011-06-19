package org.stajistics.management;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.management.beans.DefaultStatsManagerMXBean;
import org.stajistics.management.beans.DefaultStatsSessionManagerMXBean;
import org.stajistics.management.beans.StatsManagerMXBean;
import org.stajistics.management.beans.StatsSessionManagerMXBean;
import org.stajistics.session.StatsSessionManager;

import javax.management.ObjectName;
import java.util.Set;

import static org.junit.Assert.*;

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
        assertFalse(namespaces.isEmpty());
        assertEquals(1, namespaces.size());
        assertEquals("test", namespaces.iterator().next());
    }

    @Test
    public void testGetStatsManagerObjectNames() throws Exception {
        ObjectName objectName = StatsMXBeanUtil.getStatsManagerObjectName("test");
        registerStatsManagerMXBean(objectName);

        Set<ObjectName> objectNames = factory.getStatsManagerObjectNames();

        assertNotNull(objectNames);
        assertFalse(objectNames.isEmpty());
        assertEquals(1, objectNames.size());
        assertEquals(objectName, objectNames.iterator().next());
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
        assertFalse(keys.isEmpty());
        assertEquals(1, keys.size());
        assertEquals(key, keys.iterator().next());
    }

    private StatsManager registerStatsManagerMXBean(ObjectName objectName) throws Exception {
        StatsManager statsManager = new DefaultStatsManager.Builder().newManager();
        StatsManagerMXBean mBean = new DefaultStatsManagerMXBean(statsManager);
        mBean = registerMBean(mBean, objectName, StatsManagerMXBean.class);
        return statsManager;
    }

    private void registerSessionManagerMXBean(ObjectName objectName, StatsSessionManager sessionManager) throws Exception {
        StatsSessionManagerMXBean mBean = new DefaultStatsSessionManagerMXBean(sessionManager);
        mBean = registerMBean(mBean, objectName, StatsSessionManagerMXBean.class);
    }
}

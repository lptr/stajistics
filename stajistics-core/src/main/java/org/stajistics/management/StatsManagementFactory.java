package org.stajistics.management;

import static org.stajistics.Util.assertNotNull;
import static org.stajistics.management.StatsMXBeanUtil.OBJECT_NAME_ATTR_NAMESPACE;
import static org.stajistics.management.StatsMXBeanUtil.getConfigManagerObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getConfigManagerObjectNameString;
import static org.stajistics.management.StatsMXBeanUtil.getSessionManagerObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getSessionManagerObjectNameString;
import static org.stajistics.management.StatsMXBeanUtil.getStatsManagerObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getStatsManagerObjectNameString;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.stajistics.StatsKey;
import org.stajistics.management.beans.StatsConfigManagerMXBean;
import org.stajistics.management.beans.StatsManagerMXBean;
import org.stajistics.management.beans.StatsSessionManagerMXBean;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagementFactory {

    private final MBeanServerConnection mBeanServerConnection;

    /**
     * 
     * @param mBeanServerConnection
     */
    public StatsManagementFactory(final MBeanServerConnection mBeanServerConnection) {
        assertNotNull(mBeanServerConnection, "mBeanServerConnection");
        this.mBeanServerConnection = mBeanServerConnection;
    }

    /**
     * 
     * @param serviceURL
     * @return
     * @throws IOException
     */
    public static StatsManagementFactory forURL(final String serviceURL) throws IOException {
        JMXServiceURL u = new JMXServiceURL(serviceURL);
        JMXConnector connector = JMXConnectorFactory.connect(u, null);
        MBeanServerConnection connection = connector.getMBeanServerConnection();

        return new StatsManagementFactory(connection);
    }

    public MBeanServerConnection getMBeanServerConnection() {
        return mBeanServerConnection;
    }

    /**
     * 
     * @return
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public Set<String> getNamespaces() throws IOException, MalformedObjectNameException {
        Set<ObjectName> objectNames = getStatsManagerObjectNames();
        Set<String> namespaces = new HashSet<String>(objectNames.size());

        for (ObjectName objectName : objectNames) {
            String namespace = objectName.getKeyProperty(OBJECT_NAME_ATTR_NAMESPACE);
            namespaces.add(namespace);
        }

        return Collections.unmodifiableSet(namespaces);
    }

    /**
     * 
     * @return
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public Set<ObjectName> getStatsManagerObjectNames() throws IOException, MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getStatsManagerObjectNameString("*", false));
        return getObjectNames(objectName);
    }

    /**
     * 
     * @return
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public Set<ObjectName> getSessionManagerObjectNames() throws IOException, MalformedObjectNameException {
        ObjectName objectName = new ObjectName(getSessionManagerObjectNameString("*", false));
        return getObjectNames(objectName);
    }

    /**
     * 
     * @return
     * @throws IOException 
     * @throws MalformedObjectNameException 
     */
    public Set<ObjectName> getConfigManagerObjectNames() throws MalformedObjectNameException, IOException {
        ObjectName objectName = new ObjectName(getConfigManagerObjectNameString("*", false));
        return getObjectNames(objectName);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public StatsManagerMXBean getStatsManagerMXBean(final String namespace) throws MalformedObjectNameException {
        return newProxy(getStatsManagerObjectName(namespace), StatsManagerMXBean.class);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public StatsSessionManagerMXBean getSessionManagerMXBean(final String namespace) throws MalformedObjectNameException {
        return newProxy(getSessionManagerObjectName(namespace), StatsSessionManagerMXBean.class);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public StatsConfigManagerMXBean getConfigManagerMXBean(final String namespace) throws MalformedObjectNameException {
        return newProxy(getConfigManagerObjectName(namespace), StatsConfigManagerMXBean.class);
    }


    /**
     * 
     * @return
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public Set<StatsManagerMXBean> getStatsManagerMXBeans() throws IOException, MalformedObjectNameException {
        return newProxies(getStatsManagerObjectNames(), StatsManagerMXBean.class);
    }

    /**
     * 
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException
     */
    public Set<StatsSessionManagerMXBean> getSessionManagerMXBeans() throws MalformedObjectNameException, IOException {
        return newProxies(getSessionManagerObjectNames(), StatsSessionManagerMXBean.class);
    }

    /**
     * 
     * @param namespace
     * @return
     * @throws MalformedObjectNameException
     */
    public Set<StatsKey> getKeys(final String namespace) throws MalformedObjectNameException {
        StatsSessionManagerMXBean sessionMXBean = getSessionManagerMXBean(namespace);
        Set<String> openKeys = sessionMXBean.statsKeys();
        StatsKeyOpenTypeConverter converter = new StatsKeyOpenTypeConverter();
        return converter.fromOpenType(openKeys);
    }


    /**
     * 
     * @param objectName
     * @return
     * @throws IOException
     */
    protected Set<ObjectName> getObjectNames(final ObjectName objectName) throws IOException {
        Set<ObjectName> objectNames = mBeanServerConnection.queryNames(objectName, null);
        return Collections.unmodifiableSet(objectNames);
    }

    /**
     * 
     * @param <T>
     * @param objectName
     * @param interfaceClass
     * @return
     */
    protected <T> T newProxy(final ObjectName objectName, final Class<T> interfaceClass) {
        T proxy = JMX.newMXBeanProxy(mBeanServerConnection, objectName, interfaceClass);
        return proxy;
    }

    /**
     * 
     * @param <T>
     * @param objectNames
     * @param interfaceClass
     * @return
     */
    protected <T> Set<T> newProxies(final Set<ObjectName> objectNames, final Class<T> interfaceClass) {
        Set<T> proxies = new HashSet<T>(objectNames.size());

        for (ObjectName objectName : objectNames) {
            proxies.add(newProxy(objectName, interfaceClass));
        }

        return Collections.unmodifiableSet(proxies);
    }
}

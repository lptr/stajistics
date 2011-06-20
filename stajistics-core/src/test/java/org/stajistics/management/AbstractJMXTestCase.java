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
package org.stajistics.management;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.After;
import org.junit.Before;
import org.stajistics.AbstractStajisticsTestCase;

/**
 * Thanks to Eamonn McManus for the MBean testing strategy:
 * http://weblogs.java.net/blog/emcmanus/archive/2006/07/unit_testing_re.html
 *
 * @author The Stajistics Project
 */
public abstract class AbstractJMXTestCase extends AbstractStajisticsTestCase {

    private static final Registry rmiRegistry;
    static {
        try {
            rmiRegistry = LocateRegistry.createRegistry(getPort());
        } catch (RemoteException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private MBeanServer mBeanServer = null;
    private JMXConnector connector = null;
    private JMXConnectorServer connectorServer = null;
    private MBeanServerConnection mBeanServerConnection = null;

    @Before
    public void setUpMBeanServerConnection() throws Exception {

        mBeanServer = MBeanServerFactory.newMBeanServer();

        JMXServiceURL url = getJMXServiceURL();
        connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer);
        connectorServer.start();

        JMXServiceURL addr = connectorServer.getAddress();
        connector = JMXConnectorFactory.connect(addr);
        mBeanServerConnection = connector.getMBeanServerConnection();
    }

    @After
    public void tearDownMBeanServerConnection() throws Exception {

        if (connector != null) {
            try {
                connector.close();
            } catch (IOException ioe) {}
        }

        if (connectorServer != null) {
            try {
                connectorServer.stop();
            } catch (IOException ioe) {}
        }

        mBeanServer = null;
        connector = null;
        connectorServer = null;
        mBeanServerConnection = null;
    }

    protected MBeanServer getMBeanServer() {
        if (mBeanServer == null) {
            throw new RuntimeException("MBeanServer not initialized");
        }

        return mBeanServer;
    }

    protected MBeanServerConnection getMBeanServerConnection() {
        if (mBeanServerConnection == null) {
            throw new RuntimeException("MBeanServerConnection not initialized");
        }

        return mBeanServerConnection;
    }

    protected <T> T registerMBean(final T mBean,
                                  final ObjectName name,
                                  final Class<T> mBeanInterfaceClass) throws Exception {

        // Register with the local server
        mBeanServer.registerMBean(mBean, name);

        // Create a proxy to the remote MBean
        T remoteMBean = JMX.newMBeanProxy(mBeanServerConnection,
                                          name,
                                          mBeanInterfaceClass);

        return remoteMBean;
    }

    protected static int getPort() {
        return Integer.getInteger(AbstractJMXTestCase.class.getName() + ".rmiPort", 2099);
    }

    protected String getServiceURL() {
        return "service:jmx:rmi:///jndi/rmi://127.0.0.1:" + getPort() + "/unitTestServer";
    }

    protected JMXServiceURL getJMXServiceURL() throws MalformedURLException {
        JMXServiceURL url = new JMXServiceURL(getServiceURL());
        return url;
    }
}

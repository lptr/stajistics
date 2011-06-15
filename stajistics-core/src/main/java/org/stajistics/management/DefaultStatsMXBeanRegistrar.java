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

import static org.stajistics.management.StatsMXBeanUtil.MANAGER_NAME_CONFIG;
import static org.stajistics.management.StatsMXBeanUtil.MANAGER_NAME_SESSION;
import static org.stajistics.management.StatsMXBeanUtil.MANAGER_NAME_STATS;
import static org.stajistics.management.StatsMXBeanUtil.SUBTYPE_CONFIG;
import static org.stajistics.management.StatsMXBeanUtil.SUBTYPE_SESSION;
import static org.stajistics.management.StatsMXBeanUtil.TYPE_KEYS;
import static org.stajistics.management.StatsMXBeanUtil.buildManagerName;
import static org.stajistics.management.StatsMXBeanUtil.buildName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.management.beans.StatsConfigMXBean;
import org.stajistics.management.beans.StatsConfigManagerMXBean;
import org.stajistics.management.beans.StatsManagerMXBean;
import org.stajistics.management.beans.StatsSessionMXBean;
import org.stajistics.management.beans.StatsSessionManagerMXBean;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsMXBeanRegistrar implements StatsMXBeanRegistrar,Serializable {

    private static Logger logger = LoggerFactory.getLogger(DefaultStatsMXBeanRegistrar.class);

    private final StatsMXBeanFactory mBeanFactory;

    private final String namespace;
    private boolean isPlatformMBeanServer;
    private transient MBeanServer mBeanServer;

    public DefaultStatsMXBeanRegistrar(final String namespace) {
        this(namespace, new DefaultStatsMXBeanFactory());
    }

    public DefaultStatsMXBeanRegistrar(final String namespace,
                                       final StatsMXBeanFactory mBeanFactory) {
        this(namespace, mBeanFactory, ManagementFactory.getPlatformMBeanServer());
    }

    public DefaultStatsMXBeanRegistrar(final String namespace,
                                       final StatsMXBeanFactory mBeanFactory,
                                       final MBeanServer mBeanServer) {
        if (namespace == null) {
            throw new NullPointerException("namespace");
        }
        if (namespace.isEmpty()) {
            throw new IllegalArgumentException("empty namespace");
        }
        if (mBeanFactory == null) {
            throw new NullPointerException("mBeanFactory");
        }
        if (mBeanServer == null) {
            throw new NullPointerException("mBeanServer");
        }

        this.namespace = namespace;
        this.mBeanFactory = mBeanFactory;
        this.mBeanServer = mBeanServer;

        this.isPlatformMBeanServer = mBeanServer.equals(ManagementFactory.getPlatformMBeanServer());
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    

    @Override
    public void registerManagerMXBean(final StatsManager statsManager) {

        String name = buildManagerName(statsManager.getNamespace(), MANAGER_NAME_STATS);

        try {
            StatsManagerMXBean statsManagerMBean =
                mBeanFactory.createManagerMBean(statsManager);

            ObjectName objectName = new ObjectName(name);

            registerMBean(statsManagerMBean, objectName);

            logRegistrationSuccess(true, StatsManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsManagerMXBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterManagerMXBean(StatsManager statsManager) {

        String name = buildManagerName(statsManager.getNamespace(), MANAGER_NAME_STATS);

        try {
            ObjectName objectName = new ObjectName(name) ;
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsManagerMXBean.class, null, name, e);

            throw new StatsManagementException(e);
        }

    }

    @Override
    public void registerSessionManagerMXBean(final StatsSessionManager sessionManager) {

        String name = buildManagerName(namespace, MANAGER_NAME_SESSION);

        try {
            StatsSessionManagerMXBean statsSessionManagerMBean =
                mBeanFactory.createSessionManagerMBean(sessionManager);

            ObjectName objectName = new ObjectName(name);

            registerMBean(statsSessionManagerMBean, objectName);

            logRegistrationSuccess(true, StatsSessionManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionManagerMXBean.class, null, name, e);

            throw new StatsManagementException(e);
        }

    }

    @Override
    public void unregisterSessionManagerMXBean() {

        String name = buildManagerName(namespace, MANAGER_NAME_SESSION);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsSessionManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionManagerMXBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerConfigManagerMXBean(final StatsConfigManager configManager) {
        String name = buildManagerName(namespace, MANAGER_NAME_CONFIG);
        try {
            StatsConfigManagerMXBean statsConfigManagerMBean =
                mBeanFactory.createConfigManagerMBean(configManager);

            ObjectName objectName = new ObjectName(name);

            registerMBean(statsConfigManagerMBean, objectName);

            logRegistrationSuccess(true, StatsConfigManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigManagerMXBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterConfigManagerMXBean() {
        String name = buildManagerName(namespace, MANAGER_NAME_CONFIG);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsConfigManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigManagerMXBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerConfigMXBean(final StatsKey key,
                                    final StatsConfig config) {

        String name = buildName(namespace, key, TYPE_KEYS, SUBTYPE_CONFIG, false);

        try {
            StatsConfigMXBean configMBean = mBeanFactory.createConfigMBean(namespace, key, config);
            ObjectName objectName = new ObjectName(name);

            registerMBean(configMBean, objectName);

            logRegistrationSuccess(true, StatsConfigMXBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigMXBean.class, key, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterConfigMXBeanIfNecessary(final StatsKey key) {

        String name = buildName(namespace, key, TYPE_KEYS, SUBTYPE_CONFIG, false);

        try {
            ObjectName objectName = new ObjectName(name);
            if (mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);

                logRegistrationSuccess(false, StatsConfigMXBean.class, key, objectName);
            }

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigMXBean.class, key, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerSessionMXBean(final StatsSession session) {

        String name = buildName(namespace, session.getKey(), TYPE_KEYS, SUBTYPE_SESSION, true);

        try {
            StatsSessionMXBean sessionMBean = mBeanFactory.createSessionMBean(namespace,
                                                                              session);

            ObjectName objectName = new ObjectName(name);
            registerMBean(sessionMBean, objectName);

            logRegistrationSuccess(true, StatsSessionMXBean.class, session.getKey(), objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionMXBean.class, session.getKey(), name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterSessionMXBeanIfNecessary(final StatsKey key) {

        String name = buildName(namespace, key, TYPE_KEYS, SUBTYPE_SESSION, true);

        try {
            ObjectName objectName = new ObjectName(name);
            if (mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);

                logRegistrationSuccess(false, StatsSessionMXBean.class, key, objectName);
            }

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionMXBean.class, key, name, e);

            throw new StatsManagementException(e);
        }
    }

    private void registerMBean(final Object mBean,
                               final ObjectName name) throws Exception {
        if (mBeanServer.isRegistered(name)) {
            logger.warn("Replacing existing MBean: {}", name);

            mBeanServer.unregisterMBean(name);
        }

        mBeanServer.registerMBean(mBean, name);
    }

    private void logRegistrationSuccess(final boolean register,
                                        final Class<?> mBeanType,
                                        final StatsKey key,
                                        final ObjectName objectName) {
        if (logger.isDebugEnabled()) {
            StringBuilder buf = new StringBuilder(256);
            if (register) {
                buf.append("Registered ");
            } else {
                buf.append("Unregistered ");
            }

            buf.append(mBeanType.getSimpleName());

            if (key != null) {
                buf.append(" for ");
                buf.append(key.toString());
            }

            buf.append(", ObjectName: ");
            buf.append(objectName);

            logger.debug(buf.toString());
        }
    }

    private void logRegistrationFailure(final boolean register,
                                        final Class<?> mBeanType,
                                        final StatsKey key,
                                        final String objectName,
                                        final Exception e) {
        if (logger.isErrorEnabled()) {
            StringBuilder buf = new StringBuilder(256);
            buf.append("Failed to ");

            if (!register) {
                buf.append("un");
            }

            buf.append("register ");
            buf.append(mBeanType.getSimpleName());

            if (key != null) {
                buf.append(" for ");
                buf.append(key.toString());
            }

            buf.append(", ObjectName: ");
            buf.append(objectName);

            logger.error(buf.toString(), e);
        }
    }

    /* Restore transient fields */
    private void readObject(final ObjectInputStream in)
            throws IOException,ClassNotFoundException {
        in.defaultReadObject();

        if (!isPlatformMBeanServer) {
            isPlatformMBeanServer = true;

            logger.warn("Restoring transient MBeanServer after deserialization to the "
                      + "platform MBeanServer when the orginal was non-platform");
        }

        mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }
}

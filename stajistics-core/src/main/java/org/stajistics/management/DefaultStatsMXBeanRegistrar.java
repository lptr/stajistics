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

import static org.stajistics.management.StatsMXBeanUtil.getConfigManagerObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getConfigManagerObjectNameString;
import static org.stajistics.management.StatsMXBeanUtil.getConfigObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getConfigObjectNameString;
import static org.stajistics.management.StatsMXBeanUtil.getSessionManagerObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getSessionManagerObjectNameString;
import static org.stajistics.management.StatsMXBeanUtil.getSessionObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getSessionObjectNameString;
import static org.stajistics.management.StatsMXBeanUtil.getStatsManagerObjectName;
import static org.stajistics.management.StatsMXBeanUtil.getStatsManagerObjectNameString;

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
    public void registerStatsManagerMXBean(final StatsManager statsManager) {
        assert statsManager.getNamespace().equals(namespace);

        try {
            StatsManagerMXBean statsManagerMBean =
                mBeanFactory.createManagerMBean(statsManager);

            ObjectName objectName = getStatsManagerObjectName(namespace);
            registerMBean(statsManagerMBean, objectName);

            logRegistrationSuccess(true, StatsManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsManagerMXBean.class, null, getStatsManagerObjectNameString(namespace, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterStatsManagerMXBean(final StatsManager statsManager) {
        assert statsManager.getNamespace().equals(namespace);

        try {
            ObjectName objectName = getStatsManagerObjectName(namespace);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsManagerMXBean.class, null, getStatsManagerObjectNameString(namespace, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerSessionManagerMXBean(final StatsSessionManager sessionManager) {
        try {
            StatsSessionManagerMXBean statsSessionManagerMBean =
                mBeanFactory.createSessionManagerMBean(sessionManager);

            ObjectName objectName = getSessionManagerObjectName(namespace);
            registerMBean(statsSessionManagerMBean, objectName);

            logRegistrationSuccess(true, StatsSessionManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionManagerMXBean.class, null, getSessionManagerObjectNameString(namespace, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterSessionManagerMXBean() {
        try {
            ObjectName objectName = getSessionManagerObjectName(namespace);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsSessionManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionManagerMXBean.class, null, getSessionManagerObjectNameString(namespace, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerConfigManagerMXBean(final StatsConfigManager configManager) {
        try {
            StatsConfigManagerMXBean statsConfigManagerMBean =
                mBeanFactory.createConfigManagerMBean(configManager);

            ObjectName objectName = getConfigManagerObjectName(namespace);
            registerMBean(statsConfigManagerMBean, objectName);

            logRegistrationSuccess(true, StatsConfigManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigManagerMXBean.class, null, getConfigManagerObjectNameString(namespace, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterConfigManagerMXBean() {
        try {
            ObjectName objectName = getConfigManagerObjectName(namespace);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsConfigManagerMXBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigManagerMXBean.class, null, getConfigManagerObjectNameString(namespace, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerConfigMXBean(final StatsKey key,
                                     final StatsConfig config) {
        try {
            StatsConfigMXBean configMBean = mBeanFactory.createConfigMBean(namespace, key, config);

            ObjectName objectName = getConfigObjectName(namespace, key);
            registerMBean(configMBean, objectName);

            logRegistrationSuccess(true, StatsConfigMXBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigMXBean.class, key, getConfigObjectNameString(namespace, key, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterConfigMXBeanIfNecessary(final StatsKey key) {
        try {
            ObjectName objectName = getConfigObjectName(namespace, key);
            if (mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);

                logRegistrationSuccess(false, StatsConfigMXBean.class, key, objectName);
            }

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigMXBean.class, key, getConfigObjectNameString(namespace, key, true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerSessionMXBean(final StatsSession session) {
        try {
            StatsSessionMXBean sessionMBean = mBeanFactory.createSessionMBean(namespace,
                                                                              session);

            ObjectName objectName = getSessionObjectName(namespace, session.getKey());
            registerMBean(sessionMBean, objectName);

            logRegistrationSuccess(true, StatsSessionMXBean.class, session.getKey(), objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionMXBean.class, session.getKey(), getSessionObjectNameString(namespace, session.getKey(), true), e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterSessionMXBeanIfNecessary(final StatsKey key) {
        try {
            ObjectName objectName = getSessionObjectName(namespace, key);
            if (mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);

                logRegistrationSuccess(false, StatsSessionMXBean.class, key, objectName);
            }

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionMXBean.class, key, getSessionObjectNameString(namespace, key, true), e);

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

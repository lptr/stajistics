/* Copyright 2009 The Stajistics Project
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
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsConfig;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.session.StatsSession;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsManagement implements StatsManagement,Serializable {

    private static Logger logger = LoggerFactory.getLogger(DefaultStatsManagement.class);

    static final String DOMAIN = org.stajistics.Stats.class.getPackage().getName();

    static final String TYPE_MANAGER = "manager";
    static final String TYPE_KEYS = "keys";

    static final String SUBTYPE_SESSION = "session";
    static final String SUBTYPE_CONFIG = "config";

    static final String MANAGER_NAME_CONFIG = "ConfigManager";
    static final String MANAGER_NAME_SESSION = "SessionManager";
    static final String MANAGER_NAME_SNAPSHOT = "SnapshotManager";

    private static final Pattern VALUE_ESCAPE_BACKSLASH_PATTERN = Pattern.compile("[\\\\]");
    private static final String VALUE_ESCAPE_BACKSLASH_REPLACEMENT = "\\\\\\\\";
    private static final Pattern VALUE_ESCAPE_ASTERISK_PATTERN = Pattern.compile("[*]");
    private static final String VALUE_ESCAPE_ASTERISK_REPLACEMENT = "\\\\*";
    private static final Pattern VALUE_ESCAPE_QUESTION_MARK_PATTERN = Pattern.compile("[?]");
    private static final String VALUE_ESCAPE_QUESTION_MARK_REPLACEMENT = "\\\\?";
    private static final Pattern VALUE_ESCAPE_DOUBLE_QUOTE_PATTERN = Pattern.compile("[\"]");
    private static final String VALUE_ESCAPE_DOUBLE_QUOTE_REPLACEMENT = "\\\\\"";

    private final StatsMBeanFactory mBeanFactory;

    private boolean isPlatformMBeanServer;
    private transient MBeanServer mBeanServer;

    public DefaultStatsManagement() {
        this(new DefaultStatsMBeanFactory());
    }

    public DefaultStatsManagement(final StatsMBeanFactory mBeanFactory) {
        this(mBeanFactory, ManagementFactory.getPlatformMBeanServer());
    }

    public DefaultStatsManagement(final StatsMBeanFactory mBeanFactory,
                                  final MBeanServer mBeanServer) {
        if (mBeanFactory == null) {
            throw new NullPointerException("mBeanFactory");
        }
        if (mBeanServer == null) {
            throw new NullPointerException("mBeanServer");
        }

        this.mBeanFactory = mBeanFactory;
        this.mBeanServer = mBeanServer;

        this.isPlatformMBeanServer = mBeanServer.equals(ManagementFactory.getPlatformMBeanServer());
    }

    @Override
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    private String getNamespace(final StatsManager statsManager) {
        return Integer.toHexString(System.identityHashCode(statsManager));
    }

    String buildManagerName(final StatsManager statsManager,
                            final String managerName) {
        StringBuilder buf = new StringBuilder(128);
        buf.append(DOMAIN);
        buf.append(":type=");
        buf.append(TYPE_MANAGER);
        buf.append('[');
        buf.append(getNamespace(statsManager));
        buf.append(']');
        buf.append(",name=");
        buf.append(managerName);

        return buf.toString();
    }

    @Override
    public void registerSessionManagerMBean(final StatsManager statsManager) {

        String name = buildManagerName(statsManager, MANAGER_NAME_SESSION);

        try {
            StatsSessionManagerMBean statsSessionManagerMBean = 
                mBeanFactory.createSessionManagerMBean(statsManager);

            ObjectName objectName = new ObjectName(name);

            registerMBean(statsSessionManagerMBean, objectName);

            logRegistrationSuccess(true, StatsSessionManagerMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionManagerMBean.class, null, name, e);

            throw new StatsManagementException(e);
        }

    }

    @Override
    public void unregisterSessionManagerMBean(final StatsManager statsManager) {

        String name = buildManagerName(statsManager, MANAGER_NAME_SESSION);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsSessionManagerMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionManagerMBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerConfigManagerMBean(final StatsManager statsManager) {
        String name = buildManagerName(statsManager, MANAGER_NAME_CONFIG);
        try {
            StatsConfigManagerMBean statsConfigManagerMBean = 
                mBeanFactory.createConfigManagerMBean(statsManager);

            ObjectName objectName = new ObjectName(name);

            registerMBean(statsConfigManagerMBean, objectName);

            logRegistrationSuccess(true, StatsConfigManagerMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigManagerMBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterConfigManagerMBean(final StatsManager statsManager) {
        String name = buildManagerName(statsManager, MANAGER_NAME_CONFIG);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsConfigManagerMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigManagerMBean.class, null, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerConfigMBean(final StatsManager statsManager,
                                    final StatsKey key,
                                    final StatsConfig config) {

        String name = buildName(statsManager, key, TYPE_KEYS, SUBTYPE_CONFIG, false);

        try {
            StatsConfigMBean configMBean = mBeanFactory.createConfigMBean(statsManager,
                                                                          key, 
                                                                          config);
            ObjectName objectName = new ObjectName(name);

            registerMBean(configMBean, objectName);

            logRegistrationSuccess(true, StatsConfigMBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigMBean.class, key, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterConfigMBean(final StatsManager statsManager,
                                      final StatsKey key) {

        String name = buildName(statsManager, key, TYPE_KEYS, SUBTYPE_CONFIG, false);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsConfigMBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigMBean.class, key, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerSessionMBean(final StatsManager statsManager,
                                     final StatsSession session) {

        String name = buildName(statsManager, session.getKey(), TYPE_KEYS, SUBTYPE_SESSION, true);

        try {
            StatsSessionMBean sessionMBean = mBeanFactory.createSessionMBean(statsManager,
                                                                             session);

            ObjectName objectName = new ObjectName(name);
            registerMBean(sessionMBean, objectName);

            logRegistrationSuccess(true, StatsSessionMBean.class, session.getKey(), objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionMBean.class, session.getKey(), name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void unregisterSessionMBean(final StatsManager statsManager,
                                       final StatsKey key) {

        String name = buildName(statsManager, key, TYPE_KEYS, SUBTYPE_SESSION, true);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsSessionMBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionMBean.class, key, name, e);

            throw new StatsManagementException(e);
        }
    }

    @Override
    public void registerSnapshotMBean(final StatsManager statsManager) {

        String name = buildManagerName(statsManager, MANAGER_NAME_SNAPSHOT);

        try {
            StatsSnapshotMBean snapshotMBean = mBeanFactory.createSnapshotMBean(statsManager);

            ObjectName objectName = new ObjectName(name);
            mBeanServer.registerMBean(snapshotMBean, objectName);

            logRegistrationSuccess(true, StatsSnapshotMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSnapshotMBean.class, null, name, e);
        }
    }

    @Override
    public void unregisterSnapshotMBean(final StatsManager statsManager) {

        String name = buildManagerName(statsManager, MANAGER_NAME_SNAPSHOT);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsSnapshotMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSnapshotMBean.class, null, name, e);
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

    protected String buildName(final StatsManager statsManager, 
                               final StatsKey key,
                               final String type,
                               final String subtype,
                               final boolean includeAttributes) {

        StringBuilder buf = new StringBuilder(128);

        buf.append(DOMAIN);
        buf.append(":type=");
        buf.append(type);

        buf.append('[');
        buf.append(getNamespace(statsManager));
        buf.append(']');

        buf.append(",name=");
        appendValue(buf, key.getName());

        if (includeAttributes) {
            for (Map.Entry<String,Object> entry : key.getAttributes().entrySet()) {
                buf.append(',');
                buf.append(entry.getKey());
                buf.append('=');

                appendValue(buf, entry.getValue().toString());
            }
        }

        buf.append(",subtype=");
        buf.append(subtype);

        return buf.toString();
    }

    private void appendValue(final StringBuilder buf,
                             final String value) {

        final boolean valueNeedsQuotes = valueNeedsQuotes(value);
        if (valueNeedsQuotes) {
            buf.append('"');
        }

        buf.append(escapeValue(value));

        if (valueNeedsQuotes) {
            buf.append('"');
        }
    }

    private boolean valueNeedsQuotes(final String name) {
        if (name.indexOf(',') > -1) {
            return true;
        }

        if (name.indexOf('=') > -1) {
            return true;
        }

        if (name.indexOf(':') > -1) {
            return true;
        }

        if (name.indexOf('"') > -1) {
            return true;
        }

        return false;
    }

    private String escapeValue(String value) {
        value = VALUE_ESCAPE_BACKSLASH_PATTERN.matcher(value)
                                              .replaceAll(VALUE_ESCAPE_BACKSLASH_REPLACEMENT);

        value = VALUE_ESCAPE_ASTERISK_PATTERN.matcher(value)
                                             .replaceAll(VALUE_ESCAPE_ASTERISK_REPLACEMENT);

        value = VALUE_ESCAPE_QUESTION_MARK_PATTERN.matcher(value)
                                                  .replaceAll(VALUE_ESCAPE_QUESTION_MARK_REPLACEMENT);

        value = VALUE_ESCAPE_DOUBLE_QUOTE_PATTERN.matcher(value)
                                                 .replaceAll(VALUE_ESCAPE_DOUBLE_QUOTE_REPLACEMENT);

        return value;
    }

    private void logRegistrationSuccess(final boolean register,
                                        final Class<?> mBeanType,
                                        final StatsKey key,
                                        final ObjectName objectName) {
        if (logger.isInfoEnabled()) {
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

            logger.info(buf.toString());
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

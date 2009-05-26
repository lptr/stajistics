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
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.event.StatsEventHandler;
import org.stajistics.event.StatsEventType;
import org.stajistics.session.StatsSessionManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagement implements Serializable {

    private static final long serialVersionUID = -9078257122643636861L;

    private static Logger logger = LoggerFactory.getLogger(StatsManagement.class);

    private static final String SYS_PROP_MANAGEMENT_ENABLED = StatsManagement.class.getName() + ".enabled";

    static final String DOMAIN = org.stajistics.Stats.class.getPackage().getName();

    static final String TYPE_MANAGER = "manager";
    static final String TYPE_KEYS = "keys";

    static final String SUBTYPE_SESSION = "session";
    static final String SUBTYPE_CONFIG = "config";

    private static final Pattern VALUE_ESCAPE_BACKSLASH_PATTERN = Pattern.compile("[\\\\]");
    private static final String VALUE_ESCAPE_BACKSLASH_REPLACEMENT = "\\\\\\\\";
    private static final Pattern VALUE_ESCAPE_ASTERISK_PATTERN = Pattern.compile("[*]");
    private static final String VALUE_ESCAPE_ASTERISK_REPLACEMENT = "\\\\*";
    private static final Pattern VALUE_ESCAPE_QUESTION_MARK_PATTERN = Pattern.compile("[?]");
    private static final String VALUE_ESCAPE_QUESTION_MARK_REPLACEMENT = "\\\\?";
    private static final Pattern VALUE_ESCAPE_DOUBLE_QUOTE_PATTERN = Pattern.compile("[\"]");
    private static final String VALUE_ESCAPE_DOUBLE_QUOTE_REPLACEMENT = "\\\\\"";

    private transient MBeanServer mBeanServer;


    public StatsManagement() {
        this(ManagementFactory.getPlatformMBeanServer());
    }

    public StatsManagement(final MBeanServer mBeanServer) {
        setMBeanServer(mBeanServer);
    }

    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    public void setMBeanServer(final MBeanServer mBeanServer) {
        if (mBeanServer == null) {
            throw new NullPointerException("mBeanServer");
        }
        this.mBeanServer = mBeanServer;
    }

    public void initializeManagement(final StatsManager statsManager) {

        if (!Boolean.parseBoolean(System.getProperty(SYS_PROP_MANAGEMENT_ENABLED, "true"))) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("Initializing statistics management");
        }

        registerSessionManagerMBean(statsManager.getSessionManager());

        statsManager.getEventManager().addGlobalEventHandler(new StatsEventHandler() {
            private static final long serialVersionUID = 7947550194934109138L;

            @Override
            public void handleStatsEvent(final StatsEventType eventType,
                                         final StatsKey key,
                                         final Object target) {
                if (eventType == StatsEventType.SESSION_CREATED) {
                    registerSessionMBean((org.stajistics.session.StatsSession)target);

                } else if (eventType == StatsEventType.SESSION_DESTROYED) {
                    unregisterSessionMBean(key);

                } else if (eventType == StatsEventType.CONFIG_CREATED) {
                    registerConfigMBean(key, (org.stajistics.StatsConfig)target);

                } else if (eventType == StatsEventType.CONFIG_DESTROYED) {
                    unregisterConfigMBean(key);

                } else if (eventType == StatsEventType.CONFIG_CHANGED) {
                    unregisterConfigMBean(key);
                    registerConfigMBean(key, (org.stajistics.StatsConfig)target);
                }
            }
        });
    }

    String buildSessionManagerName() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(DOMAIN);
        buf.append(":type=");
        buf.append(TYPE_MANAGER);
        buf.append(",name=");
        buf.append(SessionManager.class.getSimpleName());

        return buf.toString();
    }

    public boolean registerSessionManagerMBean(final StatsSessionManager sessionManager) {

        String name = buildSessionManagerName();

        try {
            SessionManagerMBean sessionManagerMBean = new SessionManager(sessionManager);
            ObjectName objectName = new ObjectName(name); 

            registerMBean(sessionManagerMBean, objectName);

            logRegistrationSuccess(true, SessionManagerMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, SessionManagerMBean.class, null, name, e);

            return false;
        }

        return true;
    }

    public boolean unregisterSessionManagerMBean() {

        String name = buildSessionManagerName();

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, SessionManagerMBean.class, null, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, SessionManagerMBean.class, null, name, e);

            return false;
        }

        return true;
    }

    public boolean registerConfigMBean(final StatsKey key,
                                       final org.stajistics.StatsConfig config) {

        String name = buildName(key, TYPE_KEYS, SUBTYPE_CONFIG, false);

        try {
            StatsConfigMBean configMBean = new StatsConfig(config);
            ObjectName objectName = new ObjectName(name);

            registerMBean(configMBean, objectName);

            logRegistrationSuccess(true, StatsConfigMBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigMBean.class, key, name, e);

            return false;
        }

        return true;
    }

    public boolean unregisterConfigMBean(final StatsKey key) {

        String name = buildName(key, TYPE_KEYS, SUBTYPE_CONFIG, false);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsConfigMBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigMBean.class, key, name, e);

            return false;
        }

        return true;
    }

    public boolean registerSessionMBean(final org.stajistics.session.StatsSession session) {

        String name = buildName(session.getKey(), TYPE_KEYS, SUBTYPE_SESSION, true);

        try {
            StatsSessionMBean sessionMBean = new StatsSession(session);

            ObjectName objectName = new ObjectName(name);
            registerMBean(sessionMBean, objectName);

            logRegistrationSuccess(true, StatsSessionMBean.class, session.getKey(), objectName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionMBean.class, session.getKey(), name, e);

            return false;
        }

        return true;
    }

    public boolean unregisterSessionMBean(final StatsKey key) {

        String name = buildName(key, TYPE_KEYS, SUBTYPE_SESSION, true);

        try {
            ObjectName objectName = new ObjectName(name);
            mBeanServer.unregisterMBean(objectName);

            logRegistrationSuccess(false, StatsSessionMBean.class, key, objectName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionMBean.class, key, name, e);

            return false;
        }

        return true;
    }

    private void registerMBean(final Object mBean,
                               final ObjectName name) throws Exception {
        if (mBeanServer.isRegistered(name)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Replacing existing MBean: " + name);
            }

            mBeanServer.unregisterMBean(name);
        }

        mBeanServer.registerMBean(mBean, name);
    }

    protected String buildName(final StatsKey key,
                               final String type,
                               final String subtype,
                               final boolean includeAttributes) {

        StringBuilder buf = new StringBuilder(128);

        buf.append(DOMAIN);

        buf.append(":type=");
        buf.append(type);

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

        if (subtype != null) {
            buf.append(",subtype=");
            buf.append(subtype);
        }

        return buf.toString();
    }

    private void appendValue(final StringBuilder buf,
                             final String value) {

        final boolean valueNeedsQuotes = valueNeedsQuotes(value);
        if (valueNeedsQuotes) {
            buf.append('"');
        }

        buf.append(escapeValue((String)value));

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

        mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }
}

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

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.event.StatsEventHandler;
import org.stajistics.event.StatsEventType;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagement {

    private static Logger logger = LoggerFactory.getLogger(StatsManagement.class);  

    static final String DOMAIN = org.stajistics.Stats.class.getPackage().getName();

    static final String SESSION_TYPE = "session";
    static final String CONFIG_TYPE = "config";

    private static final Pattern VALUE_ESCAPE_BACKSLASH_PATTERN = Pattern.compile("[\\\\]");
    private static final String VALUE_ESCAPE_BACKSLASH_REPLACEMENT = "\\\\\\\\";
    private static final Pattern VALUE_ESCAPE_ASTERISK_PATTERN = Pattern.compile("[*]");
    private static final String VALUE_ESCAPE_ASTERISK_REPLACEMENT = "\\\\*";
    private static final Pattern VALUE_ESCAPE_QUESTION_MARK_PATTERN = Pattern.compile("[?]");
    private static final String VALUE_ESCAPE_QUESTION_MARK_REPLACEMENT = "\\\\?";
    private static final Pattern VALUE_ESCAPE_DOUBLE_QUOTE_PATTERN = Pattern.compile("[\"]");
    private static final String VALUE_ESCAPE_DOUBLE_QUOTE_REPLACEMENT = "\\\\\"";

    protected static StatsManagement instance = new StatsManagement();

    protected StatsManagement() {}

    public static StatsManagement getInstance() {
        return instance;
    }

    protected MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public void initializeManagement() {

        if (logger.isInfoEnabled()) {
            logger.info("Initializing statistics management");
        }

        registerSessionManagerMBean();

        Stats.getEventManager().addGlobalEventHandler(new StatsEventHandler() {
            @Override
            public void handleStatsEvent(final StatsEventType eventType,
                                         final StatsKey key,
                                         final org.stajistics.session.StatsSession session,
                                         final StatsTracker tracker) {
                if (eventType == StatsEventType.SESSION_CREATED) {
                    registerSessionMBean(session);

                } else if (eventType == StatsEventType.SESSION_DESTROYED) {
                    unregisterSessionMBean(key);

                } else if (eventType == StatsEventType.CONFIG_CREATED) {
                    org.stajistics.StatsConfig cfg = Stats.getConfig(key);
                    registerConfigMBean(key, cfg);

                } else if (eventType == StatsEventType.CONFIG_DESTROYED) {
                    unregisterConfigMBean(key);
                }
            }
        });
    }

    ObjectName createSessionManagerObjectName() throws MalformedObjectNameException {
        return new ObjectName(DOMAIN + ":name=" + SessionManager.class.getSimpleName());
    }

    public boolean registerSessionManagerMBean() {
        try {
            MBeanServer mbs = getMBeanServer();

            SessionManagerMBean sessionManagerMBean = new SessionManager();
            mbs.registerMBean(sessionManagerMBean, createSessionManagerObjectName());

            if (logger.isInfoEnabled()) {
                logger.info("Registered " + SessionManagerMBean.class.getSimpleName());
            }

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to register " + SessionManagerMBean.class.getSimpleName(), e);
            }

            return false;
        }

        return true;
    }

    public boolean unregisterSessionManagerMBean() {
        try {
            MBeanServer mbs = getMBeanServer();

            mbs.unregisterMBean(createSessionManagerObjectName());

            if (logger.isInfoEnabled()) {
                logger.info("Unregistered " + SessionManagerMBean.class.getSimpleName());
            }

        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to unregister " + SessionManagerMBean.class.getSimpleName(), e);
            }

            return false;
        }

        return true;
    }

    public boolean registerConfigMBean(final StatsKey key,
                                       final org.stajistics.StatsConfig config) {

        String strName = null;

        try {
            MBeanServer mbs = getMBeanServer();

            StatsConfigMBean configMBean = new StatsConfig(config);

            strName = buildName(key, CONFIG_TYPE, false);
            ObjectName configMBeanName = new ObjectName(strName);

            mbs.registerMBean(configMBean, configMBeanName);

            logRegistrationSuccess(true, StatsConfigMBean.class, key, configMBeanName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsConfigMBean.class, key, strName, e);

            return false;
        }

        return true;
    }

    public boolean unregisterConfigMBean(final StatsKey key) {

        String strName = null;

        try {
            MBeanServer mbs = getMBeanServer();

            strName = buildName(key, CONFIG_TYPE, false);
            ObjectName configMBeanName = new ObjectName(strName);

            mbs.unregisterMBean(configMBeanName);

            logRegistrationSuccess(false, StatsConfigMBean.class, key, configMBeanName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsConfigMBean.class, key, strName, e);

            return false;
        }

        return true;
    }

    public boolean registerSessionMBean(final org.stajistics.session.StatsSession session) {

        String strName = null;

        try {
            MBeanServer mbs = getMBeanServer();

            StatsSessionMBean sessionMBean = new StatsSession(session);

            strName = buildName(session.getKey(), SESSION_TYPE, true);
            ObjectName sessionMBeanName = new ObjectName(strName);

            mbs.registerMBean(sessionMBean, sessionMBeanName);

            logRegistrationSuccess(true, StatsSessionMBean.class, session.getKey(), sessionMBeanName);

        } catch (Exception e) {
            logRegistrationFailure(true, StatsSessionMBean.class, session.getKey(), strName, e);

            return false;
        }

        return true;
    }

    public boolean unregisterSessionMBean(final StatsKey key) {

        String strName = null;

        try {
            MBeanServer mbs = getMBeanServer();

            strName = buildName(key, SESSION_TYPE, true);
            ObjectName sessionMBeanName = new ObjectName(strName);

            mbs.unregisterMBean(sessionMBeanName);

            logRegistrationSuccess(false, StatsSessionMBean.class, key, sessionMBeanName);

        } catch (Exception e) {
            logRegistrationFailure(false, StatsSessionMBean.class, key, strName, e);

            return false;
        }

        return true;
    }

    protected String buildName(final StatsKey key,
                               final String type,
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
            buf.append(" for ");
            buf.append(key.toString());
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
            buf.append(" for ");
            buf.append(key.toString());
            buf.append(", ObjectName: ");
            buf.append(objectName);

            logger.error(buf.toString(), e);
        }
    }

}

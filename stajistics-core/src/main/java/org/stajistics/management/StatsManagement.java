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

    private static final String DOMAIN = org.stajistics.Stats.class.getPackage().getName();

    private static final String SESSION_TYPE = "session";
    private static final String CONFIG_TYPE = "config";

    private static final Pattern VALUE_ESCAPE_ASTERISK_PATTERN = Pattern.compile("[*]");
    private static final Pattern VALUE_ESCAPE_QUESTION_MARK_PATTERN = Pattern.compile("[?]");

    private static StatsManagement instance = new StatsManagement();

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
                    unregisterSessionMBean(session);

                } else if (eventType == StatsEventType.CONFIG_CREATED) {
                    org.stajistics.StatsConfig cfg = Stats.getConfig(key);
                    registerConfigMBean(key, cfg);

                } else if (eventType == StatsEventType.CONFIG_DESTROYED) {
                    //TODO
                }
            }
        });
    }

    public boolean registerSessionManagerMBean() {
        try {
            MBeanServer mbs = getMBeanServer();

            SessionManagerMBean sessionManagerMBean = new SessionManager();
            ObjectName sessionManagerMBeanName = new ObjectName(DOMAIN + ":name=" + SessionManager.class.getSimpleName());

            mbs.registerMBean(sessionManagerMBean, sessionManagerMBeanName);

        } catch (Exception e) {
           logger.error("Failed to register " + SessionManagerMBean.class.getSimpleName(), e);

           return false;
        }

        return true;
    }

    public boolean registerConfigMBean(final StatsKey key,
                                       final org.stajistics.StatsConfig config) {
        try {
            MBeanServer mbs = getMBeanServer();

            StatsConfigMBean configMBean = new StatsConfig(config);
            ObjectName configMBeanName = new ObjectName(makeObjectName(key, CONFIG_TYPE, false));

            mbs.registerMBean(configMBean, configMBeanName);

            if (logger.isInfoEnabled()) {
                logger.info("Registered " + StatsConfigMBean.class.getSimpleName() + ": " + configMBeanName);
            }

        } catch (Exception e) {
            logger.error("Failed to register " + StatsConfigMBean.class.getSimpleName(), e);

            return false;
        }

        return true;
    }

    public boolean registerSessionMBean(final org.stajistics.session.StatsSession session) {
        try {
            MBeanServer mbs = getMBeanServer();

            StatsSessionMBean sessionMBean = new StatsSession(session);
            ObjectName sessionMBeanName = new ObjectName(makeObjectName(session.getKey(), SESSION_TYPE, true));

            mbs.registerMBean(sessionMBean, sessionMBeanName);

            if (logger.isInfoEnabled()) {
                logger.info("Registered " + StatsSessionMBean.class.getSimpleName() + ": " + sessionMBeanName);
            }

        } catch (Exception e) {
            logger.error("Failed to register " + StatsSessionMBean.class.getSimpleName(), e);

            return false;
        }

        return false;
    }

    public boolean unregisterSessionMBean(final org.stajistics.session.StatsSession session) {
        try {
            MBeanServer mbs = getMBeanServer();

            ObjectName sessionMBeanName = new ObjectName(makeObjectName(session.getKey(), SESSION_TYPE, true));

            mbs.unregisterMBean(sessionMBeanName);

            if (logger.isInfoEnabled()) {
                logger.info("Unregistered " + StatsSessionMBean.class.getSimpleName() + ": " + sessionMBeanName);
            }

        } catch (Exception e) {
            logger.error("Failed to unregister " + StatsSessionMBean.class.getSimpleName(), e);
        }

        return true;
    }

    protected String makeObjectName(final StatsKey key,
                                    final String type,
                                    final boolean includeAttributes) {

        StringBuilder buf = new StringBuilder(128);

        buf.append(DOMAIN);

        buf.append(":type=");
        buf.append(type);

        buf.append(",name=");
        buf.append(key.getName());

        if (includeAttributes) {
            for (Map.Entry<String,Object> entry : key.getAttributes().entrySet()) {
                buf.append(',');
                buf.append(entry.getKey());
                buf.append("=\"");
                buf.append(extractValue(entry.getValue()));
                buf.append('"');
            }
        }

        return buf.toString();
    }

    protected String extractValue(final Object value) {

        if (value.getClass() == String.class) {
            return escapeValue((String)value);
        }

        return value.toString();
    }

    protected String escapeValue(String value) {
        value = VALUE_ESCAPE_ASTERISK_PATTERN.matcher(value).replaceAll("\\*");
        value = VALUE_ESCAPE_QUESTION_MARK_PATTERN.matcher(value).replaceAll("\\?");
        return value;
    }
}

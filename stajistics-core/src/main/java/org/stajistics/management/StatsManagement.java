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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.StatsKey;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsManagement {

    private static Logger logger = LoggerFactory.getLogger(StatsManagement.class);  

    public static final String DOMAIN = org.stajistics.Stats.class.getPackage().getName();

    private static StatsManagement instance = new StatsManagement();

    protected StatsManagement() {}

    public static StatsManagement getInstance() {
        return instance;
    }

    protected MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public boolean registerSessionManagerMBean() {
        try {
            MBeanServer mbs = getMBeanServer();

            SessionManagerMBean sessionManagerMBean = new SessionManager();
            ObjectName statsMBeanName = new ObjectName(DOMAIN + ":name=" + SessionManager.class.getSimpleName());

            mbs.registerMBean(sessionManagerMBean, statsMBeanName);

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
            ObjectName configMBeanName = new ObjectName(makeObjectName(key, "config", false));

            mbs.registerMBean(configMBean, configMBeanName);

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
            ObjectName sessionMBeanName = new ObjectName(makeObjectName(session.getKey(), "session", true));

            mbs.registerMBean(sessionMBean, sessionMBeanName);

        } catch (Exception e) {
            logger.error("Failed to register " + StatsSessionMBean.class.getSimpleName(), e);

            return false;
        }

        return false;
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

            //TODO: needs quotes and/or escaping

            for (Map.Entry<String,Object> entry : key.getAttributes().entrySet()) {
                buf.append(',');
                buf.append(entry.getKey());
                buf.append('=');
                buf.append(String.valueOf(entry.getValue()));
            }
        }

        return buf.toString();
    }
}

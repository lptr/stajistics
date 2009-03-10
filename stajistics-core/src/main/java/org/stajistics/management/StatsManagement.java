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

    public static final String DOMAIN = "org.stajistics";

    protected MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public boolean registerStatsMBean() {

        MBeanServer mbs = getMBeanServer();

        StatsMBean statsMBean = new Stats();
        ObjectName statsMBeanName = null;

        try {
           statsMBeanName = new ObjectName(DOMAIN + ":name=" + Stats.class.getSimpleName());
           mbs.registerMBean(statsMBean, statsMBeanName);

        } catch (Exception e) {
           logger.error("Failed to register " + StatsMBean.class.getSimpleName(), e);
           return false;
        }

        return true;
    }

}

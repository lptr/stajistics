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
package org.stajistics.bootstrap;

import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsManager;

import static org.junit.Assert.*;

/**
 * @author The Stajistics Project
 */
public abstract class AbstractStatsManagerFactoryTestCase extends AbstractStajisticsTestCase {

    protected abstract StatsManagerFactory createManagerFactory();

    protected StatsManager createManager() {
        return createManagerFactory().createManager();
    }

    @Test
    public void testCreateManagerNotNull() {
        assertNotNull(createManagerFactory().createManager());
    }

    @Test
    public void testManagerHasNonNullEventManager() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getEventManager());
    }

    @Test
    public void testManagerHasNonNullConfigManager() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getConfigManager());
    }

    @Test
    public void testManagerHasNonNullSessionManager() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getSessionManager());
    }

    @Test
    public void testManagerHasNonNullTaskService() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getTaskService());
    }

    @Test
    public void testManagerHasNonNullTrackerLocator() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getTrackerLocator());
    }

    @Test
    public void testManagerHasNonNullKeyFactory() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getKeyFactory());
    }

    @Test
    public void testManagerHasNonNullConfigFactory() {
        StatsManager mgr = createManager();
        assertNotNull(mgr.getConfigBuilderFactory());
    }

    @Test
    public void testDefaultInitialEnabledStateIsTrue() {
        StatsManager mgr = createManager();
        assertTrue(mgr.isEnabled());
    }

    @Test
    public void testInitialEnabledStateFromSystemPropertyIsTrue() {
        final String propName = StatsManager.class.getName() + ".enabled";
        try {
            System.setProperty(propName, Boolean.TRUE.toString());
            StatsManager mgr = createManager();
            assertTrue(mgr.isEnabled());
        } finally {
            System.getProperties().remove(propName);
        }
    }

    @Test
    public void testInitialEnabledStateFromSystemPropertyIsFalse() {
        final String propName = StatsManager.class.getName() + ".enabled";
        try {
            System.setProperty(propName, Boolean.FALSE.toString());
            StatsManager mgr = createManager();
            assertFalse(mgr.isEnabled());
        } finally {
            System.getProperties().remove(propName);
        }
    }
}

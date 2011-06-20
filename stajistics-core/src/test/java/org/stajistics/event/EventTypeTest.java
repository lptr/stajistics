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
package org.stajistics.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.stajistics.event.EventType.CONFIG_CHANGED;
import static org.stajistics.event.EventType.CONFIG_CREATED;
import static org.stajistics.event.EventType.CONFIG_DESTROYED;
import static org.stajistics.event.EventType.CONFIG_MANAGER_INITIALIZED;
import static org.stajistics.event.EventType.CONFIG_MANAGER_SHUTTING_DOWN;
import static org.stajistics.event.EventType.SESSION_CLEARED;
import static org.stajistics.event.EventType.SESSION_CREATED;
import static org.stajistics.event.EventType.SESSION_DESTROYED;
import static org.stajistics.event.EventType.SESSION_MANAGER_INITIALIZED;
import static org.stajistics.event.EventType.SESSION_MANAGER_SHUTTING_DOWN;
import static org.stajistics.event.EventType.STATS_MANAGER_INITIALIZED;
import static org.stajistics.event.EventType.STATS_MANAGER_SHUTTING_DOWN;
import static org.stajistics.event.EventType.TRACKER_COMMITTED;
import static org.stajistics.event.EventType.TRACKER_TRACKING;

import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class EventTypeTest extends AbstractStajisticsTestCase {

    @Test
    public void testStatsManagerInitialized() {
        assertFalse(STATS_MANAGER_INITIALIZED.isConfigEvent());
        assertTrue(STATS_MANAGER_INITIALIZED.isStatsManagerEvent());
        assertFalse(STATS_MANAGER_INITIALIZED.isSessionManagerEvent());
        assertFalse(STATS_MANAGER_INITIALIZED.isConfigManagerEvent());
        assertFalse(STATS_MANAGER_INITIALIZED.isSessionEvent());
        assertFalse(STATS_MANAGER_INITIALIZED.isTrackerEvent());
    }

    @Test
    public void testStatsManagerShuttingDown() {
        assertFalse(STATS_MANAGER_SHUTTING_DOWN.isConfigEvent());
        assertTrue(STATS_MANAGER_SHUTTING_DOWN.isStatsManagerEvent());
        assertFalse(STATS_MANAGER_SHUTTING_DOWN.isSessionManagerEvent());
        assertFalse(STATS_MANAGER_SHUTTING_DOWN.isConfigManagerEvent());
        assertFalse(STATS_MANAGER_SHUTTING_DOWN.isSessionEvent());
        assertFalse(STATS_MANAGER_SHUTTING_DOWN.isTrackerEvent());
    }

    @Test
    public void testSessionManagerInitialized() {
        assertFalse(SESSION_MANAGER_INITIALIZED.isConfigEvent());
        assertFalse(SESSION_MANAGER_INITIALIZED.isStatsManagerEvent());
        assertTrue(SESSION_MANAGER_INITIALIZED.isSessionManagerEvent());
        assertFalse(SESSION_MANAGER_INITIALIZED.isConfigManagerEvent());
        assertFalse(SESSION_MANAGER_INITIALIZED.isSessionEvent());
        assertFalse(SESSION_MANAGER_INITIALIZED.isTrackerEvent());
    }

    @Test
    public void testSessionManagerShuttingDown() {
        assertFalse(SESSION_MANAGER_SHUTTING_DOWN.isConfigEvent());
        assertFalse(SESSION_MANAGER_SHUTTING_DOWN.isStatsManagerEvent());
        assertTrue(SESSION_MANAGER_SHUTTING_DOWN.isSessionManagerEvent());
        assertFalse(SESSION_MANAGER_SHUTTING_DOWN.isConfigManagerEvent());
        assertFalse(SESSION_MANAGER_SHUTTING_DOWN.isSessionEvent());
        assertFalse(SESSION_MANAGER_SHUTTING_DOWN.isTrackerEvent());
    }

    @Test
    public void testConfigManagerInitialized() {
        assertFalse(CONFIG_MANAGER_INITIALIZED.isConfigEvent());
        assertFalse(CONFIG_MANAGER_INITIALIZED.isStatsManagerEvent());
        assertFalse(CONFIG_MANAGER_INITIALIZED.isSessionManagerEvent());
        assertTrue(CONFIG_MANAGER_INITIALIZED.isConfigManagerEvent());
        assertFalse(CONFIG_MANAGER_INITIALIZED.isSessionEvent());
        assertFalse(CONFIG_MANAGER_INITIALIZED.isTrackerEvent());
    }

    @Test
    public void testConfigManagerShuttingDown() {
        assertFalse(CONFIG_MANAGER_SHUTTING_DOWN.isConfigEvent());
        assertFalse(CONFIG_MANAGER_SHUTTING_DOWN.isStatsManagerEvent());
        assertFalse(CONFIG_MANAGER_SHUTTING_DOWN.isSessionManagerEvent());
        assertTrue(CONFIG_MANAGER_SHUTTING_DOWN.isConfigManagerEvent());
        assertFalse(CONFIG_MANAGER_SHUTTING_DOWN.isSessionEvent());
        assertFalse(CONFIG_MANAGER_SHUTTING_DOWN.isTrackerEvent());
    }

    @Test
    public void testConfigCreated() {
        assertTrue(CONFIG_CREATED.isConfigEvent());
        assertFalse(CONFIG_CREATED.isStatsManagerEvent());
        assertFalse(CONFIG_CREATED.isSessionManagerEvent());
        assertFalse(CONFIG_CREATED.isConfigManagerEvent());
        assertFalse(CONFIG_CREATED.isSessionEvent());
        assertFalse(CONFIG_CREATED.isTrackerEvent());
    }

    @Test
    public void testConfigChanged() {
        assertTrue(CONFIG_CHANGED.isConfigEvent());
        assertFalse(CONFIG_CHANGED.isStatsManagerEvent());
        assertFalse(CONFIG_CHANGED.isSessionManagerEvent());
        assertFalse(CONFIG_CHANGED.isConfigManagerEvent());
        assertFalse(CONFIG_CHANGED.isSessionEvent());
        assertFalse(CONFIG_CHANGED.isTrackerEvent());
    }

    @Test
    public void testConfigDestroyed() {
        assertTrue(CONFIG_DESTROYED.isConfigEvent());
        assertFalse(CONFIG_DESTROYED.isStatsManagerEvent());
        assertFalse(CONFIG_DESTROYED.isSessionManagerEvent());
        assertFalse(CONFIG_DESTROYED.isConfigManagerEvent());
        assertFalse(CONFIG_DESTROYED.isSessionEvent());
        assertFalse(CONFIG_DESTROYED.isTrackerEvent());
    }

    @Test
    public void testSessionCreated() {
        assertFalse(SESSION_CREATED.isStatsManagerEvent());
        assertFalse(SESSION_CREATED.isSessionManagerEvent());
        assertFalse(SESSION_CREATED.isConfigManagerEvent());
        assertFalse(SESSION_CREATED.isConfigEvent());
        assertTrue(SESSION_CREATED.isSessionEvent());
        assertFalse(SESSION_CREATED.isTrackerEvent());
    }

    @Test
    public void testSessionCleared() {
        assertFalse(SESSION_CLEARED.isStatsManagerEvent());
        assertFalse(SESSION_CLEARED.isSessionManagerEvent());
        assertFalse(SESSION_CLEARED.isConfigManagerEvent());
        assertFalse(SESSION_CLEARED.isConfigEvent());
        assertTrue(SESSION_CLEARED.isSessionEvent());
        assertFalse(SESSION_CLEARED.isTrackerEvent());
    }

    @Test
    public void testSessionDestroyed() {
        assertFalse(SESSION_DESTROYED.isStatsManagerEvent());
        assertFalse(SESSION_DESTROYED.isSessionManagerEvent());
        assertFalse(SESSION_DESTROYED.isConfigManagerEvent());
        assertFalse(SESSION_DESTROYED.isConfigEvent());
        assertTrue(SESSION_DESTROYED.isSessionEvent());
        assertFalse(SESSION_DESTROYED.isTrackerEvent());
    }

    @Test
    public void testTrackerTracking() {
        assertFalse(TRACKER_TRACKING.isStatsManagerEvent());
        assertFalse(TRACKER_TRACKING.isSessionManagerEvent());
        assertFalse(TRACKER_TRACKING.isConfigManagerEvent());
        assertFalse(TRACKER_TRACKING.isConfigEvent());
        assertFalse(TRACKER_TRACKING.isSessionEvent());
        assertTrue(TRACKER_TRACKING.isTrackerEvent());
    }

    @Test
    public void testTrackerCommitted() {
        assertFalse(TRACKER_COMMITTED.isStatsManagerEvent());
        assertFalse(TRACKER_COMMITTED.isSessionManagerEvent());
        assertFalse(TRACKER_COMMITTED.isConfigManagerEvent());
        assertFalse(TRACKER_COMMITTED.isConfigEvent());
        assertFalse(TRACKER_COMMITTED.isSessionEvent());
        assertTrue(TRACKER_COMMITTED.isTrackerEvent());
    }
}

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
package org.stajistics.event;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.stajistics.event.StatsEventType.*;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsEventTypeTest {

    @Test
    public void testConfigCreated() {
        assertTrue(CONFIG_CREATED.isConfigEvent());
        assertFalse(CONFIG_CREATED.isSessionEvent());
        assertFalse(CONFIG_CREATED.isTrackerEvent());
    }

    @Test
    public void testConfigChanged() {
        assertTrue(CONFIG_CHANGED.isConfigEvent());
        assertFalse(CONFIG_CHANGED.isSessionEvent());
        assertFalse(CONFIG_CHANGED.isTrackerEvent());
    }

    @Test
    public void testConfigDestroyed() {
        assertTrue(CONFIG_DESTROYED.isConfigEvent());
        assertFalse(CONFIG_DESTROYED.isSessionEvent());
        assertFalse(CONFIG_DESTROYED.isTrackerEvent());
    }

    @Test
    public void testSessionCreated() {
        assertFalse(SESSION_CREATED.isConfigEvent());
        assertTrue(SESSION_CREATED.isSessionEvent());
        assertFalse(SESSION_CREATED.isTrackerEvent());
    }

    @Test
    public void testSessionCleared() {
        assertFalse(SESSION_CLEARED.isConfigEvent());
        assertTrue(SESSION_CLEARED.isSessionEvent());
        assertFalse(SESSION_CLEARED.isTrackerEvent());
    }

    @Test
    public void testSessionDestroyed() {
        assertFalse(SESSION_DESTROYED.isConfigEvent());
        assertTrue(SESSION_DESTROYED.isSessionEvent());
        assertFalse(SESSION_DESTROYED.isTrackerEvent());
    }

    @Test
    public void testTrackerTracking() {
        assertFalse(TRACKER_TRACKING.isConfigEvent());
        assertFalse(TRACKER_TRACKING.isSessionEvent());
        assertTrue(TRACKER_TRACKING.isTrackerEvent());
    }

    @Test
    public void testTrackerCommitted() {
        assertFalse(TRACKER_COMMITTED.isConfigEvent());
        assertFalse(TRACKER_COMMITTED.isSessionEvent());
        assertTrue(TRACKER_COMMITTED.isTrackerEvent());
    }
}

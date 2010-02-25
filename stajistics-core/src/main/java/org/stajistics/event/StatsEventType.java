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

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public enum StatsEventType {

    CONFIG_CREATED(0),
    CONFIG_CHANGED(0),
    CONFIG_DESTROYED(0),

    SESSION_CREATED(1),
    SESSION_CLEARED(1),
    SESSION_RESTORED(1),
    SESSION_DESTROYED(1),

    TRACKER_TRACKING(2),
    TRACKER_COMMITTED(2);

    private final int eventGroup;

    private StatsEventType(final int eventGroup) {
        this.eventGroup = eventGroup;
    }

    public boolean isConfigEvent() {
        return eventGroup == 0;
    }

    public boolean isSessionEvent() {
        return eventGroup == 1;
    }

    public boolean isTrackerEvent() {
        return eventGroup == 2;
    }
}

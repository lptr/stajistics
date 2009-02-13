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
package org.stajistics;

import org.stajistics.event.StatsEventHandler;
import org.stajistics.event.StatsEventType;
import org.stajistics.event.alarm.AbstractAlarmCondition;
import org.stajistics.event.alarm.AlarmCondition;
import org.stajistics.event.alarm.AlarmHandler;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
class TestClient {

    public TestClient() {

        StatsManager.getEventManager().addGlobalEventHandler(
                new StatsEventHandler() {
                    @Override
                    public void handleStatsEvent(
                            final StatsEventType eventType,
                            final StatsSession session,
                            final StatsTracker tracker) {
                        System.out.println(eventType + " -> " + session.getKey());
                    }
                });

        StatsManager.getEventManager().addSessionEventHandler(
                StatsKey.create("NotTest"), new StatsEventHandler() {
                    @Override
                    public void handleStatsEvent(
                            final StatsEventType eventType,
                            final StatsSession session,
                            final StatsTracker tracker) {
                        System.out.println("Not supposed to happen");
                    }
                });

        StatsManager.getEventManager().addGlobalEventHandler(
                new AbstractAlarmCondition(new AlarmHandler() {
                    @Override
                    public void alarmTriggered(final AlarmCondition condition,
                                               final StatsSession session,
                                               final StatsTracker tracker) {
                        System.out.println("Alarm triggered");
                    }
                }) {
                    @Override
                    public void handleStatsEvent(final StatsEventType eventType,
                                                 final StatsSession session, 
                                                 final StatsTracker tracker) {
                        if (eventType == StatsEventType.TRACKER_COMMITTED && session.getLast() > 25) {
                            getAlarmHandler().alarmTriggered(this, session, tracker);
                        }
                    }
                });

    }

    public void test1() {

        StatsTracker tracker = StatsManager.open("Test");

        try {

            for (int i = 0; i < 1000; i++) {
                new Object();
                System.out.print('.');
            }

            System.out.println();

        } finally {
            tracker.commit();
        }

    }

    public static void main(String[] args) throws Exception {
        TestClient testClient = new TestClient();

        for (int i = 0; i < 20; i++) {
            testClient.test1();
            Thread.sleep(250);
        }
    }
}

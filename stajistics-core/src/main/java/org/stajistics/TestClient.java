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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.event.StatsEventHandler;
import org.stajistics.event.StatsEventType;
import org.stajistics.event.alarm.AbstractAlarmCondition;
import org.stajistics.event.alarm.AlarmCondition;
import org.stajistics.event.alarm.AlarmHandler;
import org.stajistics.session.ConcurrentStatsSession;
import org.stajistics.session.StatsSession;
import org.stajistics.session.StatsSessionFactory;
import org.stajistics.session.collector.DistributionDataCollector;
import org.stajistics.session.collector.RangeDataCollector;
import org.stajistics.tracker.ConcurrentAccessTracker;
import org.stajistics.tracker.HitFrequencyTracker;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.util.RangeList;

/**
 * 
 * 
 * 
 * @author The Stajistics Project
 */
class TestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);

    private final StatsKey key1 = Stats.buildConfig("Test")
                                       .withSessionFactory(new StatsSessionFactory() {
                                            @Override
                                            public StatsSession createSession(final StatsKey key) {
                                                return new ConcurrentStatsSession(key,
                                                            new DistributionDataCollector(),
                                                            new RangeDataCollector(RangeList.build()
                                                                                            .addRange(0, 5)
                                                                                            .addRange(5, 10)
                                                                                            .addRange(10, 20)
                                                                                            .addRange(20, 40)
                                                                                            .addRange(40, 80)
                                                                                            .rangeList()));
                                            }
                                       }).newKey();

    private final StatsKey key2 = Stats.buildConfig("Test2")
                                       .withTracker(ConcurrentAccessTracker.class)
                                       .withUnit("accesses")
                                       .newKey();

    private final StatsKey key3 = Stats.buildConfig("Test3")
                                       .withTracker(HitFrequencyTracker.class)
                                       .newKey();

    public TestClient() {

        Stats.getEventManager().addSessionEventHandler(key1,
                new StatsEventHandler() {
                    @Override
                    public void handleStatsEvent(
                            final StatsEventType eventType,
                            final StatsKey key,
                            final Object target) {
                        LOGGER.info(eventType + " -> " + key);
                    }
                });

        Stats.getEventManager().addSessionEventHandler(key1,
                new AbstractAlarmCondition(new AlarmHandler() {
                    @Override
                    public void alarmTriggered(final AlarmCondition condition,
                                               final StatsSession session,
                                               final StatsTracker tracker) {
                        LOGGER.warn("Alarm triggered");
                    }
                }) {
                    @Override
                    public void handleStatsEvent(final StatsEventType eventType,
                                                 final StatsKey key,
                                                 final Object target) {
                        if (eventType == StatsEventType.TRACKER_COMMITTED) {
                            StatsTracker tracker = (StatsTracker)target;
                            StatsSession session = tracker.getSession();
                            if (tracker.getSession().getLast() > 25) {
                                getAlarmHandler().alarmTriggered(this, session, tracker);
                            }
                        }
                    }
                });


    }

    public void test1() {

        StatsKey key4 = key3.buildCopy()
                            .withAttribute("cool", true) // pretend runtime data
                            .newKey();

        StatsKey key5 = key4.buildCopy()
                            .withAttribute("coolest", false) // pretend runtime data
                            .newKey();

        StatsTracker tracker = Stats.track(key1, key2, key3, key4, key5);

        try {

            for (int i = 0; i < 1000; i++) {
                new Object();
            }

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

        Thread.sleep(1000000);
    }
}

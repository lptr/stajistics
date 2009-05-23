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
package org.stajistics.event.alarm.threashold;

import org.stajistics.StatsKey;
import org.stajistics.event.StatsEventType;
import org.stajistics.event.alarm.AbstractAlarmCondition;
import org.stajistics.event.alarm.AlarmHandler;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreasholdAlarmCondition extends AbstractAlarmCondition {

    private static final long serialVersionUID = -7063704180952155773L;

    public ThreasholdAlarmCondition(final AlarmHandler alarmHandler) {
        super(alarmHandler);
    }

    @Override
    public AlarmHandler getAlarmHandler() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void handleStatsEvent(final StatsEventType eventType,
                                 final StatsKey key,
                                 final Object target) {

    }
}

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
package org.stajistics.session;

import java.io.Serializable;
import java.util.List;

import org.stajistics.StatsKey;
import org.stajistics.session.collector.DataCollector;
import org.stajistics.session.data.DataSet;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface StatsSession extends Serializable {

    StatsKey getKey();

    List<DataCollector> getDataCollectors();

    long getHits();

    long getFirstHitStamp();

    long getLastHitStamp();

    long getCommits();

    double getFirst();

    double getLast();

    double getMin();

    double getMax();

    double getSum();

    DataSet dataSet();

    void track(StatsTracker tracker, long now);

    void update(StatsTracker tracker, long now);

    StatsSession snapshot();

    void clear();

}

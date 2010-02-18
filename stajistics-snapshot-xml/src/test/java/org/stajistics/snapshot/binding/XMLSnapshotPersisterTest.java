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
package org.stajistics.snapshot.binding;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.snapshot.binding.impl.jibx.XMLSessionSnapshot;
import org.stajistics.snapshot.binding.impl.jibx.XMLStatsSnapshot;
import org.stajistics.tracker.span.SpanTracker;

/**
 * 
 * @author The Stajistics Project
 */
public class XMLSnapshotPersisterTest {

    private XMLSnapshotMarshaller persister;

    @Before
    public void setUp() {
        persister = new XMLSnapshotMarshaller();
    }

    @Test
    public void testMarshalUnmarshalEmptySnapshot() throws Exception {

        StatsSnapshot snapshot = new XMLStatsSnapshot();

        StringWriter out = new StringWriter();

        persister.marshal(snapshot, out);

        StringReader in = new StringReader(out.toString());

        StatsSnapshot snapshot2 = persister.unmarshal(in);

        assertEquals(snapshot, snapshot2);
    }

    @Test
    public void testMarshalUnmarshalPopulatedSnapshot() throws Exception {

        StatsSnapshot snapshot = new XMLStatsSnapshot();
        Map<StatsKey,SessionSnapshot> sessionSnapshots = new HashMap<StatsKey,SessionSnapshot>();
        snapshot.setSessionSnapshots(sessionSnapshots);

        StatsManager statsManager = DefaultStatsManager.createWithDefaults();
        StatsKey key1 = statsManager.getKeyFactory()
                                    .createKey("test1");
        SpanTracker tracker1 = statsManager.getTrackerLocator()
                                           .getSpanTracker(key1)
                                           .track();
        Thread.sleep(new Random().nextInt(100));
        tracker1.commit();

        sessionSnapshots.put(key1, new XMLSessionSnapshot(statsManager.getSessionManager()
                                                                       .getSession(key1),
                                                           statsManager.getConfigManager()
                                                                       .getConfig(key1)));

        StringWriter out = new StringWriter();

        persister.marshal(snapshot, out);

        StringReader in = new StringReader(out.toString());

        StatsSnapshot snapshot2 = persister.unmarshal(in);

        assertEquals(snapshot, snapshot2);
        
    }
}

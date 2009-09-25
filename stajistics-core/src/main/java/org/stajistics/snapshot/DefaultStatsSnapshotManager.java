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
package org.stajistics.snapshot;

import java.io.IOException;
import java.io.OutputStream;

import org.stajistics.Stats;
import org.stajistics.snapshot.binding.StatsSnapshot;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultStatsSnapshotManager implements StatsSnapshotManager {

    private static final long serialVersionUID = 5679286432055594788L;

    private SnapshotMarshaller marshaller;

    public DefaultStatsSnapshotManager(final SnapshotMarshaller marshaller) {
        if (marshaller == null) {
            throw new NullPointerException("marshaller");
        }

        this.marshaller = marshaller;
    }

    public static DefaultStatsSnapshotManager createWithDefaults() {
        SnapshotMarshaller marshaller = new DefaultSnapshotMarshallerFactory().createSnapshotMarshaller();
        return new DefaultStatsSnapshotManager(marshaller);
    }

    @Override
    public void takeSnapshot(final StatsSnapshotDestination destination) 
            throws IOException, SnapshotPersistenceException {
        StatsSnapshot snapshot = new DefaultSnapshotController().takeSnapshot(Stats.getManager());

        OutputStream out = destination.newOutputStream(snapshot);
        marshaller.marshal(snapshot, out);
    }
}

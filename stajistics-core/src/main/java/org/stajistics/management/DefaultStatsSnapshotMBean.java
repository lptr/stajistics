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
package org.stajistics.management;

import java.io.IOException;

import org.stajistics.snapshot.SingleFileStatsSnapshotDestination;
import org.stajistics.snapshot.SnapshotPersistenceException;
import org.stajistics.snapshot.StatsSnapshotDestination;
import org.stajistics.snapshot.StatsSnapshotManager;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class DefaultStatsSnapshotMBean implements StatsSnapshotMBean {

    private final StatsSnapshotManager snapshotManager;

    public DefaultStatsSnapshotMBean(final StatsSnapshotManager snapshotManager) {
        if (snapshotManager == null) {
            throw new NullPointerException("snapshotManager");
        }

        this.snapshotManager = snapshotManager;
    }

    @Override
    public void takeSnapshot(final String fileName) throws IOException, SnapshotPersistenceException {
        if (fileName == null) {
            throw new NullPointerException("fileName");
        }
        if (fileName.length() == 0) {
            throw new IllegalArgumentException("empty fileName");
        }

        StatsSnapshotDestination snapshotDest = new SingleFileStatsSnapshotDestination(fileName);
        snapshotManager.takeSnapshot(snapshotDest);
    }

}

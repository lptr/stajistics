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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.stajistics.snapshot.binding.StatsSnapshot;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class SingleFileStatsSnapshotDestination implements StatsSnapshotDestination {

    private static final long serialVersionUID = -6322386948582007294L;

    private final String fileName;

    public SingleFileStatsSnapshotDestination(final String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName");
        }

        this.fileName = fileName;
    }

    @Override
    public String getDescription() {
        return "Single file: " + fileName;
    }

    @Override
    public OutputStream newOutputStream(final StatsSnapshot snapshot) throws IOException {

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        OutputStream result = new BufferedOutputStream(new FileOutputStream(file));

        return result;
    }

}

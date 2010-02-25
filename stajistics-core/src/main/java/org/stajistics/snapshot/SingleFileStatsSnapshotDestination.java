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
package org.stajistics.snapshot;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.stajistics.snapshot.binding.StatsSnapshot;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class SingleFileStatsSnapshotDestination implements StatsSnapshotDestination {

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

    protected String getFileName(final String ext) {
        String result = fileName;
        if (!result.toLowerCase()
                   .endsWith("." + ext.toLowerCase())) {
            result += "." + ext;
        }
        return result;
    }

    @Override
    public void marshal(final StatsSnapshot snapshot, 
                        final SnapshotMarshaller marshaller) throws IOException,SnapshotPersistenceException {
        if (snapshot == null) {
            throw new NullPointerException("snapshot");
        }
        if (marshaller == null) {
            throw new NullPointerException("marshaller");
        }

        String fileName = getFileName(marshaller.getFileExtension());

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        if (marshaller.supportsCharacterStreams()) {
            Writer writer = new BufferedWriter(new FileWriter(file));
            try {
                marshaller.marshal(snapshot, writer);
            } finally {
                try {
                    writer.close();
                } catch (IOException ioe) {}
            }

        } else {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            try {
                marshaller.marshal(snapshot, out);
            } finally {
                try {
                    out.close();
                } catch (IOException ioe) {}
            }
        }
    }

}

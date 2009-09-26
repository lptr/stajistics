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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.stajistics.snapshot.binding.StatsSnapshot;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class SerialSnapshotMarshaller implements SnapshotMarshaller {

    public static final String CONTENT_TYPE = "application/octet-stream";
    public static final String FILE_EXTENSION = "ser";

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }

    @Override
    public boolean supportsCharacterStreams() {
        return false;
    }

    @Override
    public void marshal(final StatsSnapshot snapshot, final OutputStream out)
            throws IOException, SnapshotPersistenceException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(out));
        oos.writeObject(snapshot);
        oos.flush();
    }

    @Override
    public StatsSnapshot unmarshal(final InputStream in) 
            throws IOException,SnapshotPersistenceException {
        ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(in));
        try {
            return (StatsSnapshot) ois.readObject();

        } catch (ClassNotFoundException e) {
            throw new SnapshotPersistenceException(e);
        }
    }

    @Override
    public void marshal(final StatsSnapshot snapshot, final Writer writer)
            throws IOException, SnapshotPersistenceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public StatsSnapshot unmarshal(final Reader reader) 
            throws IOException,SnapshotPersistenceException {
        throw new UnsupportedOperationException();
    }

}

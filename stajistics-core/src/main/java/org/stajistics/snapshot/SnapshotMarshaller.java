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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.stajistics.snapshot.binding.StatsSnapshot;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface SnapshotMarshaller {

    /**
     * 
     * @return
     */
    boolean supportsCharacterStreams();

    /**
     * 
     * @return
     */
    String getContentType();

    /**
     * 
     * @param persistable
     * @param out
     * @throws IOException
     * @throws SnapshotPersistenceException
     */
    void marshal(StatsSnapshot snapshot, OutputStream out) throws IOException,SnapshotPersistenceException;

    /**
     * 
     * @param persistable
     * @param writer
     * @throws IOException
     * @throws SnapshotPersistenceException
     * @throws UnsupportedOperationException If {@link #supportsCharacterStreams()} returns <tt>false</tt>.
     */
    void marshal(StatsSnapshot snapshot, Writer writer) throws IOException,SnapshotPersistenceException;

    /**
     * 
     * @param in
     * @return
     * @throws IOException
     * @throws SnapshotPersistenceException
     */
    StatsSnapshot unmarshal(InputStream in) throws IOException,SnapshotPersistenceException;

    /**
     * 
     * @param reader
     * @return
     * @throws IOException
     * @throws SnapshotPersistenceException
     * @throws UnsupportedOperationException If {@link #supportsCharacterStreams()} returns <tt>false</tt>.
     */
    StatsSnapshot unmarshal(Reader reader) throws IOException,SnapshotPersistenceException;
}

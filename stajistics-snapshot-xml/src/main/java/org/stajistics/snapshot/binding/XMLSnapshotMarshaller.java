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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.stajistics.snapshot.SnapshotMarshaller;
import org.stajistics.snapshot.SnapshotPersistenceException;
import org.stajistics.snapshot.binding.impl.jibx.XMLStatsSnapshot;

/**
 * 
 * @author The Stajistics Project
 *
 */
public class XMLSnapshotMarshaller implements SnapshotMarshaller {

    private static final long serialVersionUID = 7357285120783625486L;

    public static final String CONTENT_TYPE = "application/stajistics-snapshot+xml";
    public static final String FILE_EXTENSION = "xml";

    private static final String CHARSET = "UTF-8";

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
        return true;
    }

    @Override
    public void marshal(final StatsSnapshot snapshot, 
                        final OutputStream out)
            throws IOException, SnapshotPersistenceException {
        doMarshal(snapshot, out, null);
    }

    @Override
    public void marshal(final StatsSnapshot snapshot, 
                        final Writer writer)
            throws IOException, SnapshotPersistenceException {
        doMarshal(snapshot, null, writer);
    }

    private void doMarshal(final StatsSnapshot snapshot,
                           final OutputStream os,
                           final Writer writer) throws SnapshotPersistenceException {
        try {
            IBindingFactory bfact = 
                BindingDirectory.getFactory(XMLStatsSnapshot.class);

            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);

            if (os != null) {
                mctx.marshalDocument(snapshot, CHARSET, null, os);
            } else if (writer != null) {
                mctx.marshalDocument(snapshot, CHARSET, null, writer);
            } else {
                throw new InternalError();
            }

        } catch (JiBXException e) {
            throw new SnapshotPersistenceException(e);
        }
    }

    @Override
    public StatsSnapshot unmarshal(final InputStream in) throws IOException,SnapshotPersistenceException {
        return doUnmarshal(in, null);
    }

    @Override
    public StatsSnapshot unmarshal(final Reader reader) throws IOException,SnapshotPersistenceException {
        return doUnmarshal(null, reader);
    }

    private StatsSnapshot doUnmarshal(final InputStream is, final Reader reader)
            throws SnapshotPersistenceException {
        StatsSnapshot snapshot;
        try {
            IBindingFactory bfact = 
                BindingDirectory.getFactory(XMLStatsSnapshot.class);

            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

            if (is != null) {
                snapshot = (StatsSnapshot)uctx.unmarshalDocument(is, CHARSET);
            } else if (reader != null) {
                snapshot = (StatsSnapshot)uctx.unmarshalDocument(reader);
            } else {
                throw new InternalError();
            }

        } catch (JiBXException e) {
            throw new SnapshotPersistenceException(e);
        }

        return snapshot;
    }

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java " + XMLSnapshotMarshaller.class.getName() + " <file>");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            System.err.println("File not found: " + file);
            System.exit(2);
        }

        XMLSnapshotMarshaller xmlMarshaller = new XMLSnapshotMarshaller();

        System.out.println("Unmarshalling: " + file);
        StatsSnapshot snapshot = xmlMarshaller.unmarshal(new FileInputStream(file));
        System.out.println("Done unmarshalling.");

        File outFile = new File(file.getPath() + ".out");
        System.out.println("Marshalling: " + outFile);
        xmlMarshaller.marshal(snapshot, new FileOutputStream(outFile));
        System.out.println("Done marshalling.");
    }
}

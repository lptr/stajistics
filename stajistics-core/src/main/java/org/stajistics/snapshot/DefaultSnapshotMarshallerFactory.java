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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author The Stajistics Project
 */
public class DefaultSnapshotMarshallerFactory implements SnapshotMarshallerFactory {

    private static final String DEFAULT_SNAPSHOT_MARSHALLER_CLASS_NAME = 
        "org.stajistics.snapshot.binding.XMLSnapshotMarshaller";

    @Override
    public SnapshotMarshaller createSnapshotMarshaller() {
        SnapshotMarshaller marshaller = null;

        try {
            Class<?> marshallerClass = Class.forName(DEFAULT_SNAPSHOT_MARSHALLER_CLASS_NAME);
            marshaller = (SnapshotMarshaller)marshallerClass.newInstance();

        } catch (Exception e) {
            Logger logger = Logger.getLogger(DefaultSnapshotMarshallerFactory.class.getName());
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Failed to locate XMLSnapshotMarshaller; falling back on SerialSnapshotMarshaller.");
            }

            marshaller = new SerialSnapshotMarshaller();
        }

        return marshaller;
    }

}

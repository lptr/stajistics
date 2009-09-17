package org.stajistics.snapshot;

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
            e.printStackTrace();
        }

        return marshaller;
    }

}

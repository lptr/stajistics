package org.stajistics.io;

import static org.stajistics.Util.assertNotNull;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.manual.ManualTracker;

/**
 * @author The Stajistics Project
 */
public class StatsFilterOutputStream extends FilterOutputStream {

    private ManualTracker tracker;

    public StatsFilterOutputStream(final StatsKey key,
                                   final OutputStream out) {
        this(null, key, out);
    }

    public StatsFilterOutputStream(StatsFactory factory,
                                   final StatsKey key,
                                   final OutputStream out) {
        super(out);

        if (factory == null) {
            factory = StatsFactory.forNamespace(StatsConstants.DEFAULT_NAMESPACE);
        }

        assertNotNull(key, "key");
        tracker = factory.getManualTracker(key);
    }

    @Override
    public void write(final int b) throws IOException {
        tracker.addValue(1);
        out.write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        tracker.addValue(b.length);
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        tracker.addValue(len);
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (tracker != null) {
            tracker.commit();
            tracker = null;
        }
        out.close();
    }
}

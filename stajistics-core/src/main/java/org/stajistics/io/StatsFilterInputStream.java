package org.stajistics.io;

import static org.stajistics.Util.assertNotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.manual.ManualTracker;

/**
 * @author The Stajistics Project
 */
public class StatsFilterInputStream extends FilterInputStream {

    private ManualTracker tracker;

    public StatsFilterInputStream(final StatsKey key,
                                  final InputStream in) {
        this(null, key, in);
    }

    public StatsFilterInputStream(StatsFactory factory,
                                  final StatsKey key,
                                  final InputStream in) {
        super(in);

        if (factory == null) {
            factory = StatsFactory.forNamespace(StatsConstants.DEFAULT_NAMESPACE);
        }

        assertNotNull(key, "key");
        tracker = factory.getManualTracker(key);
    }

    @Override
    public int read() throws IOException {
        final int i = in.read();
        if (i > -1) {
            tracker.addValue(1);
        }
        return i;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        final int i = in.read(b);
        if (i > -1) {
            tracker.addValue(i);
        }
        return i;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int i = in.read(b, off, len);
        if (i > -1) {
            tracker.addValue(i);
        }
        return i;
    }

    @Override
    public long skip(final long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        if (tracker != null) {
            tracker.commit();
            tracker = null;
        }
        in.close();
    }

    @Override
    public void mark(final int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }
}

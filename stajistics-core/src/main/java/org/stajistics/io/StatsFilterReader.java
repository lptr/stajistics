package org.stajistics.io;

import static org.stajistics.Util.assertNotNull;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.manual.ManualTracker;

/**
 * @author The Stajistics Project
 */
public class StatsFilterReader extends FilterReader {

    private ManualTracker tracker;

    public StatsFilterReader(final StatsKey key,
                             final Reader in) {
        this(null, key, in);
    }

    public StatsFilterReader(StatsFactory factory,
                             final StatsKey key,
                             final Reader in) {
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
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        final int i = in.read(cbuf, off, len);
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
    public boolean ready() throws IOException {
        return in.ready();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        in.mark(readAheadLimit);
    }

    @Override
    public void reset() throws IOException {
        in.reset();
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
    public int read(final CharBuffer target) throws IOException {
        final int i = in.read(target);
        if (i > -1) {
            tracker.addValue(i);
        }
        return i;
    }

    @Override
    public int read(final char[] cbuf) throws IOException {
        final int i = in.read(cbuf);
        if (i > -1) {
            tracker.addValue(i);
        }
        return i;
    }
}

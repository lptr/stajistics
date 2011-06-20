package org.stajistics.io;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.tracker.manual.ManualTracker;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author The Stajistics Project
 */
public class StatsInputStream extends FilterInputStream {

    private final StatsManager statsManager;
    private final StatsKey key;

    private final ManualTracker tracker;

    protected StatsInputStream(final StatsManager statsManager,
                               final StatsKey key,
                               final InputStream in) {
        super(in);

        if (statsManager == null) {
            this.statsManager = Stats.getManager();
        } else {
            this.statsManager = statsManager;
        }

        if (key == null) {
            throw new NullPointerException("key");
        }

        this.key = key;

        tracker = statsManager.getTrackerLocator().getManualTracker(key);
    }

    @Override
    public int read() throws IOException {
        final int i = in.read();
        if (i > -1) {
            tracker.addValue(1).commit();
        }
        return i;
    }

    @Override
    public int read(final byte[] b) throws IOException {
        final int i = in.read(b);
        if (i > -1) {
            tracker.addValue(i).commit();
        }
        return i;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int i = in.read(b, off, len);
        if (i > -1) {
            tracker.addValue(i).commit();
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

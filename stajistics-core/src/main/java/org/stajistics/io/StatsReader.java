package org.stajistics.io;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.tracker.manual.ManualTracker;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * @author The Stajistics Project
 */
public class StatsReader extends FilterReader {

    private final StatsManager statsManager;
    private final StatsKey key;

    private final ManualTracker tracker;

    public StatsReader(final StatsManager statsManager,
                       final StatsKey key,
                       final Reader in) {
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
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        final int i = in.read(cbuf, off, len);
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
        in.close();
    }

    @Override
    public int read(final CharBuffer target) throws IOException {
        final int i = in.read(target);
        if (i > -1) {
            tracker.addValue(i).commit();
        }
        return i;
    }

    @Override
    public int read(final char[] cbuf) throws IOException {
        final int i = in.read(cbuf);
        if (i > -1) {
            tracker.addValue(i).commit();
        }
        return i;
    }
}

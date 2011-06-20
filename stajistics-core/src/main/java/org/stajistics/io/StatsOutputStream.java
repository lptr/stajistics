package org.stajistics.io;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.tracker.manual.ManualTracker;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author The Stajistics Project
 */
public class StatsOutputStream extends FilterOutputStream {

    private final StatsManager statsManager;
    private final StatsKey key;

    private final ManualTracker tracker;

    public StatsOutputStream(final StatsKey key,
                             final OutputStream out) {
        this(null, key, out);
    }

    public StatsOutputStream(final StatsManager statsManager,
                             final StatsKey key,
                             final OutputStream out) {
        super(out);

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
    public void write(final int b) throws IOException {
        tracker.addValue(1).commit();
        out.write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        tracker.addValue(b.length).commit();
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        tracker.addValue(len).commit();
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}

package org.stajistics.io;

import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.tracker.manual.ManualTracker;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author The Stajistics Project
 */
public class StatsWriter extends FilterWriter {

    private final StatsManager statsManager;
    private final StatsKey key;

    private final ManualTracker tracker;

    protected StatsWriter(final StatsManager statsManager,
                          final StatsKey key,
                          final Writer out) {
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
    public void write(final int c) throws IOException {
        out.write(c);
        tracker.addValue(1).commit();
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        out.write(cbuf, off, len);
        tracker.addValue(len).commit();
    }

    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        out.write(str, off, len);
        tracker.addValue(len).commit();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void write(final char[] cbuf) throws IOException {
        out.write(cbuf);
        tracker.addValue(cbuf.length).commit();
    }

    @Override
    public void write(final String str) throws IOException {
        out.write(str);
        tracker.addValue(str.length()).commit();
    }

    @Override
    public Writer append(final CharSequence csq) throws IOException {
        out.append(csq);
        tracker.addValue(csq.length()).commit();
        return this;
    }

    @Override
    public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
        out.append(csq, start, end);
        tracker.addValue(end - start).commit();
        return this;
    }

    @Override
    public Writer append(final char c) throws IOException {
        out.append(c);
        tracker.addValue(1).commit();
        return this;
    }
}

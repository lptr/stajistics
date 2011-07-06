package org.stajistics.io;

import static org.stajistics.Util.assertNotNull;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.manual.ManualTracker;

/**
 * @author The Stajistics Project
 */
public class StatsFilterWriter extends FilterWriter {

    private ManualTracker tracker;

    public StatsFilterWriter(final StatsKey key,
                             final Writer out) {
        this(null, key, out);
    }

    public StatsFilterWriter(StatsFactory factory,
                             final StatsKey key,
                             final Writer out) {
        super(out);

        if (factory == null) {
            factory = StatsFactory.forNamespace(StatsConstants.DEFAULT_NAMESPACE);
        }

        assertNotNull(key, "key");
        tracker = factory.getManualTracker(key);
    }

    @Override
    public void write(final int c) throws IOException {
        out.write(c);
        tracker.addValue(1);
    }

    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        out.write(cbuf, off, len);
        tracker.addValue(len);
    }

    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        out.write(str, off, len);
        tracker.addValue(len);
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

    @Override
    public void write(final char[] cbuf) throws IOException {
        out.write(cbuf);
        tracker.addValue(cbuf.length);
    }

    @Override
    public void write(final String str) throws IOException {
        out.write(str);
        tracker.addValue(str.length());
    }

    @Override
    public Writer append(final CharSequence csq) throws IOException {
        out.append(csq);
        tracker.addValue(csq.length());
        return this;
    }

    @Override
    public Writer append(final CharSequence csq, final int start, final int end) throws IOException {
        out.append(csq, start, end);
        tracker.addValue(end - start);
        return this;
    }

    @Override
    public Writer append(final char c) throws IOException {
        out.append(c);
        tracker.addValue(1);
        return this;
    }
}

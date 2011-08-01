package org.stajistics.integration.servlet;

import static org.stajistics.Util.assertNotNull;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import org.stajistics.Stats;
import org.stajistics.StatsConstants;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.tracker.manual.ManualTracker;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsServletOutputStream extends ServletOutputStream {

    private final ServletOutputStream out;

    private ManualTracker tracker;

    public StatsServletOutputStream(final StatsKey key,
                                    final ServletOutputStream out) {
        this(null, key, out);
    }

    public StatsServletOutputStream(StatsFactory factory,
                                    final StatsKey key,
                                    final ServletOutputStream out) {
        super();

        if (factory == null) {
            factory = Stats.getFactory(StatsConstants.DEFAULT_NAMESPACE);
        }

        assertNotNull(out, "out");
        this.out = out;

        assertNotNull(key, "key");
        tracker = factory.getManualTracker(key);
    }

    @Override
    public void write(final int b) throws IOException {
        tracker.addValue(1);
        out.write(b);
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

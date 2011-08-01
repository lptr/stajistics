package org.stajistics.integration.servlet;

import static org.stajistics.Util.assertNotNull;

import java.io.IOException;

import javax.servlet.ServletInputStream;

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
public class StatsServletInputStream extends ServletInputStream {

	private ManualTracker tracker;
	private final ServletInputStream in;

	public StatsServletInputStream(StatsFactory factory, 
								   final StatsKey key,
								   final ServletInputStream in) {
		super();

		if (factory == null) {
			factory = Stats.getFactory(StatsConstants.DEFAULT_NAMESPACE);
		}

		assertNotNull(in, "in");
		this.in = in; 

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
	public void close() throws IOException {
        if (tracker != null) {
            tracker.commit();
            tracker = null;
        }
        in.close();
	}

}

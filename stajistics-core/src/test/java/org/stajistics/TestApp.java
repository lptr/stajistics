package org.stajistics;

import org.stajistics.tracker.span.SpanTracker;

/**
 *
 * @author The Stajistics Project
 *
 */
public class TestApp {

    public static void main(String[] args) throws Exception {

        StatsFactory statsFactory = StatsFactory.forClass(TestApp.class);

        StatsKey key = statsFactory.newKey("Test1");

        SpanTracker tracker = statsFactory.track(key);

        for (long i = 0; i < 100000; i++) {
            Long.toString(i);
        }

        tracker.commit();

        Object o = new Object();
        synchronized (o) {
            o.wait();
        }
    }

}

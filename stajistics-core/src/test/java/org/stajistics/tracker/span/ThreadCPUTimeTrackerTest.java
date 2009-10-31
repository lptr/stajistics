package org.stajistics.tracker.span;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreadCPUTimeTrackerTest extends AbstractSpanStatsTrackerTestCase {

    @Override
    protected SpanTracker createStatsTracker() {
        return new ThreadCPUTimeTracker(mockSession);
    }

}

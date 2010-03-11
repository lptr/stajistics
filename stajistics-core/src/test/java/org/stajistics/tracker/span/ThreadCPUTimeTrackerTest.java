package org.stajistics.tracker.span;

import org.stajistics.session.StatsSession;


/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreadCPUTimeTrackerTest extends AbstractSpanTrackerTestCase {

    @Override
    protected SpanTracker createStatsTracker(final StatsSession session) {
        return new ThreadCPUTimeTracker(session);
    }

}

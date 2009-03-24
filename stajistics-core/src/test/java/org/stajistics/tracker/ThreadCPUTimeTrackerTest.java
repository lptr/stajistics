package org.stajistics.tracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ThreadCPUTimeTrackerTest extends AbstractStatsTrackerTestCase {

    @Override
    protected StatsTracker createStatsTracker() {
        return new ThreadCPUTimeTracker(mockSession);
    }

}

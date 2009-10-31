package org.stajistics.tracker;

import java.util.List;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public interface CompositeStatsTracker<T extends StatsTracker> extends StatsTracker {

    List<T> getTrackers();

}

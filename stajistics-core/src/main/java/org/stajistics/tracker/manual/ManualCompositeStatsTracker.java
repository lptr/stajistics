package org.stajistics.tracker.manual;

import java.util.List;

import org.stajistics.tracker.AbstractCompositeStatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class ManualCompositeStatsTracker 
    extends AbstractCompositeStatsTracker<ManualTracker>
    implements ManualTracker {

    private static final long serialVersionUID = -213407626510448696L;

    public ManualCompositeStatsTracker(final ManualTracker... trackers) {
        super(trackers);
    }

    public ManualCompositeStatsTracker(final List<ManualTracker> trackers) { 
        super(trackers);
    }

    @Override
    public ManualTracker addValue(final double value) {
        
        return this;
    }

    @Override
    public ManualTracker setValue(final double value) {

        return this;
    }

    @Override
    public ManualTracker commit() {

        return null;
    }
}

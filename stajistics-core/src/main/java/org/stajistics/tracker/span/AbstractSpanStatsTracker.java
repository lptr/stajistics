package org.stajistics.tracker.span;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.AbstractStatsTracker;
import org.stajistics.tracker.StatsTracker;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractSpanStatsTracker extends AbstractStatsTracker 
    implements SpanTracker {

    private static final long serialVersionUID = 2051648853298387629L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractSpanStatsTracker.class);

    protected boolean tracking = false;

    public AbstractSpanStatsTracker(final StatsSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SpanTracker start() {

        if (tracking) {
            logger.warn("start() called when already started: {}", this);

            return this;
        }

        tracking = true;

        timeStamp = System.currentTimeMillis();

        startImpl(timeStamp);

        return this;
    }

    protected void startImpl(final long now) {
        session.track(this, now);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SpanTracker stop() {

        if (!tracking) {
            logger.warn("stop() called when not started: {}", this);

            return this;
        }

        tracking = false;

        stopImpl(-1);

        return this;
    }

    protected void stopImpl(final long now) {
        session.update(this, now);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTracking() {
        return tracking;
    }

    @Override
    public StatsTracker reset() {
        super.reset();
        tracking = false;
        return this;
    }
}

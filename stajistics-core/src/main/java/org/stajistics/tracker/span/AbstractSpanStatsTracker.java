package org.stajistics.tracker.span;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stajistics.session.StatsSession;
import org.stajistics.tracker.AbstractStatsTracker;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public abstract class AbstractSpanStatsTracker extends AbstractStatsTracker 
    implements SpanTracker {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSpanStatsTracker.class);

    protected long startTime = 0L;
    protected boolean tracking = false;

    public AbstractSpanStatsTracker(final StatsSession session) {
        super(session);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SpanTracker track() {

        if (tracking) {
            logger.warn("track() called when already tracking: {}", this);

            return this;
        }

        tracking = true;

        startTime = System.currentTimeMillis();

        startImpl(startTime);

        return this;
    }

    protected void startImpl(final long now) {
        session.track(this, now);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void commit() {

        if (!tracking) {
            logger.warn("commit() called when not tracking: {}", this);

            return;
        }

        tracking = false;

        stopImpl(-1);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public long getStartTime() {
        return startTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatsTracker reset() {
        super.reset();
        startTime = 0L;
        tracking = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(256);

        buf.append(getClass().getSimpleName());
        buf.append("[startTime=");
        buf.append(new Date(startTime));
        buf.append(",tracking=");
        buf.append(tracking);
        buf.append(",value=");
        buf.append(value);
        buf.append(",session=");
        buf.append(session);
        buf.append(']');

        return buf.toString();
    }
    
    public abstract static class AbstractSpanStatsTrackerFactory implements StatsTrackerFactory<SpanTracker> {

        @Override
        public Class<SpanTracker> getTrackerType() {
            return SpanTracker.class;
        }
    }
}

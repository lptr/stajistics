package org.stajistics.jdbc.wrapper;

import org.stajistics.jdbc.decorator.AbstractXAResourceDecorator;

import javax.transaction.xa.XAResource;

/**
 *
 * @author The Stajistics Project
 *
 */
public class StatsXAResourceWrapper extends AbstractXAResourceDecorator {

    public StatsXAResourceWrapper(final XAResource delegate) {
        super(delegate);
    }


}

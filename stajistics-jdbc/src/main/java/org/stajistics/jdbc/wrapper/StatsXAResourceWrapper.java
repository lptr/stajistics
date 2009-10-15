package org.stajistics.jdbc.wrapper;

import javax.transaction.xa.XAResource;

import org.stajistics.jdbc.decorator.AbstractXAResourceDecorator;

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

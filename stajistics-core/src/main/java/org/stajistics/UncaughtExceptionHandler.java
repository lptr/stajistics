package org.stajistics;

import java.io.Serializable;

/**
 * @author The Stajistics Project
 */
public interface UncaughtExceptionHandler extends Serializable {

    /**
     * 
     * @param key A key related to the Exception, or <tt>null</tt>.
     * @param e
     */
    void uncaughtException(StatsKey key, Throwable e);

}

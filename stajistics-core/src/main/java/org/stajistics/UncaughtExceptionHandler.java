package org.stajistics;

import java.io.Serializable;

/**
 * @author The Stajistics Project
 */
public interface UncaughtExceptionHandler extends Serializable {

    /**
     * 
     * @param key
     * @param e
     */
    void uncaughtException(StatsKey key, Throwable e);

}

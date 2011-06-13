package org.stajistics;


/**
 * @author The Stajistics Project
 */
public class NullUncaughtExceptionHandler implements UncaughtExceptionHandler {

    private static final NullUncaughtExceptionHandler instance = new NullUncaughtExceptionHandler();

    private NullUncaughtExceptionHandler() {}

    public static NullUncaughtExceptionHandler getInstance() {
        return instance;
    }

    @Override
    public void uncaughtException(StatsKey key, Throwable e) {}
}

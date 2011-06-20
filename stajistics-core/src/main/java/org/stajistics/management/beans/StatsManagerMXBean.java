package org.stajistics.management.beans;

import javax.management.MXBean;

/**
 * @author The Stajistics Project
 */
@MXBean
public interface StatsManagerMXBean {

    String getNamespace();

    String getSessionManagerImpl();

    String getConfigManagerImpl();

    String getEventManagerImpl();

    String getTaskServiceImpl();

    boolean getEnabled();

    void setEnabled(boolean enabled);

    void shutdown();
}

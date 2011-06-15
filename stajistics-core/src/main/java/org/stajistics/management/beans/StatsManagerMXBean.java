package org.stajistics.management.beans;

import javax.management.MXBean;

/**
 * @author The Stajistics Project
 */
@MXBean
public interface StatsManagerMXBean {

    boolean getEnabled();

    void setEnabled(boolean enabled);

    void shutdown();
}

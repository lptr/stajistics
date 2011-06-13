package org.stajistics.management;

import javax.management.MXBean;

/**
 * @author The Stajistics Project
 */
@MXBean
public interface StatsManagerMBean {

    boolean getEnabled();

    void setEnabled(boolean enabled);

    void shutdown();
}

package org.stajistics.management;

/**
 * This simply repeats the tests in {@link StatsManagementFactoryTest} except
 * it constructs the connection using {@link StatsManagementFactory#forURL(String)}.
 *
 * @author The Stajistics Project
 */
public class StatsManagementFactoryRemoteTest extends StatsManagementFactoryTest {

    @Override
    protected StatsManagementFactory createStatsManagementFactory() throws Exception {
        return StatsManagementFactory.forURL(getServiceURL());
    }

}

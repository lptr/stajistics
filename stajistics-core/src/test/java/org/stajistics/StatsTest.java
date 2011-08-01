package org.stajistics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.stajistics.DefaultStatsFactoryTest.ClassLoadableMockStatsManagerFactory;
import org.stajistics.bootstrap.StatsManagerFactory;

/**
 * 
 *
 * @author The Stajistics Project
 */
public class StatsTest extends AbstractStajisticsTestCase {

    @Test
    public void testLoadStatsManagerFactoryFromProperties() throws Exception {
        try {
            System.getProperties()
                  .setProperty(StatsManagerFactory.class.getName(),
                               ClassLoadableMockStatsManagerFactory.class.getName());

            StatsManagerFactory managerFactory = Stats.loadStatsManagerFactoryFromProperties();

            assertNotNull(managerFactory);
            assertInstanceOf(ClassLoadableMockStatsManagerFactory.class, managerFactory);

        } finally {
            System.getProperties()
                  .remove(StatsManagerFactory.class.getName());
        }
    }

    @Test(expected = ClassNotFoundException.class)
    public void testLoadInvalidStatsManagerFactoryFromProperties() throws Exception {
        try {
            System.getProperties()
                  .setProperty(StatsManagerFactory.class.getName(),
                               "org.stajistics.DoesntExistAtAllInAnyWayWhatSoEverSoThereHa");

            Stats.loadStatsManagerFactoryFromProperties();
            fail("Allowed loading of invalid " + StatsManagerFactory.class.getSimpleName() + " from properties");

        } finally {
            System.getProperties()
                  .remove(StatsManager.class.getName());
        }
    }
    
}

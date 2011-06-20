package org.stajistics.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;

/**
 * @author The Stajistics Project
 */
public class ServiceLifeCycleSupportTest extends AbstractStajisticsTestCase {

    private ServiceLifeCycle.Support support;

    @Before
    public void setUp() {
        support = new ServiceLifeCycle.Support();
    }

    @Test
    public void testInitialState() {
        assertFalse(support.isRunning());
    }

    @Test
    public void testInitialize() throws Exception {

        final Callable<Void> mockCallable = mockery.mock(Callable.class);

        mockery.checking(new Expectations() {{
            one(mockCallable).call();
        }});

        support.initialize(mockCallable);

        assertTrue(support.isRunning());

        // Try again to ensure no effect
        support.initialize(mockCallable);
    }

    @Test
    public void testInitializeWithNull() {
        support.initialize(null);
        assertTrue(support.isRunning());
    }

    @Test
    public void testShutdown() throws Exception {

        support.initialize(null);

        final Callable<Void> mockCallable = mockery.mock(Callable.class);

        mockery.checking(new Expectations() {{
            one(mockCallable).call();
        }});

        support.shutdown(mockCallable);

        assertFalse(support.isRunning());

        // Try again to ensure no effect
        support.shutdown(mockCallable);
    }

    @Test
    public void testShutdownWithNull() {
        support.initialize(null);
        support.shutdown(null);
        assertFalse(support.isRunning());
    }

    @Test(expected = IllegalStateException.class)
    public void testShutdownWithoutInitialize() {
        support.shutdown(null);
    }
}

package org.stajistics.session;

import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsConstants;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.bootstrap.DefaultStatsManagerFactory;
import org.stajistics.event.EventManager;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.task.TaskService;

/**
 * @author The Stajistics Project
 */
public class DefaultSessionFactoryTest extends AbstractStajisticsTestCase {

    @Before
    public void setUp() {
        new DefaultStatsManagerFactory().createManager(StatsConstants.DEFAULT_NAMESPACE);
    }
    
    @After
    public void tearDown() {
        System.getProperties().remove(DefaultSessionFactory.PROP_DEFAULT_SESSION_IMPL);
    }

    @Test
    public void testDefaultSessionImplIsConcurrent() {
        StatsSession session = prepareExpectationsAndCreateSession();
        assertTrue(session instanceof ConcurrentSession);
    }

    @Test
    public void testConcurrentSessionConfig() {
        System.setProperty(DefaultSessionFactory.PROP_DEFAULT_SESSION_IMPL, "concurrent");
        StatsSession session = prepareExpectationsAndCreateSession();
        assertTrue(session instanceof ConcurrentSession);
    }

    @Test
    public void testAsynchronousSessionConfig() {
        System.setProperty(DefaultSessionFactory.PROP_DEFAULT_SESSION_IMPL, "asynchronous");
        StatsSession session = prepareExpectationsAndCreateSession();
        assertTrue(session instanceof AsynchronousSession);
    }

    @Test
    public void testImmutableSessionConfig() {
        System.setProperty(DefaultSessionFactory.PROP_DEFAULT_SESSION_IMPL, "immutable");
        StatsSession session = prepareExpectationsAndCreateSession();
        assertTrue(session instanceof ImmutableSession);
    }

    private StatsSession prepareExpectationsAndCreateSession() {
        final StatsKey mockKey = mockery.mock(StatsKey.class);
        final StatsManager mockManager = mockery.mock(StatsManager.class);
        final EventManager mockEventManager = mockery.mock(EventManager.class);
        final TaskService mockTaskService = mockery.mock(TaskService.class);
        final DataRecorder[] dataRecorders = new DataRecorder[] {};

        mockery.checking(new Expectations() {{
            allowing(mockKey).getNamespace();
            will(returnValue(StatsConstants.DEFAULT_NAMESPACE));
            
            allowing(mockManager).getEventManager();
            will(returnValue(mockEventManager));

            allowing(mockManager).getTaskService();
            will(returnValue(mockTaskService));
        }});

        StatsSession session = DefaultSessionFactory.getInstance().createSession(mockKey, dataRecorders);
        return session;
    }
}

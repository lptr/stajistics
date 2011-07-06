/* Copyright 2009 - 2010 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stajistics.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.task.DebugTaskService;
import org.stajistics.task.TaskService;
import org.stajistics.task.TaskServiceFactory;

/**
 * @author The Stajistics Project
 */
public class AsynchronousSessionTest extends AbstractStatsSessionTestCase {

    @Before
    public void setUp() {
        TaskServiceFactory.getInstance().loadTaskService(new DebugTaskService());
    }

    @After
    public void tearDown() {
        TaskServiceFactory.getInstance().loadTaskService(null);
    }

    @Override
    protected StatsSession createStatsSession(final DataRecorder... dataRecorders) {
        return new AsynchronousSession(mockKey, mockEventManager, dataRecorders);
    }

    @Override
    @Test
    public void testConstructWithNullKey() {
        try {
            new AsynchronousSession(null, mockEventManager, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Override
    @Test
    public void testConstructWithNullEventManager() {
        try {
            new AsynchronousSession(mockKey, null, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullUpdateQueue() {
        try {
            new AsynchronousSession(mockKey, 
                                    mockEventManager, 
                                    null, 
                                    (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("updateQueue", npe.getMessage());
        }
    }

    @Test
    public void testTrackWithNastyTaskService() {
        TaskServiceFactory.getInstance().loadTaskService(new NastyTaskService());
        StatsSession service = new AsynchronousSession(mockKey,
                                                       mockEventManager);

        service.track(mockTracker, 1L);
    }

    @Test
    public void testUpdateWithNastyTaskService() {
        TaskServiceFactory.getInstance().loadTaskService(new NastyTaskService());
        StatsSession service = new AsynchronousSession(mockKey,
                                                       mockEventManager);

        service.update(mockTracker, 1L);
    }

    /* NESTED CLASSES */

    private static final class NastyTaskService implements TaskService {

        @Override
        public void execute(Class<?> source, Runnable task) {
            throw new RuntimeException();
        }


        @Override
        public void initialize() {
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void shutdown() {
            throw new RuntimeException();
        }

        @Override
        public <T> Future<T> submit(Class<?> source, Callable<T> task) {
            throw new RuntimeException();
        }
    }

}

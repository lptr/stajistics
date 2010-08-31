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

import org.junit.Test;
import org.stajistics.session.recorder.DataRecorder;
import org.stajistics.task.DebugTaskService;
import org.stajistics.task.TaskService;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author The Stajistics Project
 */
public class AsynchronousSessionTest extends AbstractStatsSessionTestCase {

    private TaskService mockTaskService;

    @Override
    protected void initMocks() {
        mockTaskService = new DebugTaskService();
    }

    @Override
    protected StatsSession createStatsSession(final DataRecorder... dataRecorders) {
        return new AsynchronousSession(mockKey, mockEventManager, mockTaskService, mockFieldSetFactory, dataRecorders);
    }

    @Override
    @Test
    public void testConstructWithNullKey() {
        try {
            new AsynchronousSession(null, mockEventManager, mockTaskService, mockFieldSetFactory, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Override
    @Test
    public void testConstructWithNullEventManager() {
        try {
            new AsynchronousSession(mockKey, null, mockTaskService, mockFieldSetFactory, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }

    @Override
    @Test
    public void testConstructWithNullFieldSetFactory() {
        try {
            new AsynchronousSession(mockKey, mockEventManager, mockTaskService, null, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("fieldSetFactory", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullTaskService() {
        try {
            new AsynchronousSession(mockKey, mockEventManager, null, mockFieldSetFactory, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("taskService", npe.getMessage());
        }
    }

    @Test
    public void testConstructWithNullUpdateQueue() {
        try {
            new AsynchronousSession(mockKey, mockEventManager, mockTaskService, null, mockFieldSetFactory, (DataRecorder[])null);
            fail();
        } catch (NullPointerException npe) {
            assertEquals("updateQueue", npe.getMessage());
        }
    }

    @Test
    public void testTrackWithNastyTaskService() {
        StatsSession service = new AsynchronousSession(mockKey,
                                                            mockEventManager,
                                                            new NastyTaskService(),
                                                            mockFieldSetFactory);

        service.track(mockTracker, 1L);
    }

    @Test
    public void testUpdateWithNastyTaskService() {
        StatsSession service = new AsynchronousSession(mockKey,
                                                            mockEventManager,
                                                            new NastyTaskService(),
                                                            mockFieldSetFactory);

        service.update(mockTracker, 1L);
    }

    /* NESTED CLASSES */

    private static final class NastyTaskService implements TaskService {

        @Override
        public void execute(Class<?> source, Runnable task) {
            throw new RuntimeException();
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

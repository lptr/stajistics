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

import org.jmock.Expectations;
import org.junit.Test;
import org.stajistics.session.recorder.DataRecorder;

/**
 *
 * @author The Stajistics Project
 *
 */
public class ConcurrentSessionTest extends AbstractStatsSessionTestCase {

    @Override
    protected StatsSession createStatsSession(final DataRecorder... dataRecorders) {
        return new ConcurrentSession(mockKey, mockEventManager, dataRecorders);
    }

    @Override
    @Test
    public void testConstructWithNullKey() {
        try {
            new ConcurrentSession(null, mockEventManager, new DataRecorder[0]);

        } catch (NullPointerException npe) {
            assertEquals("key", npe.getMessage());
        }
    }

    @Override
    @Test
    public void testConstructWithNullEventManager() {
        try {
            new ConcurrentSession(mockKey, null, new DataRecorder[0]);

        } catch (NullPointerException npe) {
            assertEquals("eventManager", npe.getMessage());
        }
    }
}

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
package org.stajistics;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public abstract class AbstractStajisticsTestCase {

    protected Mockery mockery;

    @Before
    public void createMockery() {
        resetMockery();
    }

    protected void resetMockery() {
        mockery = new Mockery();
    }

    @After
    public void resetStatsManager() {
        // Ensure we're working with a clean slate for each test
        Stats.loadManager(DefaultStatsManager.createWithDefaults());
    }
}

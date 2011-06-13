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
package org.stajistics.management;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsManager;
import org.stajistics.configuration.StatsConfigBuilder;

import javax.management.ObjectName;

import static org.junit.Assert.assertTrue;

/**
 * @author The Stajistics Project
 */
public class DefaultStatsManagerMBeanTest extends AbstractMBeanTestCase {

    protected StatsManager mockManager = null;

    @Before
    public void setUp() {
        mockManager = mockery.mock(StatsManager.class);
    }

    @Test
    public void testGetEnabled() throws Exception {
        mockery.checking(new Expectations() {{
            one(mockManager).isEnabled(); will(returnValue(true));
        }});

        StatsManagerMBean mBean = new DefaultStatsManagerMBean(mockManager);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsManagerMBean.class);

        assertTrue(mBean.getEnabled());
    }

    @Test
    public void testSetEnabled() throws Exception {
        mockery.checking(new Expectations() {{
            one(mockManager).setEnabled(false);
            one(mockManager).setEnabled(true);
        }});

        StatsManagerMBean mBean = new DefaultStatsManagerMBean(mockManager);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsManagerMBean.class);

        mBean.setEnabled(false);
        mBean.setEnabled(true);
    }

}

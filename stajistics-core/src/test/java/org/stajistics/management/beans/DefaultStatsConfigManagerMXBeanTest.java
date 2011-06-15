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
package org.stajistics.management.beans;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.management.AbstractMXBeanTestCase;
import org.stajistics.management.beans.DefaultStatsConfigManagerMXBean;
import org.stajistics.management.beans.StatsConfigManagerMXBean;

import javax.management.ObjectName;

import static org.junit.Assert.assertEquals;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigManagerMXBeanTest extends AbstractMXBeanTestCase {

    protected StatsConfigManager mockConfigManager;

    @Before
    public void setUp() {
        mockConfigManager = mockery.mock(StatsConfigManager.class);
    }

    protected StatsConfigManagerMXBean createConfigManagerMBean(final StatsConfigManager configManager) {
        return new DefaultStatsConfigManagerMXBean(configManager);
    }

    @Test
    public void testGetImplementation() throws Exception {

        StatsConfigManagerMXBean mBean = createConfigManagerMBean(mockConfigManager);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigManagerMXBean.class);

        assertEquals(mockConfigManager.getClass().getName(),
                     mBean.getImplementation());
    }

    @Test
    public void testGetConfigCount() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfigManager).getConfigCount(); will(returnValue(42));
        }});

        StatsConfigManagerMXBean mBean = createConfigManagerMBean(mockConfigManager);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigManagerMXBean.class);

        assertEquals(42, mBean.getConfigCount());
    }
}

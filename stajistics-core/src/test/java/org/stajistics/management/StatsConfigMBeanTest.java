/* Copyright 2009 The Stajistics Project
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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsConfigMBeanTest extends AbstractMBeanTestCase {

    protected Mockery mockery = null;
    protected org.stajistics.StatsConfig mockConfig = null;

    protected StatsConfigMBean createStatsConfigMBean(final org.stajistics.StatsConfig config) {
        return new StatsConfig(config);
    }

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockConfig = mockery.mock(org.stajistics.StatsConfig.class);
    }

    @Test
    public void testIsEnabled() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfig).isEnabled(); will(returnValue(true));
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMBean.class);

        assertTrue(mBean.getEnabled());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetUnit() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfig).getUnit(); will(returnValue("aUnit"));
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMBean.class);

        assertEquals("aUnit", mBean.getUnit());

        mockery.assertIsSatisfied();
    }

    @Test
    public void testGetDescription() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfig).getDescription(); will(returnValue("aDescription"));
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMBean.class);

        assertEquals("aDescription", mBean.getDescription());

        mockery.assertIsSatisfied();
    }

}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigBuilder;
import org.stajistics.StatsConfigFactory;
import org.stajistics.StatsConfigManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigMBeanTest extends AbstractMBeanTestCase {

    protected StatsKey mockKey = null;
    protected StatsManager mockManager = null;
    protected StatsConfigFactory mockConfigFactory = null;
    protected StatsConfigManager mockConfigManager = null;
    protected StatsConfig mockConfig = null;

    protected DefaultStatsConfigMBean createStatsConfigMBean(final StatsConfig config) {
        return new DefaultStatsConfigMBean(mockManager, mockKey, config);
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        mockKey = mockery.mock(StatsKey.class);
        mockManager = mockery.mock(StatsManager.class);
        mockConfigFactory = mockery.mock(StatsConfigFactory.class);
        mockConfigManager = mockery.mock(StatsConfigManager.class);
        mockConfig = mockery.mock(StatsConfig.class);

        mockery.checking(new Expectations() {{
            allowing(mockManager).getConfigFactory(); will(returnValue(mockConfigFactory));
            allowing(mockManager).getConfigManager(); will(returnValue(mockConfigManager));
        }});
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
    }

    @Test
    public void testSetEnabled() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).isEnabled(); will(returnValue(true));
            one(mockConfigFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withEnabledState(with(false)); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMBean.class);

        mBean.setEnabled(false);
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
    }

    @Test
    public void testSetUnit() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).getUnit(); will(returnValue("unit1"));
            one(mockConfigFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withUnit(with("unit2")); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMBean.class);

        mBean.setUnit("unit2");
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
    }

    @Test
    public void testSetDescription() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).getDescription(); will(returnValue("d1"));
            one(mockConfigFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withDescription(with("d2")); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMBean.class);

        mBean.setDescription("d2");
    }

}

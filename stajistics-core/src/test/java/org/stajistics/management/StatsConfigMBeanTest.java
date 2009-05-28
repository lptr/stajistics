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
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsConfig;
import org.stajistics.StatsConfigBuilder;
import org.stajistics.StatsConfigFactory;
import org.stajistics.StatsKey;

/**
 * 
 * 
 *
 * @author The Stajistics Project
 */
public class StatsConfigMBeanTest extends AbstractMBeanTestCase {

    protected Mockery mockery = null;
    protected StatsKey mockKey = null;
    protected StatsConfigFactory mockConfigFactory = null;
    protected StatsConfig mockConfig = null;

    protected StatsConfigMBean createStatsConfigMBean(final StatsConfig config) {
        return new DefaultStatsConfigMBean(mockConfigFactory, mockKey, config);
    }

    @Before
    public void setUp() {
        mockery = new Mockery();
        mockKey = mockery.mock(StatsKey.class);
        mockConfigFactory = mockery.mock(StatsConfigFactory.class);
        mockConfig = mockery.mock(StatsConfig.class);
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
    public void testSetEnabled() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).isEnabled(); will(returnValue(true));
            one(mockConfigFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withEnabledState(with(false)); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        mBean.setEnabled(false);

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
    public void testSetUnit() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).getUnit(); will(returnValue("unit1"));
            one(mockConfigFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withUnit(with("unit2")); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMBean mBean = createStatsConfigMBean(mockConfig);
        mBean.setUnit("unit2");

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
        mBean.setDescription("d2");

        mockery.assertIsSatisfied();
    }

}

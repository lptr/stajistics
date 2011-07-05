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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.StatsManagerRegistry;
import org.stajistics.configuration.StatsConfig;
import org.stajistics.configuration.StatsConfigBuilder;
import org.stajistics.configuration.StatsConfigBuilderFactory;
import org.stajistics.configuration.StatsConfigManager;
import org.stajistics.management.AbstractJMXTestCase;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsConfigMXBeanTest extends AbstractJMXTestCase {

    private static final String NAMESPACE = "ns";
    
    protected StatsKey mockKey = null;
    protected StatsManager mockManager = null;
    protected StatsConfigBuilderFactory mockConfigBuilderFactory = null;
    protected StatsConfigManager mockConfigManager = null;
    protected StatsConfig mockConfig = null;

    protected DefaultStatsConfigMXBean createStatsConfigMBean(final StatsConfig config) {
        return new DefaultStatsConfigMXBean(NAMESPACE, mockKey, config);
    }

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class);
        mockManager = mockery.mock(StatsManager.class);
        mockConfigBuilderFactory = mockery.mock(StatsConfigBuilderFactory.class);
        mockConfigManager = mockery.mock(StatsConfigManager.class);
        mockConfig = mockery.mock(StatsConfig.class);

        mockery.checking(new Expectations() {{
            allowing(mockManager).getNamespace();
            will(returnValue(NAMESPACE));
            allowing(mockManager).getConfigBuilderFactory();
            will(returnValue(mockConfigBuilderFactory));
            allowing(mockManager).getConfigManager();
            will(returnValue(mockConfigManager));
        }});

        StatsManagerRegistry.getInstance().registerStatsManager(mockManager);
    }

    @After
    public void tearDown() {
        StatsManagerRegistry.getInstance().removeStatsManager(mockManager);
    }
    
    @Test
    public void testGetEnabled() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfig).isEnabled(); will(returnValue(true));
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        assertTrue(mBean.getEnabled());
    }

    @Test
    public void testSetEnabled() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).isEnabled(); will(returnValue(true));
            one(mockConfigBuilderFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withEnabledState(with(false)); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        mBean.setEnabled(false);
    }

    @Test
    public void testSetEnabledWithNoChange() throws Exception {
        mockery.checking(new Expectations() {{
            one(mockConfig).isEnabled(); will(returnValue(true));
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        mBean.setEnabled(true);
    }

    @Test
    public void testGetUnit() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfig).getUnit(); will(returnValue("aUnit"));
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        assertEquals("aUnit", mBean.getUnit());
    }

    @Test
    public void testSetUnit() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).getUnit(); will(returnValue("unit1"));
            one(mockConfigBuilderFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withUnit(with("unit2")); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        mBean.setUnit("unit2");
    }

    @Test
    public void testSetUnitWithNoChange() throws Exception {
        mockery.checking(new Expectations() {{
            one(mockConfig).getUnit(); will(returnValue("unit1"));
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        mBean.setUnit("unit1");
    }

    @Test
    public void testGetDescription() throws Exception {

        mockery.checking(new Expectations() {{
            one(mockConfig).getDescription(); will(returnValue("aDescription"));
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        assertEquals("aDescription", mBean.getDescription());
    }

    @Test
    public void testSetDescription() throws Exception {
        final StatsConfigBuilder configBuilder = mockery.mock(StatsConfigBuilder.class);

        mockery.checking(new Expectations() {{
            one(mockConfig).getDescription(); will(returnValue("d1"));
            one(mockConfigBuilderFactory).createConfigBuilder(mockConfig); will(returnValue(configBuilder));
            one(configBuilder).withDescription(with("d2")); will(returnValue(configBuilder));
            one(configBuilder).setConfigFor(mockKey);
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        mBean.setDescription("d2");
    }

    @Test
    public void testSetDescriptionWithNoChange() throws Exception {
        mockery.checking(new Expectations() {{
            one(mockConfig).getDescription(); will(returnValue("d1"));
        }});

        StatsConfigMXBean mBean = createStatsConfigMBean(mockConfig);
        ObjectName name = new ObjectName(getClass().getName() + ":name=test");

        mBean = registerMBean(mBean, name, StatsConfigMXBean.class);

        mBean.setDescription("d1");
    }

}

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

import javax.management.ObjectName;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.management.AbstractJMXTestCase;
import org.stajistics.session.StatsSessionManager;

/**
 *
 *
 *
 * @author The Stajistics Project
 */
public class DefaultStatsSessionManagerMXBeanTest extends AbstractJMXTestCase {

     protected StatsSessionManager mockSessionManager;

     @Before
     public void setUp() {
         mockSessionManager = mockery.mock(StatsSessionManager.class);
     }

     protected DefaultStatsSessionManagerMXBean createSessionManagerMBean(final StatsSessionManager sessionManager) {
         return new DefaultStatsSessionManagerMXBean(sessionManager);
     }

     @Test
     public void testGetImplementation() throws Exception {

         StatsSessionManagerMXBean mBean = createSessionManagerMBean(mockSessionManager);
         ObjectName name = new ObjectName(getClass().getName() + ":name=test");

         mBean = registerMBean(mBean, name, StatsSessionManagerMXBean.class);

         assertEquals(mockSessionManager.getClass().getName(),
                      mBean.getImplementation());
     }


     @Test
     public void testGetSessionCount() throws Exception {

         mockery.checking(new Expectations() {{
             one(mockSessionManager).getSessionCount(); will(returnValue(42));
         }});

         StatsSessionManagerMXBean mBean = createSessionManagerMBean(mockSessionManager);
         //ObjectName name = new ObjectName(getClass().getName() + ":name=test");

         // Don't register because it screws up on serialization of Mockery.. what to do?
         //mBean = registerMBean(mBean, name, StatsSessionManagerMXBean.class);

         assertEquals(42, mBean.getSessionCount());

         mockery.assertIsSatisfied();
     }

}

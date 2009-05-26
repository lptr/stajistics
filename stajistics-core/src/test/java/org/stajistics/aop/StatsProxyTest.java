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
package org.stajistics.aop;

import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.Stats;
import org.stajistics.StatsKey;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.StatsTrackerFactory;

/**
 * 
 * @author The Stajistics Project
 */
public class StatsProxyTest {

    private static Method SERVICE_FAIL_METHOD;
    static {
        try {
            SERVICE_FAIL_METHOD = Service2.class.getMethod("fail", (Class[])null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Mockery mockery;
    private StatsKey key;

    @Before
    public void setUp() {
        mockery = new Mockery();
        key = Stats.newKey("test");
    }

    @After
    public void tearDown() {
        Stats.getConfigManager().clearConfigs();
    }

    @Test
    public void testMethodDelegation() {
        final Service service = mockery.mock(Service.class);

        mockery.checking(new Expectations() {{
            one(service).query();
        }});

        Service serviceProxy = StatsProxy.wrap(key, service);
        serviceProxy.query();

        mockery.assertIsSatisfied();
    }

    @Test
    public void testWrapWithKeyTarget() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(key, serviceImpl);
        @SuppressWarnings("unused")
        Service2 service2 = (Service2)serviceImpl;
    }

    @Test
    public void testWrapWithKeyTargetInterface() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(key, serviceImpl, Service.class);
        try {
            @SuppressWarnings("unused")
            Service2 service2 = (Service2)serviceImpl;
            fail("Allowed cast to un-proxied interface");
        } catch (ClassCastException cce) {
            // expected
        }
    }
    
    @Test
    public void testWrapWithKeyTargetInterfaceArray() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(key, 
                                      serviceImpl, 
                                      new Class<?>[] { Service.class, Service2.class });
        @SuppressWarnings("unused")
        Service2 service2 = (Service2)serviceImpl;
    }

    @SuppressWarnings("serial")
    @Test
    public void testTrackMethodCall() {

        StatsKey methodKey = key.buildCopy()
                                .withAttribute("method", StatsProxy.getMethodString(SERVICE_FAIL_METHOD))
                                .newKey();

        final StatsTracker mockTracker = mockery.mock(StatsTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTracker).track(); will(returnValue(mockTracker));
            one(mockTracker).commit(); will(returnValue(mockTracker));
        }});

        Stats.buildConfig()
             .withTrackerFactory(new StatsTrackerFactory() {
                @Override
                public StatsTracker createTracker(final StatsKey key,
                                                  final StatsSessionManager sessionManager) {

                    return mockTracker;
                }
             })
             .setConfigFor(methodKey);

        Service serviceImpl = StatsProxy.wrap(key, new ServiceImpl()); 

        serviceImpl.query();

        mockery.assertIsSatisfied();
    }

    @SuppressWarnings("serial")
    @Test
    public void testTrackExceptionIncident() {

        final StatsKey methodKey = key.buildCopy()
                                      .withAttribute("method", StatsProxy.getMethodString(SERVICE_FAIL_METHOD))
                                      .newKey();
        final StatsKey exceptionKey = methodKey.buildCopy()
                                               .withAttribute("threw", IllegalStateException.class.getName())
                                               .newKey();

        final StatsTracker methodTracker = mockery.mock(StatsTracker.class, "methodTracker");
        final StatsTracker exceptionTracker = mockery.mock(StatsTracker.class, "exceptionTracker");

        mockery.checking(new Expectations() {{
            one(methodTracker).track(); will(returnValue(methodTracker));
            one(exceptionTracker).track(); will(returnValue(exceptionTracker));
            one(exceptionTracker).commit(); will(returnValue(exceptionTracker));
            one(methodTracker).commit(); will(returnValue(methodTracker));
        }});

        Stats.buildConfig()
             .withTrackerFactory(new StatsTrackerFactory() {
                 @Override
                 public StatsTracker createTracker(final StatsKey key,
                                                   final StatsSessionManager sessionManager) {
                     if (key.equals(methodKey)) {
                         return methodTracker;
                     }

                     if (key.equals(exceptionKey)) {
                         return exceptionTracker;
                     }

                     throw new Error();
                 }
             })
             .setConfigFor(methodKey);

        Service2 serviceImpl = StatsProxy.wrap(key, new ServiceImpl()); 

        try {
            serviceImpl.fail();
            fail("Exception thrown from proxied method was swallowed");
        } catch (IllegalStateException ise) {
            // expected
        }

        mockery.assertIsSatisfied();
    }

    private interface Service {
        void query();
    }

    private interface Service2 {
        void fail();
    }

    private static class ServiceImpl implements Service,Service2 {
        @Override
        public void query() {}

        @Override
        public void fail() {
            throw new IllegalStateException();
        }
    }
}

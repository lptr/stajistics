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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyUtils;
import org.stajistics.StatsManager;
import org.stajistics.session.StatsSessionManager;
import org.stajistics.tracker.StatsTracker;
import org.stajistics.tracker.StatsTrackerFactory;
import org.stajistics.tracker.incident.IncidentTracker;
import org.stajistics.tracker.span.SpanTracker;

/**
 * 
 * @author The Stajistics Project
 */
@RunWith(JMock.class)
public class StatsProxyTest {

    private static Method SERVICE_QUERY_METHOD;
    static {
        try {
            SERVICE_QUERY_METHOD = Service.class.getMethod("query", (Class[])null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Mockery mockery;
    private StatsManager mockStatsManager;
    private StatsKey mockKey;
    private Service mockService;

    @Before
    public void setUp() {
        mockery = new Mockery();

        // TODO: these should be _actually_ mocked
        mockStatsManager = DefaultStatsManager.createWithDefaults();
        mockKey = mockStatsManager.getKeyFactory().createKey("test");

        mockService = mockery.mock(Service.class);
    }

    @Test
    public void testMethodDelegation() {
        mockery.checking(new Expectations() {{
            one(mockService).query();
        }});

        Service serviceProxy = StatsProxy.wrap(mockStatsManager, mockKey, mockService);
        serviceProxy.query();
    }

    @Test
    public void testWrapWithKeyTarget() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(mockStatsManager, mockKey, serviceImpl);
        @SuppressWarnings("unused")
        Service2 service2 = (Service2)serviceImpl;
    }

    @Test
    public void testWrapWithKeyTargetInterface() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(mockStatsManager, mockKey, serviceImpl, Service.class);
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
        serviceImpl = StatsProxy.wrap(mockStatsManager, 
                                      mockKey, 
                                      serviceImpl, 
                                      new Class<?>[] { Service.class, Service2.class });
        @SuppressWarnings("unused")
        Service2 service2 = (Service2)serviceImpl;
    }

    @SuppressWarnings("serial")
    @Test
    public void testTrackMethodCall() {

        StatsKey methodKey = mockKey.buildCopy()
                                .withAttribute("method", StatsProxy.getMethodString(SERVICE_QUERY_METHOD))
                                .newKey();

        final SpanTracker mockTracker = mockery.mock(SpanTracker.class);

        mockery.checking(new Expectations() {{
            one(mockTracker).start(); will(returnValue(mockTracker));
            one(mockTracker).stop(); will(returnValue(mockTracker));
        }});

        mockStatsManager.getConfigFactory()
                        .createConfigBuilder()
                        .withTrackerFactory(new StatsTrackerFactory<StatsTracker>() {
            @Override
            public StatsTracker createTracker(final StatsKey key,
                                              final StatsSessionManager sessionManager) {

                return mockTracker;
            }
         })
         .setConfigFor(methodKey);

        mockery.checking(new Expectations() {{
            one(mockService).query();
        }});

        Service serviceProxy = StatsProxy.wrap(mockStatsManager, mockKey, mockService); 

        serviceProxy.query();
    }

    @SuppressWarnings("serial")
    @Test
    public void testTrackExceptionIncident() {

        final StatsKey methodKey = mockKey.buildCopy()
                                      .withAttribute("method", StatsProxy.getMethodString(SERVICE_QUERY_METHOD))
                                      .newKey();
        final StatsKey exceptionKey = StatsKeyUtils.keyForFailure(methodKey,
                                                                  new IllegalStateException());

        final SpanTracker methodTracker = mockery.mock(SpanTracker.class, "methodTracker");
        final IncidentTracker exceptionTracker = mockery.mock(IncidentTracker.class, "exceptionTracker");

        mockery.checking(new Expectations() {{
            one(methodTracker).start(); will(returnValue(methodTracker));
            one(exceptionTracker).incident(); will(returnValue(exceptionTracker));
            one(methodTracker).stop(); will(returnValue(methodTracker));
        }});

        mockStatsManager.getConfigFactory()
                        .createConfigBuilder()
                        .withTrackerFactory(new StatsTrackerFactory<StatsTracker>() {
             @Override
             public StatsTracker createTracker(final StatsKey key,
                                               final StatsSessionManager sessionManager) {
                 if (key.equals(methodKey)) {
                     return methodTracker;
                 }

                 if (key.equals(exceptionKey)) {
                     return exceptionTracker;
                 }

                 throw new Error("key is neither the methodKey nor the exceptionKey");
             }
         })
         .setConfigFor(methodKey);

        final IllegalStateException exception = new IllegalStateException();

        mockery.checking(new Expectations() {{
            one(mockService).query(); will(throwException(exception));
        }});

        Service serviceProxy = StatsProxy.wrap(mockStatsManager, mockKey, mockService); 

        try {
            serviceProxy.query();
            fail("Exception thrown from proxied method was swallowed");
        } catch (IllegalStateException ise) {
            // expected
            assertEquals(exception, ise);
        }
    }

    @Test
    public void testProxyEqualsProxy() {
        Service proxy1 = StatsProxy.wrap(mockStatsManager, mockKey, mockService);
        Service proxy2 = StatsProxy.wrap(mockStatsManager, mockKey, mockService);
        assertEquals(proxy1, proxy2);
    }

    @Test
    public void testProxyEqualsNonProxy() {
        Service proxy = StatsProxy.wrap(mockStatsManager, mockKey, mockService);
        assertEquals(proxy, mockService); // yes, this is the correct oder for this test
    }

    @Test
    public void testNonProxyEqualsProxy() {
        Service proxy = StatsProxy.wrap(mockStatsManager, mockKey, mockService);
        assertFalse(mockService.equals(proxy));
    }

    @Test
    public void testUnwrapProxy() {
        Service proxy = StatsProxy.wrap(mockStatsManager, mockKey, mockService);
        Service unwrappedServiceImpl = StatsProxy.unwrap(proxy);
        assertSame(mockService, unwrappedServiceImpl);
    }

    @Test
    public void testUnwrapNonProxy() {
        Service unwrappedServiceImpl = StatsProxy.unwrap(mockService);
        assertSame(mockService, unwrappedServiceImpl);
    }

    private interface Service {
        void query();
    }

    private interface Service2 {}

    private static class ServiceImpl implements Service, Service2 {
        @Override
        public void query() {}
    }
}

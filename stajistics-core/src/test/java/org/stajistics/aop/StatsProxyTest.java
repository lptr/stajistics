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
package org.stajistics.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.stajistics.AbstractStajisticsTestCase;
import org.stajistics.StatsFactory;
import org.stajistics.StatsKey;
import org.stajistics.StatsKeyBuilder;
import org.stajistics.TestUtil;
import org.stajistics.tracker.span.SpanTracker;

/**
 *
 * @author The Stajistics Project
 */
public class StatsProxyTest extends AbstractStajisticsTestCase {

    private static Method SERVICE_QUERY_METHOD;
    static {
        try {
            SERVICE_QUERY_METHOD = Service.class.getMethod("query", (Class[])null);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private StatsFactory mockFactory;
    private StatsKey mockKey;
    private StatsKeyBuilder mockKeyBuilder;
    private Service mockService;

    @Before
    public void setUp() {
        mockKey = mockery.mock(StatsKey.class, "key1");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey, "key1");
        mockKeyBuilder = mockery.mock(StatsKeyBuilder.class);

        mockFactory = mockery.mock(StatsFactory.class);

        mockService = mockery.mock(Service.class);
    }

    private StatsKey expectKeyForMethod(final String methodName) {
        final StatsKey mockKey2 = mockery.mock(StatsKey.class, "key2");
        TestUtil.buildStatsKeyExpectations(mockery, mockKey2, "key2");

        mockery.checking(new Expectations() {{
            allowing(mockKey).buildCopy();
            will(returnValue(mockKeyBuilder));

            one(mockKeyBuilder).withAttribute("method", methodName);
            will(returnValue(mockKeyBuilder));
            
            one(mockKeyBuilder).newKey();
            will(returnValue(mockKey2));
        }});

        return mockKey2;
    }

    @Test
    public void testMethodDelegation() {
        final StatsKey mockKey2 = expectKeyForMethod("query");

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey2);
            one(mockService).query();
        }});

        Service serviceProxy = StatsProxy.wrap(mockFactory, mockKey, mockService);
        serviceProxy.query();
    }

    @Test
    public void testWrapWithKeyTarget() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(mockFactory, mockKey, serviceImpl);
        assertInstanceOf(Service2.class, serviceImpl);
    }

    @Test
    public void testWrapWithKeyTargetInterface() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(mockFactory, mockKey, serviceImpl, Service.class);
        assertNotInstanceOf(Service2.class, serviceImpl);
    }

    @Test
    public void testWrapWithKeyTargetInterfaceArray() {
        Service serviceImpl = new ServiceImpl();
        serviceImpl = StatsProxy.wrap(mockFactory,
                                      mockKey,
                                      serviceImpl,
                                      new Class<?>[] { Service.class, Service2.class });
        assertInstanceOf(Service2.class, serviceImpl);
    }

    @Test
    public void testTrackMethodCall() {
        final StatsKey mockKey2 = expectKeyForMethod(StatsProxy.getMethodString(SERVICE_QUERY_METHOD));

        final SpanTracker mockTracker = mockery.mock(SpanTracker.class);

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey2);
            will(returnValue(mockTracker));

            one(mockService).query();

            one(mockTracker).commit();
        }});

        Service serviceProxy = StatsProxy.wrap(mockFactory, mockKey, mockService);

        serviceProxy.query();
    }

    @Test
    public void testTrackExceptionIncident() {
        final StatsKey mockKey2 = expectKeyForMethod(StatsProxy.getMethodString(SERVICE_QUERY_METHOD));
        final SpanTracker methodTracker = mockery.mock(SpanTracker.class, "methodTracker");

        final IllegalStateException exception = new IllegalStateException();

        mockery.checking(new Expectations() {{
            one(mockFactory).track(mockKey2);
            will(returnValue(methodTracker));

            one(mockService).query();
            will(throwException(exception));

            one(methodTracker).commit();

            one(mockFactory).failure(exception, mockKey2);
        }});

        Service serviceProxy = StatsProxy.wrap(mockFactory, mockKey, mockService);

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
        Service proxy1 = StatsProxy.wrap(mockFactory, mockKey, mockService);
        Service proxy2 = StatsProxy.wrap(mockFactory, mockKey, mockService);
        assertEquals(proxy1, proxy2);
    }

    @Test
    public void testProxyEqualsNonProxy() {
        Service proxy = StatsProxy.wrap(mockFactory, mockKey, mockService);
        assertEquals(proxy, mockService); // yes, this is the correct oder for this test
    }

    @Test
    public void testNonProxyEqualsProxy() {
        Service proxy = StatsProxy.wrap(mockFactory, mockKey, mockService);
        assertFalse(mockService.equals(proxy));
    }

    @Test
    public void testUnwrapProxy() {
        Service proxy = StatsProxy.wrap(mockFactory, mockKey, mockService);
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

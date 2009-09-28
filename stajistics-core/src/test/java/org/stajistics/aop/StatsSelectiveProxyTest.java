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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stajistics.DefaultStatsManager;
import org.stajistics.StatsKey;
import org.stajistics.StatsManager;
import org.stajistics.aop.StatsSelectiveProxy.EnabledCriteria;
import org.stajistics.aop.StatsSelectiveProxy.MethodCriteria;
import org.stajistics.aop.StatsSelectiveProxy.MethodModifierCriteria;
import org.stajistics.aop.StatsSelectiveProxy.MethodSetCriteria;
import org.stajistics.aop.StatsSelectiveProxy.SelectionCriteria;

/**
 * TODO: run all StatsProxy tests against StatsSelectiveProxy
 *
 * @author The Stajistics Project
 *
 */
@RunWith(JMock.class)
public class StatsSelectiveProxyTest {

    private Mockery mockery;
    private StatsManager mockStatsManager;
    private StatsKey mockKey;
    private Service mockService;

    private static final Method PRIVATE_METHOD;
    private static final Method PRIVATE_STATIC_METHOD;
    private static final Method PROTECTED_METHOD;
    private static final Method PROTECTED_STATIC_METHOD;
    private static final Method PUBLIC_METHOD;

    static {
        try {
            Class<StatsSelectiveProxyTest> cls = StatsSelectiveProxyTest.class;

            PRIVATE_METHOD = cls.getDeclaredMethod("privateTestMethod", null);
            PRIVATE_STATIC_METHOD = cls.getDeclaredMethod("privateStaticTestMethod", null);
            PROTECTED_METHOD = cls.getDeclaredMethod("protectedTestMethod", null);
            PROTECTED_STATIC_METHOD = cls.getDeclaredMethod("protectedStaticTestMethod", null);
            PUBLIC_METHOD = cls.getDeclaredMethod("publicTestMethod", null);

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final Method METHOD1 = PRIVATE_METHOD;;
    private static final Method METHOD2 = PROTECTED_METHOD;
    private static final Method METHOD3 = PUBLIC_METHOD;

    @Before
    public void setUp() {
        mockery = new Mockery();

        // TODO: these should be _actually_ mocked
        mockStatsManager = DefaultStatsManager.createWithDefaults();
        mockKey = mockStatsManager.getKeyFactory().createKey("test");

        mockService = mockery.mock(Service.class);
    }

    @Test
    public void testInvokeWithPassingCriteria() {

        final SelectionCriteria mockCriteria = mockery.mock(SelectionCriteria.class);

        mockery.checking(new Expectations() {{
            one(mockCriteria).select(with(aNonNull(Method.class)), 
                                     with(any(Object[].class)));
            will(returnValue(true));

            one(mockService).query();
        }});

        Service serviceProxy = StatsSelectiveProxy.wrap(mockStatsManager, 
                                                        mockKey, 
                                                        mockService, 
                                                        mockCriteria);

        serviceProxy.query();
    }

    @Test
    public void testInvokeWithFailingCriteria() {

        final SelectionCriteria mockCriteria = mockery.mock(SelectionCriteria.class);

        mockery.checking(new Expectations() {{
            one(mockCriteria).select(with(aNonNull(Method.class)), 
                                 with(any(Object[].class)));
            will(returnValue(false));

            one(mockService).query();
        }});

        Service serviceProxy = StatsSelectiveProxy.wrap(mockStatsManager, 
                                                        mockKey, 
                                                        mockService, 
                                                        mockCriteria);

        serviceProxy.query();
    }

    @Test
    public void testEnabledCriteria() throws Exception {
        EnabledCriteria criteria = new EnabledCriteria(true);

        assertTrue(criteria.select(METHOD1, null));
        criteria.setEnabled(false);
        assertFalse(criteria.select(METHOD1, null));
        criteria.setEnabled(true);
        assertTrue(criteria.select(METHOD1, null));
    }

    @Test
    public void testMethodCriteriaInclude() {
        MethodCriteria criteria = new MethodCriteria(METHOD1, true);
        assertTrue(criteria.select(METHOD1, null));
        assertFalse(criteria.select(METHOD2, null));
    }

    @Test
    public void testMethodCriteriaExclude() {
        MethodCriteria criteria = new MethodCriteria(METHOD1, false);
        assertFalse(criteria.select(METHOD1, null));
        assertTrue(criteria.select(METHOD2, null));
    }

    @Test
    public void testMethodSetCriteriaInclude() {
        Set<Method> methodSet = new HashSet<Method>();
        methodSet.add(METHOD1);
        methodSet.add(METHOD2);

        MethodSetCriteria criteria = new MethodSetCriteria(methodSet, true);

        assertTrue(criteria.select(METHOD1, null));
        assertTrue(criteria.select(METHOD2, null));
        assertFalse(criteria.select(METHOD3, null));
    }

    @Test
    public void testMethodSetCriteriaExclude() {
        Set<Method> methodSet = new HashSet<Method>();
        methodSet.add(METHOD1);
        methodSet.add(METHOD2);

        MethodSetCriteria criteria = new MethodSetCriteria(methodSet, false);

        assertFalse(criteria.select(METHOD1, null));
        assertFalse(criteria.select(METHOD2, null));
        assertTrue(criteria.select(METHOD3, null));
    }

    @Test
    public void testMethodModifierCriteriaAny() {

        int modifierMask = Modifier.PROTECTED | Modifier.STATIC;

        MethodModifierCriteria criteria = new MethodModifierCriteria(modifierMask, true);

        assertTrue(criteria.select(PROTECTED_METHOD, null));
        assertTrue(criteria.select(PROTECTED_STATIC_METHOD, null));
        assertTrue(criteria.select(PRIVATE_STATIC_METHOD, null));

        assertFalse(criteria.select(PUBLIC_METHOD, null));
    }

    @Test
    public void testMethodModifierCriteriaAll() {

        int modifierMask = Modifier.PROTECTED | Modifier.STATIC;

        MethodModifierCriteria criteria = new MethodModifierCriteria(modifierMask, false);

        assertTrue(criteria.select(PROTECTED_STATIC_METHOD, null));

        assertFalse(criteria.select(PROTECTED_METHOD, null));
        assertFalse(criteria.select(PRIVATE_STATIC_METHOD, null));
        assertFalse(criteria.select(PUBLIC_METHOD, null));
    }

    @SuppressWarnings("unused")
    private static void privateStaticTestMethod() {};

    protected static void protectedStaticTestMethod() {};

    @SuppressWarnings("unused")
    private void privateTestMethod() {}

    protected void protectedTestMethod() {}

    public void publicTestMethod() {}

    private interface Service {
        void query();
    }
}

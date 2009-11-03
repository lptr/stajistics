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
package org.stajistics.integration.groovy

import org.stajistics.*
import org.stajistics.event.*
import org.stajistics.session.*
import org.stajistics.tracker.*
import org.stajistics.tracker.incident.*
import org.stajistics.tracker.manual.*
import org.stajistics.tracker.span.*
import org.stajistics.tracker.CompositeStatsTrackerFactory
/**
 * 
 * @author The Stajistics Project
 */
class StatsConfigDSLInterpreterTest extends GroovyTestCase {

    private def interp

    void setUp() {
        interp = new StatsConfigDSLInterpreter()
    }

    void testEvalStringEmptyKey() {
        def configMap = interp.eval("""
            key {}
        """)

        assertEquals 1, configMap.size()
        assertEquals 'key', configMap.keySet().iterator().next()
    }

    void testEvalStringDescription() {
        def configMap = interp.eval("""
            key { desc 'test description' }
        """)

        assertEquals 1, configMap.size()
        assertEquals 'key', configMap.keySet().iterator().next()
        assertEquals 'test description', configMap.key.description
    }

    void testEvalStringUnit() {
        def configMap = interp.eval("""
            key { unit 'test unit' }
        """)

        assertEquals 1, configMap.size()
        assertEquals 'key', configMap.keySet().iterator().next()
        assertEquals 'test unit', configMap.key.unit
    }

    void testEvalStringTrackerFactoryByName() {
        def configMap = interp.eval("""
            key { tracker 'NanoTimeDuration' }
        """)

        assertEquals 1, configMap.size()
        assertEquals 'key', configMap.keySet().iterator().next()
        assertEquals NanoTimeDurationTracker.FACTORY, configMap.key.trackerFactory
    }

    /**
     * This is a tricky test. It verifies that a custom StatsTrackerFactory implementation can
     * be defined using a closure. The SpanTracker.start() method normally returns 'this', 
     * but the mock SpanTracker produced by the factory under test returns a different 
     * SpanTracker instance (not 'this'). By verifying that the mockTracker != mockTracker.start(),
     * we are assured that the mockTracker is in fact the mock, and thus, the correct factory 
     * was used. There is probably a groovier way to test this, but this is the best I could
     * come up with. 
     */
    void testEvalStringTrackerFactoryWithClosure() {
        def configMap = interp.eval("""
            key {
                tracker { key, sessionMgr ->
                    { -> { -> } as SpanTracker } as SpanTracker
                }
            }
        """)
 
        assertEquals 1, configMap.size()
        assertEquals 'key', configMap.keySet().iterator().next()

        def testKey = { -> } as StatsKey
        def testSessionManager = { -> } as StatsSessionManager

        def testFactory = configMap.key.getTrackerFactory()
        def testTracker = testFactory.createTracker(testKey, testSessionManager)

        assertTrue testTracker != testTracker.start()
    }

    void testEvalStringTrackerFactoryWithClosureOneParameter() {
        shouldFail(StatsConfigDSLParseException) {
            interp.eval("""
                key {
                    tracker { wtf ->
                        { -> } as StatsTracker
                    }
                }
            """)
        }
    }

    void testEvalStringTrackerFactoryWithClosureThreeParameters() {
        shouldFail(StatsConfigDSLParseException) {
            interp.eval("""
                key {
                    tracker { w, t, f ->
                        { -> } as StatsTracker
                    }
                }
            """)
        }
    }

    void testEvalStringTrackerFactoryComposite() {
        def configMap = interp.eval("""
            key {
                tracker([
                    time: 'NanoTimeDuration',
                    hits: 'HitFrequency'
                ])
            }
        """)

        assertTrue configMap.key.trackerFactory instanceof CompositeStatsTrackerFactory
        //TODO: assert 2 of the correct types are defined
    }

    void testEvalStringSessionFactoryByClosure() {
        def configMap = interp.eval("""
            key {
                session { key, eventMgr ->
                    { -> { -> 'testKey' } as StatsKey } as StatsSession
                }
            }
        """)

        assertEquals 'testKey', configMap.key.sessionFactory.createSession(null, null).key.name
    }

    void testEvalStringMultipleKeys() {
        def configMap = interp.eval("""
            key1 { unit 'unit1' }
            key2 { unit 'unit2' }
            key3 { unit 'unit3' }
        """)

        assertEquals 3, configMap.size()
        assertEquals 'unit1', configMap.key1.unit
        assertEquals 'unit2', configMap.key2.unit
        assertEquals 'unit3', configMap.key3.unit
    }

    void testEvalStringKeyOrder() {
       def configMap = interp.eval("""
           b {}
           d {}
           a {}
           c {}
           a.a {}
           a.a.a.a {}
           a.a.b {}
           a.b {}
       """)

       assertEquals 8, configMap.size()

       def it = configMap.keySet().iterator()
       assertEquals 'b', it.next()
       assertEquals 'd', it.next()
       assertEquals 'a', it.next()
       assertEquals 'c', it.next()
       assertEquals 'a.a', it.next()
       assertEquals 'a.a.a.a', it.next()
       assertEquals 'a.a.b', it.next()
       assertEquals 'a.b', it.next()
    }

}
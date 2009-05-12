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
import org.stajistics.tracker.*
import org.stajistics.session.*
import groovy.lang.MissingMethodException

/**
 * 
 * 
 * @author The Stajistics Project
 */
class StatsConfigDSLInterpreter {

    private def configBuilder
    private def keyStack = []
    private def inConfig = false
    private def configs = new LinkedHashMap()

    def propertyMissing(String name, args) {
        keyStack << name
        this
    }

    def methodMissing(String name, args) {
        if (!inConfig) {

            keyStack << name
            def keyName = keyStack.join('.')
            keyStack.clear()

            def closure = args[-1]
            if (!closure || !(args[-1] instanceof Closure)) {
                throw new StatsConfigDSLParseException("Must pass a closure defining configuration for key: ${keyName}") 
            }

            configs.put(keyName, evalConfig(closure))

        } else {
            if (name.equals('tracker')) {
                handleTracker args

            } else if (name.equals('session')) {
                handleSession args

            } else if (name.equals('unit')) {
                configBuilder.withUnit(args[0])

            } else if (name.equals('desc')) {
                configBuilder.withDescription(args[0])

            } else {
            	throw new MissingMethodException(name, getClass(), args)
            }
        }
    }

    private def handleTracker(args) {
        def arg = args[0]
        def factory

        if (arg instanceof CharSequence) {
            Class trackerClass = null;
            try {
                trackerClass = Class.forName("org.stajistics.tracker.${arg}Tracker")
                factory = trackerClass.getField("FACTORY").get(null)
            } catch (e) {
                factory = Class.forName(arg).newInstance()
            }

        } else if (arg instanceof Map) {

            def factories = [:]
            arg.each { keyName, trackerFactory -> 
                factories.put(keyName, handleTracker([trackerFactory]))
            }

            factory = new CompositeStatsTrackerFactory(factories)

        } else if (arg instanceof Closure) {
            if (arg.maximumNumberOfParameters != 2) {
                throw new StatsConfigDSLParseException("tracker Closure must accept two parameters: StatsKey and StatsSessionManager")
            }

            factory = arg.asType(StatsTrackerFactory.class)

        } else if (arg instanceof StatsTrackerFactory) {
            factory = arg

        } else {
            throw new StatsConfigDSLParseException("Illegal argument(s): ${args}")
        }

        configBuilder.withTrackerFactory(factory)

        return factory
    }

    private def handleSession(args) {
        def arg = args[0]
        def factory

        if (arg instanceof CharSequence) {
            def sessionFactoryClass = Class.forName(arg)

        } else if (arg instanceof Closure) {
            if (arg.maximumNumberOfParameters == 2) {
                factory = arg.asType(StatsSessionFactory.class)

            } else {
                throw new StatsConfigDSLParseException("session Closure must accept two parameters: StatsKey, StatsEventManager")
            }

        } else if (arg instanceof StatsSessionFactory) {
            factory = arg
            
        } else {
            throw new StatsConfigDSLParseException("Illegal argument(s): ${args}")
        }

        configBuilder.withSessionFactory(factory)
    }


    private def doEval(CharSequence dsl, String op) {
        def script = """
            import org.stajistics.*
            import org.stajistics.event.*
            import org.stajistics.session.*
            import org.stajistics.tracker.*

            def closure = {
                ${dsl}
            }

            closure.delegate = interpreter
            interpreter.${op}(closure)
        """

        def shell = new GroovyShell()
        shell.setVariable('interpreter', this)

        return shell.evaluate(script)
    }

    Map<String,StatsConfig> eval(CharSequence dsl) {
        doEval(dsl, 'eval')
    }

    Map<String,StatsConfig> eval(Closure closure) {
        try {
            closure.delegate = this
            closure()

            def result = new LinkedHashMap(configs)
            return result

        } finally {
            configs.clear()
        }
    }

    StatsConfig evalConfig(CharSequence dsl) {
        doEval(dsl, 'evalConfig')
    }

    StatsConfig evalConfig(Closure closure) {
        try {
            inConfig = true
            configBuilder = Stats.buildConfig()

            closure.delegate = this
            closure()

            return configBuilder.newConfig()

        } finally {
            inConfig = false
            configBuilder = null
        }
    }

    static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: groovy ${StatsConfigDSLInterpreter.class.getName()} <file>")
            System.err.println("\tfile: File containing StatsConfig DSL")
            System.exit(1);
        }

        def dsl = new File(args[0]).text
        def interp = new StatsConfigDSLInterpreter()

        println interp.eval(dsl)
        //println interp.evalConfig(dsl)
    }
}

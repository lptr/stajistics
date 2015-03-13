## Welcome ##

Stajistics is a free monitoring and runtime performance statistics collection API for Java.

For an [Introduction](Introduction.md) and further documentation see the project wiki.

## Release ##

The latest stable release version is 0.3. The current development version is 0.4-SNAPSHOT.

The Stajistics project is in the beta stage of development until the anticipated 1.0 release version. Prior to 1.0, no guarantees are made regarding API backwards compatibility between minor releases as features are still being fleshed out.

## Overview ##

Runtime Requirements:
  * Java 1.6

Build Requirements:
  * Maven 2.x or 3.x

Dependencies:
  * [SLF4J](http://www.slf4j.org)

Functionality Goals:
  * To provide an extensible, flexible framework for statistics collection
  * To allow real time monitoring of runtime statistics
  * To allow interactive control over runtime statistics collection
  * To support a variety of statistics data persistence strategies
  * To support integration with popular and industry standard APIs and frameworks such as servlets, JDBC, and [Inversion of Control](http://martinfowler.com/articles/injection.html) containers

Design Goals:
  * To provide a variety of integration options through modules
  * To keep the performance and memory overhead of statistics collection to a minimum
  * To implement thread safety and scalability in concurrency
  * To eliminate any negative impact of integration of Stajistics into client code (i.e. avoid throwing Exceptions into client code)
  * To minimize imposing transitive library dependencies on clients
  * To provide integration options ranging from direct usage in client code to non-intrusive externally defined configuration
  * To maintain an API design that facilitates management by [Inversion of Control](http://martinfowler.com/articles/injection.html) containers
  * To support behaviour customization by making use of separated interfaces and implementations, and factories

Features:
  * Extensible statistics collection API
  * In-memory statistics database
  * Event API for custom interaction with statistical data
  * Statistics querying and configuration via JMX
  * JDBC integration (In progress)
  * XML based configuration (In progress)
  * Real-time logging of statistical data
  * Emergency kill switch for disabling statistics collection on specific targets, trees of targets, or the entire Stajistics system
  * Uncaught exception handler interface for dealing with any Exceptions that were prevented from being thrown into client code

Statistics Collection Tools:
  * Servlet filter and session listener for tracking requests, sessions, and I/O
  * Reflective proxy for monitoring method calls on an object
  * Decorators for interfaces like Runnable, Callable, ThreadFactory, and Executor
  * Object life cycle monitor for watching object instantiations, lifespans, and deaths
  * InputStream/OutputStream/Reader/Writer filters for monitoring I/O

Planned Features:
  * Alarming API for responding to exceeded thresholds, for example
  * Configurable statistical data retention policy
  * Statistical data persistence
  * Annotation based configuration

## Project Widgets ##

&lt;wiki:gadget url="http://www.ohloh.net/p/319564/widgets/project\_basic\_stats.xml" height="220"  border="1" /&gt;

&lt;wiki:gadget url="http://www.ohloh.net/p/319564/widgets/project\_languages.xml" height="220"  border="1" /&gt;

&lt;wiki:gadget url="http://www.ohloh.net/p/319564/widgets/project\_factoids.xml" border="1" /&gt;
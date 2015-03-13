# Getting Started #

## Getting To Know Stajistics ##

### Terminology ###

  * Target - Something upon which statistical data can be measured and collected.
  * Key - Represents a target. Associated with trackers and a session.
  * Tracker - Knows how to measure statistical data. Several instances per key.
  * Session - Stores and performs calculations on statistical data. One instance per key.
  * Field - A named unit of data stored in a session. Several fields per session.

### Keys ###

In Stajistics, a key is what answers the question, "On what do I collect statistics?"

To track statistics about something, a key must be assigned to that something. In Stajistics documentation, something to track is referred to as a _target_. The purpose of a key is to provide a handle which can be used to access and manipulate statistical data and configuration that is specific to a _target_.

Assigning a key to a _target_ can be as simple as selecting a unique key name and, in your Java code, passing the key name String into one of the available statistics collection methods. A key can be represented by a StatsKey instance, or by a simple String (which is converted into a StatsKey automatically under the hood). Keys in String form are supported as a convenience for clients migrating from other statistics/monitoring APIs, but keys in StatsKey instance form are far more useful, and as such, are recommended over the use of String keys.

As an example, a key of "org.myapp.service.requests" may be assigned to track the number of requests that a service receives. As another example, a key of "org.myapp.servlet.sessions" might track the number and duration of user sessions managed by a web app.

A StatsKey is typically composed of a static name, as well as any number of attributes, which incorporate runtime data into the uniqueness of the key. For example, a key that represents how long an application executes could be composed of a simple static name, such as, "myapp.executionTime". There would be no need to incorporate runtime data into the key because there is just _one_ application lifespan. However, a key that represents how long a user has been logged in, for example, must incorporate the runtime data of the user name or ID to make the key unique per-user. So, a key could be composed having a static name, "myapp.user.logins", and with an attribute, "username"="john", where "john" is supplied at runtime, upon a user login.

For more details on keys, see the [StatsKey source and JavaDoc](http://code.google.com/p/stajistics/source/browse/stajistics-core/src/main/java/org/stajistics/StatsKey.java)

### Trackers ###

In Stajistics, a tracker is what answers the question, "how do I measure things to get statistical data from them?"

A StatsTracker is what receives statistical data from a _target_ piece of code. It is the main interface between the client code and the Stajistics system for the collection of data. A StatsTracker is associated with a single StatsKey, however, there may be many StatsTracker instances alive at a time for a StatsKey.

There are three main types of trackers, all of which implement the StatsTracker interface:
  * SpanTracker - Measures something between a starting point and an end point. For example, the amount of time an algorithm took to complete.
  * IncidentTracker - Records occurrences of events or incidents. For example, a user click.
  * ManualTracker - Receives any kind of numerical statistical data that client code may be aware of but the Stajistics system is not. While the SpanTracker and IncidentTracker will cover most use cases, this kind of tracker exists to allow flexibility in statistical data collection.

As you will see, a StatsTracker may be used directly in client code (i.e. to measure a portion of an algorithm buried in a method), or may be used indirectly in a non-hard-compiled, configurable manner (i.e. in a servlet Filter for measuring request/response timings).

StatsTrackers are responsible for interfacing with client code in order to _receive_ statistical data, however, they do not store the data. This is what Sessions are for.

### Sessions ###

In Stajistics, a session is what answers the question, "What statistical data do I store?"

A StatsSession holds statistical data (in memory) collected over the lifetime of a running application. A StatsSession is also associated with a single StatsKey, and there will only ever be one StatsSession instance alive for a StatsKey.

Every StatsTracker instance has a reference to its StatsSession, and when it is told, it will publish the data it collected to the session for longer term storage.

There are two kinds of session, both of which implement the StatsSession interface:
  * ConcurrentSession - Puts emphasis on performance while sacrificing the accuracy of the data collected.
  * AsynchronousSession - Focuses on the accuracy of collected data while allowing slightly degraded performance (in comparison to the ConcurrentSession).

Statistical data does not have to live in a session in-memory forever. When the application shuts down, or when prompted by the management interface, data can be dumped to a more permanent medium, such as a filesystem or a database. (Note: this capability is not currently implemented, but planned for the near future).

## What Can I Track? ##

The following is a table of tracker implementations that are standard in the stajistics-core module.

| Tracker Name | Description |
|:-------------|:------------|
| IncidentTracker | Records single occurrences of events. |
| TimeDurationTracker | Records the amount of time, in various precisions, between a start and end point. |
| ConcurrentAccessTracker | Tracks how many simultaneous accesses occur to a span defined by a start and end point. |
| HitFrequencyTracker | Measures the amount of time between accesses to a span defined by a start and end point. |
| GarbageCollectionTimeTracker | Records the amount of time spent in garbage collection between a start and end point. |
| ThreadCPUTimeTracker | Records the amount of CPU time allotted to a thread between a start and end point. |
| ThreadBlockTimeTracker | Measures the amount of time a thread spent blocking between a start and end point. |
| ThreadWaitTimeTracker | Measures the amount of time a thread spent waiting between a start and end point. |
| ManualTracker | Collects whatever numerical data is fed to it. |
| CompositeSpanTracker | Aggregates several configurable trackers into one for simple collection of multiple kinds of data on a single span defined by a start and end point. |

For any SpanTracker, calling track() marks the beginning point of the span being monitored, while calling commit() marks the end point of the span.

## What Gets Collected? ##

When a tracker collects a unit of statistical data, it hands it off to the session where it is stored and/or minor initial calculations are performed upon it. A piece of data stored in a session is known as a field, and is identified by a name.

By default, a session will only keep a handful of calculation results that can be derived from streams of data points; it will not store the data points themselves. For example, given a data point time-spent-in-block-A, only the sum and average (among other things) are stored, not the individual times themselves. Why? Storing all data points in a stream would have high demands on memory and other resources. If a full history of data points is desired, Stajistics can support that through custom DataRecorders.

The following is a table of standard fields stored in a session:

| Field Name | Description |
|:-----------|:------------|
| hits | The total number of times a tracker was invoked. |
| firstHitStamp | The long time stamp of the first hit recorded by the session. |
| lastHitStamp | The long time stamp of the most recent hit recorded by the session. |
| commits | The total number of times a unit of data was submitted to the session. |
| first | The first unit of data committed to the session. |
| last | The most recent unit of data committed to the session. |
| min | The smallest seen unit of data committed to the session. |
| max | The largest seen unit of data committed to the session. |
| sum | The total of all units of data committed to the session. |

The 'hits' field is incremented when IncidentTracker#incident() is called, or when SpanTracker#track() is called. The 'commits' field is incremented upon IncidentTracker#incident() calls as well, and when SpanTracker#commit() is called. For IncidentTrackers, hits and commits are essentially the same metric, but for SpanTrackers, comparing hits to commits (i.e. hits - commits) shows the number of trackers currently "in" a span.

## Track Something! ##

### Preparing for Statistics Collection ###

The first step to using Stajistics in your code is to obtain an instance of a StatsFactory, which is done as follows:

```
    StatsFactory statsFactory = StatsFactory.forClass(getClass());
```

Ideally, like a Logger, the StatsFactory can be stored as a static variable within your class (a StatsFactory is thread safe):

```
    import org.stajistics.StatsFactory;

    public MyClass {
        private static StatsFactory statsFactory = StatsFactory.forClass(MyClass.class);
    }
```

The first thing to do with a StatsFactory is to create a key to represent a particular target.

```
    StatsKey key = statsFactory.newKey("myAlgorithm");
```

As StatsKeys are immutable, they too are thread safe, and can be stored statically to avoid the overhead of creation every time the target is executed.

```
    import org.stajistics.StatsFactory;
    import org.stajistics.StatsKey;

    public MyClass {
        private static StatsFactory statsFactory = StatsFactory.forClass(MyClass.class);

        private static StatsKey myAlgorithmKey = statsFactory.newKey("myAlgorithm");
    }
```

The `myAlgorithmKey` instance can now represent all invocations of the theoretical myAlgorithm. But, what happens if you want to collect statistics related to something that can only be known at runtime? This is where key copying and key attributes come in to play. Say, for example, that the myAlgorithm method takes a String parameter that specifies the underlying strategy by which the method satisfies its contract. The performance of myAlgorithm may vary based on the strategy used, so measurements need to be stored specific to the strategy. The solution is to make a copy of the myAlgorithmKey at runtime which has an attribute specifying the passed-in strategy:

```
    public MyClass {
        private static StatsFactory statsFactory = StatsFactory.forClass(MyClass.class);

        private static StatsKey myAlgorithmKey = statsFactory.newKey("myAlgorithm");

        public void myAlgorithm(String strategy) {
            StatsKey strategySpecificKey = 
                myAlgorithmKey.buildCopy()
                              .withAttribute("strategy", strategy)
                              .newKey();

            // Use strategySpecificKey (how to use a key is described blow)
        }
    }
```

Lastly, what if you wanted to track the performance of myAlgorithm for all strategies combined as well as per-strategy. For this case, you would simply use both keys at once: myAlgorithmKey and strategySpecificKey. How to do this will become clear shortly. Keep reading.

### Incidents ###

The most basic use of Stajistics would be to track occurrences of events, or incidents, like so:

```
    public void somethingInteresting() {
        statsFactory.incident(key);
        // do something interesting
    }
```

The previous snippet is actually a short-hand convenience for the following:

```
    public void somethingInteresting() {
        IncidentTracker tracker = statsFactory.getIncidentTracker(key);
        tracker.incident();
        // do something interesting
    }
```

### Spans ###

Stajistics can also measure something between a start and end point, for example, the time taken to execute an algorithm:

```
    public void bigLongAlgorithm() {
        SpanTracker tracker = statsFactory.track(key);
        try {
            // Buuuubble soooort!
        } finally {
            tracker.commit();
        }
    }
```

Again, the above snippet is a short-hand convenience for the following:

```
    public void bigLongAlgorithm() {
        SpanTracker tracker = statsFactory.getSpanTracker(key);
        tracker.track();
        try {
            // Buuuubble soooort!
        } finally {
            tracker.commit();
        }
    }
```

### Manually Collected Data ###

In the event that Stajitics cannot know about the data being collected through means of the span or incident trackers, a ManualTracker can collect the data:

```
    public void setUserAge(int age) {
        ManualTracker tracker = statsFactory.getManualTracker(key);
        tracker.setValue(age);
        tracker.commit();

        this.age = age;
    }
```

A shorthand for the above could be:

```
    public void setUserAge(int age) {
        statsFactory.getManualTracker(key).setValue(age).commit();

        this.age = age;
    }
```

### Aggregating Keys ###

In each of the above tracker examples, multiple key instances can be passed into the appropriate StatsFactory method that returns a Tracker instance. This would record the collected data for each key individually.

To extend the example started in the "Preparing for Statistics Collection" section, myAlgorithm could aggregate the `myAlgorithmKey` and the `strategySpecificKey` like so:

```
    public void myAlgorithm(String strategy) {
        StatsKey strategySpecificKey = 
            myAlgorithmKey.buildCopy()
                          .withAttribute("strategy", strategy)
                          .newKey();

        SpanTracker tracker = statsFactory.track(myAlgorithmKey, strategySpecificKey);
        try {
            // Run the algorithm
        } finally {
            tracker.commit();
        }
    }
```

### Getting Dynamic ###

In the above examples, Stajistics APIs are called directly from client code, which can be a large commitment for certain code bases. The following shows a few ways to use Stajistics in a less intrusive manner.

#### Interface Proxy ####

A StatsProxy will wrap an Object, implement one or more of its interfaces, and collect statistics on method calls. An example way to use a StatsProxy that would minimize coupling to the Stajistics API would be to proxy services coming out of a factory.

```
    
    public class ServiceFactory {

        private static StatsFactory statsFactory = StatsFactory.forClass(ServiceFactory.class);

        public Service createService(String name) {

            Service service = new ServiceImpl(name);

            // Create a key to uniquely represent the service
            StatsKey serviceKey = statsFactory.buildKey("services")
                                              .withAttribute("name", name)
                                              .newKey();

            // Proxy the service, implementing all of its interfaces
            service = StatsProxy.wrap(statsFactory,
                                      serviceKey,
                                      service);

            // Clients of the factory are none the wiser :O

            return service;
        }

    }

```

## View Collected Statistics ##

Coming soon...
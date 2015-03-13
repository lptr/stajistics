# What is Stajistics? #

Stajistics is a free monitoring and runtime performance statistics collection API for Java. It is an open source project that is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

The primary goal of Stajistics is to allow the collection of statistical data in a program, to expose that data so that it may be queried in real time, and to allow persistence of the data so that historical records can be accumulated and compared. Stajistics can be used to collect data about a running application (such as how many times a method was called), or it can be used to collect domain-specific data (such as how many users in role "A" are logged in).

Stajistics is designed to be continuously active in production environments. It tries to minimize the performance impact of statistics collection in part by using concurrent, scalable data structures. By default, only _calculations_ performed on streams of data are stored in memory (as opposed to streams of data themselves), such as totals and averages, in order to keep the memory footprint small.

# What is Stajistics not? #

Stajistics is not a profiling tool. It does not perform any class instrumentation.

# How can I use Stajistics? #

The Stajistics API is initially integrated into an application by developers. After deployment of the application, statistics are collected by the Stajistics system and exposed through JMX among other means. Technical managers or system administrators may then query statistical data, configure statistics collection, or persist data for aggregate or trend report generation.

Some example usages of Stajistics might be:
  * Tracking hits and response times for a service
  * Monitoring user login counts, frequencies, and durations
  * Recording the performance of a critical algorithm

# Getting help #

Please post any questions or comments to the appropriate mailing list:

  * [stajistics-users](http://groups.google.com/group/stajistics-users) - End user discussion.
  * [stajistics-dev](http://groups.google.com/group/stajistics-dev) - Technical discussion. Receives commit and build notifications.
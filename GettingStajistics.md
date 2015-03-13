## Prerequisites ##

The most basic use of Stajistics requires Java 1.6. It is recommended that Stajistics be used in application configurations that expose management via JMX. While Stajistics can happily collect statistics without it, being able to monitor statistics in real time using JMX is really where Stajistics shines.

## Getting Stajistics ##

The latest stable release of Stajistics is version 0.3. The current development version of Stajistics is 0.4-SNAPSHOT.

Release binaries and source packages are available in the downloads section (look up).

Releases are also available through the [Maven Central repository](http://repo1.maven.org/maven2) (see below).

## Installing Stajistics ##

Installation only requires that you add `stajistics-core-<version>.jar` and its dependent jars to your project classpath. The only dependency of the stajistics-core module is `slf4j-api-<version>.jar` for logging. Refer to [the SLF4J website](http://www.slf4j.org) for information on how to configure logging.

Stajistics provides feature extensions in the form of separate module jars. One of the reasons for this is that a given feature may require additional dependency jars. This project model allows the user to select the desired features while not forcing the need for dependency jars of undesired features.

### Using Maven ###

#### Releases ####

Stajistics releases are available from the [Maven Central repository](http://repo1.maven.org/maven2). (Note: no releases have been published as of this time. A release is planned shortly.)

Add the following to your dependencies element of your pom:

```
    <dependency>
      <groupId>org.stajistics</groupId>
      <artifactId>stajistics-core</artifactId>
      <version>0.3</version>
    </dependency>
```

#### Snapshots ####

Stajistics nightly snapshot artifacts are hosted by Sonatype OSS. Ensure the following is present in your repositories element of your pom:
```
    <repository>
      <id>sonatype-nexus-snapshots</id>
      <url>https://oss.sonatype.org/content/repositories/snapshots</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
```

Add the following to your dependencies element of your pom:
```
    <dependency>
      <groupId>org.stajistics</groupId>
      <artifactId>stajistics-core</artifactId>
      <version>0.4-SNAPSHOT</version>
    </dependency>
```
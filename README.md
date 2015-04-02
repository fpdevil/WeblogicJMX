WeblogicJMX
===========

#### *This is a Work In Progress and will be updated frequently until completed*

A Set of JMX Server utilities for Weblogic server from version 9.X and above. At present it gives some monitoring statistics in the real time which would be updated with more features

* [Features](#features)
* [Concepts](#concepts)
* [Prerequisites](#prerequisites)
    * [Settings](#settings)
* [Usage](#usage)

Features
--------

Currently the following features are available:

 * JVM Monitoring Statistics
 * Server ThreadPoolRuntime Statistics
 * Server Statistics
 * JDBC Datasource Statistics
 * Application Deployment list and Status 
 * WTC Status

Concepts
--------

WeblogicJMX uses the Java JMX for MBean Server Invocation and get the statistics connecting to the DomainRuntimeMBean

Prerequisites
-------------

### Settings

Need to have a compatible JDK 1.6 and above.

Need the following libraries which needs to be placed manually till the project is mavenized

- slf4j-api-1.6.6
- logback-core-1.0.13
- logback-classic-1.0.13
- weblogicfullclient.jar (* for details of building weblogicfullclient.jar refer to oracle doc *)


### JVM 

### Thread Pool

### JDBC connection Pool

### Application Runtime

### Usage

*Invocation usage and other details will be updated soon as this is a Work in Progress.*
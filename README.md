Data Visualization Platform V - UltraHack project
=================================

Data visualization and prosessing platform utilizing microservices architecture.

![alt tag](/web/public/images/dataVisuPlatformArkkitehtuuri.png)

## 1. Integration to external data sources
The platform integrates to existing data sources via a range of methods. The exetrnal data sources can be, for example:

* Public API's of an organization
* Private API's of an organization
* Open Data
* Smart Devices
* Sensors
* Automation systems
* Databases
* ...

The key is to have access to all the data sources that matter. All of the relevant data should be available for 

* visualization
* analyses and data prosessing

And the visualizations and analyses should often be available almost in real time.

### Data polling

The method we are implementing in ultrahack for data source integration is polling: the platform enables the user to provide api endpoints that are polled at user-spesicied intervals. The polled data is submitted to kafka topics for further analysis and/or persistent storage. 

The key here is that the configuration of this polling is made **super-easy**: If you want to use/store data behind certain API, it's a no brainer.

This api polling approach could be useful in numerous different domains. For example: 
* Home automation: There are numerous different home automation platforms, gadgets and other data sources that can affect in one building. Yet they most often have access through specified api for status-checks, sensor readings etc. Now we could have it all in one place.
* Industrial machinery: Industrial equipment has nowadays a wide array of sensors, logs and usage statistics. The also tend to be more and more connected to the outside world. 
* Open Data fusion: numerous open data api's could provide useful contextual data to enrichen the data analyses. Through frequent polling this data can be stored and utilized easily. 

### Data refining and preprocessing

Often times the data provided by machinery, sensors or open data sources, is not in usable format. Platform V's refining ang preprocessing services are aimed to be simple and scalable services that refines input data to one or several output formats that could be utilized in statistical analyses, for example. The data refining services could include

* Normalization (value or frequency)
* Add contextual data
* Formating
* Value conversions
* Parsing
* Error handling

## 2. Central scalable message queue

To enable vast amounts of data with sufficient error handling and data persistence properties, an Apache Kafka Distributed server is utilized. It functions as a centran gateway to all data, and makes the system fault tolerant by acting as a buffer. It also enables the data to be used both in real time and in bathces.

## 3. Batch and stream processing

The data is streamed from Kafka to different services. 

### Persistent storage services store it for later use: 

* Scheduled prosessing services uses batch processing to utilize, for example, machile learning, forecasting and dynamic modelling algorithms 
* Connection to R Studio enables the end users of the platform to directly connect to the raw data for advanced extra analytics. It can also be used to develop the platform and its services by prototyping different calculations, and by getting to know the raw data in more detail.

### Real-time prosessing services use the data as it comes:

* updates existing models with new data
* create predictions based on last x number of datapoints
* show real-time status of a large system by using data from a wide range of inputs
* stream important filtered data straight to end users dashboard


## 4. Data visualization

The frontend web app utilizes WebSockets for duplex communication between the platform and the user. The streamed real-time data is transformed to the screen of the end user. The data is visualized in charts and KPI's, in such a intuitive way: no extra clutter and no inuseful information. 

## 5. Data analytics

This platform allows us to use R-languages vast library analytic functions. We create predictive models from the raw data and push the results to a database from which they can be fetched easily by the web service.

=================================

# Micro services architecture

The big idea behind microservices is to architect large, complex and long-lived applications as a set of cohesive services that evolve over time. The term microservices strongly suggests that the services should be small.

In short, the microservice architectural style is an approach to developing a single application as a suite of small services, each running in its own process and communicating with lightweight mechanisms, often an HTTP resource API.

#### Single node microservice architecture

![alt tag](/web/public/images/microservices-arch.png)

#### Multiple node microservice architecture with load balancer

![alt tag](/web/public/images/microservices-arch-with-elb.png)


---

# Project instructions

Project uses:
* Play Framework 2.4.x
* Apache Kafka 0.8.2

### Kafka localhost
1. Install Apache kafka with zookeeper from [kafka.apache.org](http://kafka.apache.org/downloads.html)
2. clone ```https://github.com/yahoo/kafka-manager.git``` repo for kafka manager
3. See powershell folder for start script.
4. verify that kafka works by using the provided command line consumer and producer


### Scalastyle : Check the code quality

To check code quality of all the modules
```
$ ./activator clean compile scalastyle
```

### Scoverage : Check code coverage of test cases

To check code coverage of test cases for all modules
```
$ ./activator clean coverage test
```
By default, scoverage will generate reports for each project seperately. You can merge them into an aggregated report by invoking
```
$ ./activator coverageAggregate
```

### Deployment : microservices
```
$ ./activator "project <service-name>" "run <PORT>"
```

-----------------------------------------------------------------------
References
-----------------------------------------------------------------------
* [Play Framework](http://www.playframework.com/)
* [Microservices](http://martinfowler.com/articles/microservices.html)
* [Microservices: Decomposing Applications](http://www.infoq.com/articles/microservices-intro)
* [playing-microservices](http://blog.knoldus.com/2015/06/15/play-microservice-architecture/)

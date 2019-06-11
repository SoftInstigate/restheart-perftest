
# RESTHeart Performance Tests

This repository contains all the stuff needed for perf-testing  ***RESTHeart PRO Change Streams*** feature by using *Gatling v2.3 Perf-Testing Tool*.

Refer to [Gatling 2.3 official documentation](https://gatling.io/docs/2.3) for more informations. 

## ChangeStreamSimulation.scala

Provided with Gatling v2.3 bundle, this test is divided into three main phases: 
	
 **- First phase:** The given number of WebSocket clients (_1000 clients by default_) connects to the stream, listening for the first notification sent by RESTHeart. 
 
  **- Second phase:** After receiving the first notification, all clients starts listening for test notifications. (this phase is mandatory for setting a common start line for all testing session time results) 
  
  **- Third phase:** Clients listens for all notifications pushed by RESTHeart while processing 3 notification-triggering POST requests per second (for a total of 180 requests by default). *Clients will fail this check if they won't receive all notifications.*

## Running tests into basic local environment

 1. Clone this repository.
 2. Refer to this [this page](https://gatling.io/docs/2.3/general/operations/) to configure your testing machine properly.
 3. Get your up-and-running RESTHeart PRO instance [here](https://restheart.org/get).
 4. Startup _RESTHeart_ enabling HTTP listener and [define a stream](https://restheart.org/learn/change-streams/)  into the targetted collection _(`/coll` by default)_ as follows: 
	 ```json
		"streams":[	
			{
				"stages":[{"_$match":{"fullDocument.name":{"_$var":"n"}}}],
				"uri":"changeStream"
			}
	]
	```
 5. Execute `./bin/gatling.sh`script from this project to launch the test tool. ***Gatling 2.3 needs JDK8 to run propertly.***
 6. After simulation completes, watch out the test results at the logged report location on your local filesystem.


## How to read simulation results

Gatling prints out an _html report_ at the end of each simulation under the `./results` path. 

To get rid of this bunch of data, we suggest to consider the most meaningful ***Mean Response Time*** (MRT) of *Listen notifications* test report's section as a ChangeStream performance index.  

## Customize Test Parameters

It's possible to change the test's input params by editing the starting section of `./user-files/simulations/ChangeStreamSimulation.scala`

```scala
class ChangeStreamSimulation extends Simulation {

  // Test Parameters
  // NOTE: numberOfDocumentsPosted / postPhaseDuration = POSTs RPS (3 RPS by default)

  val baseUrl = "localhost:8080";
  val numberOfClients = 1000;
  val numberOfPostedDocuments = 180;
  val postPhaseDuration = 60
  
  ...
  
}
```
package streams

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ChangeStreamSimulation extends Simulation {

  // Test Parameters
  // NOTE: numberOfDocumentsPosted / postPhaseDuration = POSTs RPS (3 RPS by default)

  val baseUrl = "localhost:8080";
  val numberOfClients = 1000;
  val numberOfPostedDocuments = 180;
  val postPhaseDuration = 60;


  val postChain = http("POST a document into test-change-streams/coll")
                .post("/coll")
                .header("Content-Type", "application/json")
                .body(StringBody(
                  """
{
    "name": "test"
}
        """))
            


  val changeStreams = scenario("ChangeStream Main Scenario")
            .exec(ws("Connect WS to stream").open("/coll/_streams/changeStream?avars={\"n\":\"test\"}"))   
            .exec(ws("First Notification").check(wsAwait.within(600).until(1).regex(".*stream.*")))
            .exec(ws("Listen for notifications").check(wsAwait.within(600).until(numberOfPostedDocuments).regex(".*stream.*")))
            
            
  
  val postDocuments = scenario("POSTing documents into watched collection").exec(postChain)
  val postFirstDocument = scenario("POSTing first document into watched collection").exec(postChain)

  val httpConf = http
  .baseURL("http://" + baseUrl)
  .wsBaseURL("ws://" + baseUrl)
  
  setUp(
    changeStreams.inject(rampUsers(numberOfClients) over 120).protocols(httpConf),
    postFirstDocument.inject(nothingFor(130 seconds), rampUsers(1) over 1).protocols(httpConf),
    postDocuments.inject(nothingFor(135 seconds), rampUsers(numberOfPostedDocuments) over postPhaseDuration).protocols(httpConf)

  )

}

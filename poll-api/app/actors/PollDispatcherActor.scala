package actors

import play.api.mvc._
import akka.actor._
import javax.inject._
import org.apache.kafka.clients.producer.{ ProducerConfig, KafkaProducer, ProducerRecord }
import java.util.HashMap
import kafka.serializer.StringDecoder
import play.api.libs.ws._
import models._
import actors._
import akka.pattern.ask
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import scala.concurrent.duration._
//import DispatcherActor._

import fi.platformv.actors.DispatcherActorBase
import fi.platformv.actors.DispatcherActorBase._

@Singleton
class PollDispatcherActor @Inject() (system: ActorSystem, ws: WSClient) 
  extends DispatcherActorBase[PollerActor, Poll] ( { (context: ActorContext, poll: Poll) => context.actorOf(Props(classOf[PollerActor], system, ws, poll)) }) {
}

package fi.platformv.actors

import scala.concurrent.Future

import org.joda.time.DateTime

import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.ActorRef
import akka.actor.PoisonPill
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout.intToTimeout
import fi.platformv.models.ActorStatus
import fi.platformv.models.KafkaErrorMessage
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json

trait ActorDispatcher[-SpawnActorProps <: fi.platformv.models.SpawnActorPropsBase] extends Actor {

  def actorIdBase: String
  
  def errorTopic: String = fi.platformv.utils.KafkaConstants.GENERAL_ERROR_TOPIC;

  def spawnActor(context: ActorContext, props: SpawnActorProps, uniqueName: String): ActorRef
  
  private lazy val errorProducer = fi.platformv.models.Producer[String](errorTopic)
  
  def sendErrorMessage(err: KafkaErrorMessage): Unit = {
    println(s"error: $err")
    errorProducer.send(Json.stringify(Json.toJson(err)))
  }

  def startActors(items: List[SpawnActorProps]) {
    items foreach (startActor(_))
  }

  def startActor(item: SpawnActorProps) {
    stopActorById(item.id)
    if (item.active) spawnActor(context, item, ActorDispatcher.getUniqueActorId(item.id, actorIdBase))
  }

  def stopActorById(id: String) {
    context.children.foreach { childActor =>
      if (childActor.path.name.contains(ActorDispatcher.getActorIdBase(id, actorIdBase)))
        childActor ! PoisonPill
    }
  }

  def stopAllActors {
    context.children foreach (_ ! PoisonPill)
  }

  def restartWithActors(items: List[SpawnActorProps]) {
    stopAllActors
    startActors(items)
  }

  def getStatuses = {
    Future.sequence(context.children.map { childActor =>
      implicit val timeout: akka.util.Timeout = 5000
      (childActor ? ActorDispatcher.GetActorStatus).mapTo[ActorStatus]
    })
  }
  
  override def postStop(): Unit = {
    println(s"stop dispatcher actor $actorIdBase")
    errorProducer.close()
  }
}

object ActorDispatcher { 
  case class GetActorStatuses()
  case class GetActorStatus()
  
  case class StopActor()
  case class StopActorById(id: String)
  case class StopActors()

  private val rnd = new scala.util.Random(DateTime.now.getMillis)
  def getActorIdBase(id: String, idBase: String): String = s"$idBase-$id"
  def getUniqueActorId(id: String, idBase: String): String = s"${getActorIdBase(id, idBase)}-${rnd.alphanumeric.take(10).mkString("")}"
}

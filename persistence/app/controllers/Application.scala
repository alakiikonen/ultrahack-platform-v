package controllers

import play.api._
import play.api.mvc._

import akka.actor._
import javax.inject._


import actors._
import actors.PersistenceDispatcherActor._
import play.api.Play.current

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {
  val dispatcher = system.actorOf(Props(current.injector.instanceOf[PersistenceDispatcherActor]), "poller-scheduler-actor")

  def index = Action {
    Ok("index")
  }
  
  def start(id: String, database: String, topic: String) = Action {
    dispatcher ! StartWriting(id, database, topic)
    Ok("started")
  }
  
  def stop = Action {
    dispatcher ! StopAllWriting
    Ok("started")
  }
}

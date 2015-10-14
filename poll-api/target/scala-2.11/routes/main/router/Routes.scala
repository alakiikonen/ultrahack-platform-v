
// @GENERATOR:play-routes-compiler
// @SOURCE:I:/platform/playing-microservices/poll-api/conf/routes
// @DATE:Wed Oct 14 13:00:28 EEST 2015

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._
import play.core.j._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:6
  Application_1: controllers.Application,
  // @LINE:17
  Assets_0: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:6
    Application_1: controllers.Application,
    // @LINE:17
    Assets_0: controllers.Assets
  ) = this(errorHandler, Application_1, Assets_0, "/")

  import ReverseRouteContext.empty

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, Application_1, Assets_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.Application.index"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """start""", """controllers.Application.start"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """restart""", """controllers.Application.restart"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """stop""", """controllers.Application.stopAll"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """status""", """controllers.Application.status"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/polls/save""", """controllers.Application.savePoll"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/polls/list""", """controllers.Application.findAllPolls"""),
    ("""DELETE""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/polls/delete/$id<[^/]+>""", """controllers.Application.deletePoll(id:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_Application_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_Application_index0_invoker = createInvoker(
    Application_1.index,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "index",
      Nil,
      "GET",
      """ Home page""",
      this.prefix + """"""
    )
  )

  // @LINE:7
  private[this] lazy val controllers_Application_start1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("start")))
  )
  private[this] lazy val controllers_Application_start1_invoker = createInvoker(
    Application_1.start,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "start",
      Nil,
      "GET",
      """""",
      this.prefix + """start"""
    )
  )

  // @LINE:8
  private[this] lazy val controllers_Application_restart2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("restart")))
  )
  private[this] lazy val controllers_Application_restart2_invoker = createInvoker(
    Application_1.restart,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "restart",
      Nil,
      "GET",
      """""",
      this.prefix + """restart"""
    )
  )

  // @LINE:9
  private[this] lazy val controllers_Application_stopAll3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("stop")))
  )
  private[this] lazy val controllers_Application_stopAll3_invoker = createInvoker(
    Application_1.stopAll,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "stopAll",
      Nil,
      "GET",
      """""",
      this.prefix + """stop"""
    )
  )

  // @LINE:10
  private[this] lazy val controllers_Application_status4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("status")))
  )
  private[this] lazy val controllers_Application_status4_invoker = createInvoker(
    Application_1.status,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "status",
      Nil,
      "GET",
      """""",
      this.prefix + """status"""
    )
  )

  // @LINE:12
  private[this] lazy val controllers_Application_savePoll5_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/polls/save")))
  )
  private[this] lazy val controllers_Application_savePoll5_invoker = createInvoker(
    Application_1.savePoll,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "savePoll",
      Nil,
      "POST",
      """""",
      this.prefix + """api/polls/save"""
    )
  )

  // @LINE:13
  private[this] lazy val controllers_Application_findAllPolls6_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/polls/list")))
  )
  private[this] lazy val controllers_Application_findAllPolls6_invoker = createInvoker(
    Application_1.findAllPolls,
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "findAllPolls",
      Nil,
      "GET",
      """""",
      this.prefix + """api/polls/list"""
    )
  )

  // @LINE:14
  private[this] lazy val controllers_Application_deletePoll7_route = Route("DELETE",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/polls/delete/"), DynamicPart("id", """[^/]+""",true)))
  )
  private[this] lazy val controllers_Application_deletePoll7_invoker = createInvoker(
    Application_1.deletePoll(fakeValue[String]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Application",
      "deletePoll",
      Seq(classOf[String]),
      "DELETE",
      """""",
      this.prefix + """api/polls/delete/$id<[^/]+>"""
    )
  )

  // @LINE:17
  private[this] lazy val controllers_Assets_versioned8_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned8_invoker = createInvoker(
    Assets_0.versioned(fakeValue[String], fakeValue[Asset]),
    HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      """ Map static resources from the /public folder to the /assets URL path""",
      this.prefix + """assets/$file<.+>"""
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_Application_index0_route(params) =>
      call { 
        controllers_Application_index0_invoker.call(Application_1.index)
      }
  
    // @LINE:7
    case controllers_Application_start1_route(params) =>
      call { 
        controllers_Application_start1_invoker.call(Application_1.start)
      }
  
    // @LINE:8
    case controllers_Application_restart2_route(params) =>
      call { 
        controllers_Application_restart2_invoker.call(Application_1.restart)
      }
  
    // @LINE:9
    case controllers_Application_stopAll3_route(params) =>
      call { 
        controllers_Application_stopAll3_invoker.call(Application_1.stopAll)
      }
  
    // @LINE:10
    case controllers_Application_status4_route(params) =>
      call { 
        controllers_Application_status4_invoker.call(Application_1.status)
      }
  
    // @LINE:12
    case controllers_Application_savePoll5_route(params) =>
      call { 
        controllers_Application_savePoll5_invoker.call(Application_1.savePoll)
      }
  
    // @LINE:13
    case controllers_Application_findAllPolls6_route(params) =>
      call { 
        controllers_Application_findAllPolls6_invoker.call(Application_1.findAllPolls)
      }
  
    // @LINE:14
    case controllers_Application_deletePoll7_route(params) =>
      call(params.fromPath[String]("id", None)) { (id) =>
        controllers_Application_deletePoll7_invoker.call(Application_1.deletePoll(id))
      }
  
    // @LINE:17
    case controllers_Assets_versioned8_route(params) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned8_invoker.call(Assets_0.versioned(path, file))
      }
  }
}
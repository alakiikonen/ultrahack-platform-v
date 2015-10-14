
// @GENERATOR:play-routes-compiler
// @SOURCE:I:/platform/playing-microservices/poll-api/conf/routes
// @DATE:Wed Oct 14 10:35:42 EEST 2015

import play.api.routing.JavaScriptReverseRoute
import play.api.mvc.{ QueryStringBindable, PathBindable, Call, JavascriptLiteral }
import play.core.routing.{ HandlerDef, ReverseRouteContext, queryString, dynamicString }


import _root_.controllers.Assets.Asset

// @LINE:6
package controllers.javascript {
  import ReverseRouteContext.empty

  // @LINE:17
  class ReverseAssets(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:17
    def versioned: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Assets.versioned",
      """
        function(file) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[Asset]].javascriptUnbind + """)("file", file)})
        }
      """
    )
  
  }

  // @LINE:6
  class ReverseApplication(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def restart: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.restart",
      """
        function() {
        
          if (true) {
            return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "start"})
          }
        
        }
      """
    )
  
    // @LINE:12
    def savePoll: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.savePoll",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/polls/save"})
        }
      """
    )
  
    // @LINE:14
    def deletePoll: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.deletePoll",
      """
        function(id) {
          return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "api/polls/delete/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("id", encodeURIComponent(id))})
        }
      """
    )
  
    // @LINE:13
    def findAllPolls: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.findAllPolls",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/polls/list"})
        }
      """
    )
  
    // @LINE:9
    def stopAll: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.stopAll",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "stop"})
        }
      """
    )
  
    // @LINE:10
    def status: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.status",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "status"})
        }
      """
    )
  
    // @LINE:6
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Application.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }


}
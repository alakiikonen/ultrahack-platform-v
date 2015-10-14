
// @GENERATOR:play-routes-compiler
// @SOURCE:I:/platform/playing-microservices/poll-api/conf/routes
// @DATE:Wed Oct 14 13:00:28 EEST 2015


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}

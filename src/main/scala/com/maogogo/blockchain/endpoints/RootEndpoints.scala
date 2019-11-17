package com.maogogo.blockchain.endpoints

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.server.Directives._
import akka.pattern.AskTimeoutException
import org.json4s.native.Serialization.write

class RootEndpoints(implicit system: ActorSystem) extends Json4sSupport {

  val logger = Logging(system, getClass)

  val blockChainEndpoints = new BlockChainEndpoints


  def apply(): Route = {
    // (authenticateBasic("Basic", myUserPassAuthenticator)
    handleExceptions(exceptionHandler) {
      route
    }
  }

  private[this] def route: Route = {
    pathEndOrSingleSlash {
      get {
        complete("hello")
      }
    } ~ blockChainEndpoints()
  }

  private[this] val exceptionHandler = ExceptionHandler {
    case e: AskTimeoutException ⇒
      logger.error(e, "timeout exception: {}", e.getMessage)
      complete(errorResponse("Timeout or Has no routee"))
    case e: Exception ⇒
      logger.error(e, "Exception {}", e.getMessage)
      complete(errorResponse(e.getMessage))
    case e: Throwable ⇒
      logger.error(e, "Throwable exception: {}", e.getMessage)
      complete(errorResponse(e.getMessage))
  }

  private def errorResponse(msg: String): HttpResponse = {
    val response = ResponseWrapped(error = ErrorWrapped("50000", msg))

    HttpResponse(
      StatusCodes.InternalServerError,
      entity = HttpEntity(
        ContentTypes.`application/json`,
        write(response)))
  }

}

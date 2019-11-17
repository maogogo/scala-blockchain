package com.maogogo.blockchain.endpoints

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.maogogo.blockchain.actors.BlockChainActor
import akka.pattern.ask
import akka.util.Timeout
import com.maogogo.blockchain.model.{InvokeChaincodeReq, InvokeChaincodeRes, QueryBlockChainInfoReq, QueryBlockChainInfoRes, QueryChaincodeReq, QueryChaincodeRes}

import scala.concurrent.duration._

class BlockChainEndpoints(implicit system: ActorSystem) extends Json4sSupport {

  implicit val timeout = Timeout(15 seconds)

  val blockActor = system.actorOf(Props[BlockChainActor], "BlockChainActor")

  def apply(): Route = {
    path("blockHeight") {
      get {
        val f = (blockActor ? QueryBlockChainInfoReq).mapTo[QueryBlockChainInfoRes]
        complete(f)
      }
    } ~ path("query") {
      get {
        parameter('name.as[String]) { name ⇒
          val f = (blockActor ? QueryChaincodeReq(name)).mapTo[QueryChaincodeRes]
          complete(f)
        }
      }
    } ~ path("invoke") {
      post {
        entity(as[InvokeChaincodeReq]) { req ⇒
          val f = (blockActor ? req).mapTo[InvokeChaincodeRes]
          complete(f)
        }
      }
    }
  }

}

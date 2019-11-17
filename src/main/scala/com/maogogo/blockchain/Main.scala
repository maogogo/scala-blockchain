package com.maogogo.blockchain

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import org.bouncycastle.jce.provider.BouncyCastleProvider

import scala.concurrent.ExecutionContext.Implicits._

object Main extends App {

  import java.security.Security

  Security.addProvider(new BouncyCastleProvider)

  val config = ServiceModule.config

  implicit val system = ActorSystem("ChainCode", config)

  val logger = Logging(system, getClass)

  val root = ServiceModule.provideRootEndpoints

  val bind = Http().bindAndHandle(root(), interface = "0.0.0.0", port = 9000)

  bind.onComplete {
    case scala.util.Success(binding) ⇒ logger.info(s"http server started! ${binding.localAddress}")
    case scala.util.Failure(ex) ⇒ logger.error("http server start failed!", ex)
  }

  println(logo)

  lazy val logo =
    """
      |    ____            __
      |   / __ \___  _____/ /_
      |  / /_/ / _ \/ ___/ __/
      | / _, _/  __(__  ) /_
      |/_/ |_|\___/____/\__/
      |""".stripMargin

  //  //
  //  //  // 有数据这里应该报错
  //  //  response.asScala.filter(_.isVerified).foreach(r ⇒ println(r.getMessage))
  //  //
  //  //
  //  //  //  import scala.compat.java8._
  //  //
  //  //  import scala.compat.java8.FutureConverters._
  //  //
  //  //  val dd = channel.sendTransaction(response)
  //  //
  //  //  val ff = dd.toScala.filter(_.isValid).map { x ⇒
  //  //    x.getTransactionID
  //  //  }
  //  //
  //  //  ff.onComplete {
  //  //    case Success(value) ⇒ println(value)
  //  //    case Failure(exception) ⇒ exception.printStackTrace()
  //  //  }
  //
  //  // query()
  //
  //  def query(): Unit = {
  //    val request = client.newQueryProposalRequest()
  //
  //    request.setArgs(Array("a"): _*)
  //    request.setFcn("query")
  //    request.setChaincodeID(ccId)
  //
  //    val queryProposals = channel.queryByChaincode(request)
  //
  //    queryProposals.asScala.foreach { pr ⇒
  //
  //      if (!pr.isVerified() || pr.getStatus() != ChaincodeResponse.Status.SUCCESS) {
  //
  //        println("error : " + pr.getMessage)
  //
  //      } else {
  //        val payload = pr.getProposalResponse.getResponse.getPayload.toStringUtf8
  //        println(payload)
  //      }
  //    }
  //  }
  //
  //
  //  //  @throws[Exception]
  //  def getClient = {
  //    val client = HFClient.createNewInstance
  //    val cryptoSuite = CryptoSuiteFactory.getDefault.getCryptoSuite
  //    client.setCryptoSuite(cryptoSuite)
  //    client
  //  }

}

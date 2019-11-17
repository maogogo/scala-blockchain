package com.maogogo.blockchain.actors

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.event.Logging
import akka.pattern.pipe
import com.google.common.base.Stopwatch
import com.maogogo.blockchain.fabric.FabricFactory
import com.maogogo.blockchain.model._
import org.hyperledger.fabric.sdk.ProposalResponse

import scala.collection.JavaConverters._

class BlockChainActor extends Actor {

  import context.dispatcher

  val logger = Logging(context.system, this)

  override def receive: Receive = {
    case QueryBlockChainInfoReq ⇒

      FabricFactory.blockchain { (_, channel, peers, _) ⇒
        val info = channel.queryBlockchainInfo(peers(0))
        sender() ! QueryBlockChainInfoRes(info.getHeight)
      }

    case QueryChaincodeReq(name) ⇒

      val watch = Stopwatch.createStarted()

      FabricFactory.blockchain { (client, channel, _, ccid) ⇒
        val req = client.newQueryProposalRequest()
        req.setArgs(Array(name): _*)
        req.setFcn("query")
        req.setChaincodeID(ccid)

        val qprs = channel.queryByChaincode(req)

        val balance = txWithResponse(qprs.asScala) {
          case h #:: _ ⇒ h.getProposalResponse.getResponse.getPayload.toStringUtf8.toLong
          case _ ⇒ 0
        }
        val time = watch.elapsed(TimeUnit.MILLISECONDS)
        logger.info("stopwatcher time cost : {}", time)
        sender() ! QueryChaincodeRes(balance, time)

      }

    case InvokeChaincodeReq(from, to, value) ⇒

      val watch = Stopwatch.createStarted()

      FabricFactory.blockchain { (client, channel, _, ccid) ⇒

        val tpr = client.newTransactionProposalRequest

        tpr.setChaincodeID(ccid)
        tpr.setFcn("invoke")
        tpr.setArgs(Array(from, to, value.toString): _*)

        val qprs = channel.sendTransactionProposal(tpr)

        import scala.compat.java8.FutureConverters._

        txWithResponse(qprs.asScala) { _ ⇒
          val txEvent = channel.sendTransaction(qprs)

          val time = watch.elapsed(TimeUnit.MILLISECONDS)
          logger.info("stopwatcher time cost : {}", time)

          txEvent.toScala.map(tx ⇒ InvokeChaincodeRes(tx.getTransactionID, time))
        } pipeTo sender()

      }
  }

  private def txWithResponse[T](responseSeq: Iterable[ProposalResponse])(callback: Iterable[ProposalResponse] ⇒ T): T = {
    responseSeq.filter(!_.isVerified) match {
      case head :: _ ⇒ throw new Exception(head.getMessage)
      case _ ⇒ callback(responseSeq.toSeq)
    }
  }

}

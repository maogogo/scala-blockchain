package com.maogogo.blockchain

import akka.actor.ActorSystem
import com.maogogo.blockchain.endpoints.RootEndpoints
import com.maogogo.blockchain.fabric._
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

trait ServiceModule {

  def config: Config = ConfigFactory.load().resolve()

  def provideRootEndpoints(implicit system: ActorSystem): RootEndpoints = new RootEndpoints

  def provideFabricBlockChain(config: Config): FabricBlockChain = {

    val orderers = config.getObjectList("fabric.orderer").asScala.map(c ⇒ toFabricOrderer(c.toConfig))

    val organizations = config.getObjectList("fabric.organization").asScala.map { _cfg ⇒

      val cfg = _cfg.toConfig

      val org = cfg.getOrg
      val msp = cfg.getMsp

      val peers = cfg.getObjectList("peer").asScala.map(c ⇒ toFabricPeer(org)(c.toConfig))

      val users = cfg.getObjectList("user").asScala.map(c ⇒ toFabricUser(msp)(c.toConfig))

      FabricOrganization(org, msp, peers, users)
    }

    FabricBlockChain(orderers, organizations)

  }

  def toFabricUser(msp: String): PartialFunction[Config, FabricUser] = {
    case config ⇒
      FabricUser(name = config.getName, mspId = msp, cert = config.getCert, pem = config.getPem)
  }

  def toFabricPeer(org: String): PartialFunction[Config, FabricPeer] = {
    case config =>
      FabricPeer(name = config.getName, grpc = config.getGrpc, org = org, tls = config.getTls, pem = config.getPem)
  }

  def toFabricOrderer: PartialFunction[Config, FabricOrderer] = {
    case config =>
      FabricOrderer(name = config.getName, grpc = config.getGrpc, org = config.getOrg, tls = config.getTls, pem = config.getPem)
  }

  implicit class ConfigImplicit(config: Config) {

    def getName: String = config.getString("name")

    def getGrpc: String = config.getString("grpc")

    def getOrg: String = config.getString("org")

    def getTls: Boolean = config.getBoolean("tls")

    def getPem: String = config.getString("pem")

    def getCert: String = config.getString("cert")

    def getMsp: String = config.getString("msp")
  }

}


object ServiceModule extends ServiceModule
package com.maogogo.blockchain.fabric

import com.maogogo.blockchain.ServiceModule
import com.maogogo.blockchain.util.ThreadLocal
import org.hyperledger.fabric.sdk.{ChaincodeID, Channel, HFClient, Peer, User}
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory
import com.google.common.cache.CacheBuilder

trait FabricFactory {

  private lazy val channelCacher = CacheBuilder.newBuilder.build[String, Channel]
  private lazy val blockChainConfig: FabricBlockChain = ServiceModule.provideFabricBlockChain(ServiceModule.config)

  // hfclient local cache
  lazy val hfClient = new ThreadLocal({
    val client = HFClient.createNewInstance
    val cryptoSuite = CryptoSuiteFactory.getDefault.getCryptoSuite
    client.setCryptoSuite(cryptoSuite)
    client
  })

  // chaincode local cache
  lazy val chaincodeID = new ThreadLocal(ChaincodeID.newBuilder().setName("mycc").build())


  def channel(client: HFClient): Channel = client.newChannel("mychannel")

  lazy val org1User = new ThreadLocal(blockChainConfig.organizations(0).users(0))

  lazy val org2User = new ThreadLocal(blockChainConfig.organizations(1).users(0))

  def orderer(client: HFClient) = new ThreadLocal({
    val or = blockChainConfig.orderers(0)
    client.newOrderer(or.name, or.grpc, or.getProperties())
  })

  def peer0Org1(client: HFClient) = new ThreadLocal({
    peer(client) {
      _.organizations(0).peers(0)
    }
  })

  def peer0Org2(client: HFClient) = new ThreadLocal({
    peer(client) {
      _.organizations(1).peers(0)
    }
  })

  def getChannelFromCache(client: HFClient): (Channel, Seq[Peer]) = {

    val peer0 = peer0Org1(client)()
    val peer1 = peer0Org2(client)()

    val cc = channelCacher.get("example_channel", () ⇒ {
      val chnl = channel(client)

      chnl.addOrderer(orderer(client)())
      chnl.addPeer(peer0)
      chnl.addPeer(peer1)

      if (!chnl.isInitialized)
        chnl.initialize()

      channelCacher.put("example_channel", chnl)

      chnl
    })

    (cc, Seq(peer0, peer1))

  }

  def blockchain[T](next: (HFClient, Channel, Seq[Peer], ChaincodeID) ⇒ T): Unit = {

    val cli = hfClient()
    cli.setUserContext(org1User())

    val (chnl, peers) = getChannelFromCache(cli)

    next(cli, chnl, peers, chaincodeID())

  }

  private[this] def peer(client: HFClient)(config: FabricBlockChain ⇒ FabricPeer): Peer = {
    val peer = config(blockChainConfig)
    client.newPeer(peer.hostname(), peer.grpc, peer.getProperties())
  }

}

object FabricFactory extends FabricFactory
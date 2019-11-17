package com.maogogo.blockchain.fabric

import java.util.Properties

sealed trait FabricNode {

  /**
   * 节点名称
   *
   * @return
   */
  def name: String

  /**
   * 节点host
   *
   * @return
   */
  def grpc: String

  /**
   * 节点组织
   *
   * @return
   */
  def org: String

  /**
   * 是否开启tls
   *
   * @return
   */
  def tls: Boolean

  /**
   * 节点pem
   *
   * @return
   */
  def pem: String

  /**
   * 节点 hostname
   *
   * @return
   */
  def hostname(): String = s"${name}.${org}"

  /**
   * 节点的属性
   *
   * @return
   */
  def getProperties(): Properties = {

    val props = new Properties()

    // 设置证书路径
    props.setProperty("pemFile", pem)
    if (tls) {
      // 设置 tls 相关
      props.setProperty("sslProvider", "openSSL")
      props.setProperty("negotiationType", "TLS")
    }

    // 设置 hostname
    props.setProperty("hostnameOverride", s"${hostname}")

    props
  }

}

case class FabricOrderer(name: String, grpc: String, org: String, tls: Boolean = true, pem: String)
  extends FabricNode

case class FabricPeer(name: String, grpc: String, org: String, tls: Boolean = true, pem: String)
  extends FabricNode

case class FabricOrganization(org: String, msp: String, peers: Seq[FabricPeer], users: Seq[FabricUser])

case class FabricBlockChain(orderers: Seq[FabricOrderer], organizations: Seq[FabricOrganization])
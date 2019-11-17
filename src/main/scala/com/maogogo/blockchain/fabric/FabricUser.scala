package com.maogogo.blockchain.fabric

import java.util

import org.hyperledger.fabric.sdk.{Enrollment, User}
import scala.collection.JavaConverters._

case class FabricUser(name: String, mspId: String, cert: String, pem: String) extends User {

  override def getName: String = name

  override def getRoles: util.Set[String] = Set.empty[String].asJava

  override def getAccount: String = None.orNull

  override def getAffiliation: String = None.orNull

  override def getEnrollment: Enrollment = FabricEnrollment(cert, pem)

  override def getMspId: String = mspId

}

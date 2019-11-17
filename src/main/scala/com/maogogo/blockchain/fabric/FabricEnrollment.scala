package com.maogogo.blockchain.fabric

import java.security.PrivateKey

import com.maogogo.blockchain.util.FileUtils
import org.hyperledger.fabric.sdk.Enrollment

case class FabricEnrollment(certFile: String, pemFile: String) extends Enrollment with Serializable {

  override def getKey: PrivateKey = FileUtils.getCertKey(pemFile)

  override def getCert: String = new String(FileUtils.getCertBytes(certFile))
}

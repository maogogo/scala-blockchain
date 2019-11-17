package com.maogogo.blockchain.util

import java.io.{FileInputStream, InputStreamReader}
import java.security.PrivateKey

import org.apache.commons.compress.utils.IOUtils
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

object FileUtils {

  def getCertBytes(file: String): Array[Byte] = {
    IOUtils.toByteArray(new FileInputStream(file))
  }

  def getCertKey(file: String): PrivateKey = {
    val reader = new PEMParser(new InputStreamReader(new FileInputStream(file)))
    val info = reader.readObject().asInstanceOf[PrivateKeyInfo]
    val pk = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME).getPrivateKey(info)
    IOUtils.closeQuietly(reader)
    pk
  }

}

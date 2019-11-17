package com.maogogo.blockchain.endpoints

import org.json4s.JsonAST.JNull
import org.json4s.{CustomSerializer, DefaultFormats}

trait Json4sSupport extends de.heikoseeberger.akkahttpjson4s.Json4sSupport {

  implicit val serialization = org.json4s.native.Serialization
  implicit val formats = DefaultFormats + new NoneOrNilSerializer

}

class NoneOrNilSerializer extends CustomSerializer[Option[_]](_ ⇒ ( {
  case JNull => None
}, {
  case None => JNull
}))

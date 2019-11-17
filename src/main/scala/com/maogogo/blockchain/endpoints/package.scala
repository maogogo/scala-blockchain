package com.maogogo.blockchain

package object endpoints {

  case class ErrorWrapped(code: String, message: String)

  case class ResponseWrapped[T](result: T = None.orNull, error: ErrorWrapped = None.orNull)

}

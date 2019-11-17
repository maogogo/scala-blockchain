package com.maogogo.blockchain.util

class ThreadLocal[T](init: ⇒ T) extends java.lang.ThreadLocal[T] with Function0[T] {

  override def initialValue: T = init

  override def apply(): T = get

  def withValue[S](thunk: (T => S)): S = thunk(get)

}

package com.maogogo.blockchain.model

case class QueryChaincodeReq(name: String)

case class QueryChaincodeRes(balance: Long, takeTime: Long)

case class InvokeChaincodeReq(from: String, to: String, value: Long)

case class InvokeChaincodeRes(payload: String, takeTime: Long)
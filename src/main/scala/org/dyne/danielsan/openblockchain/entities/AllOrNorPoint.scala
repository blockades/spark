package org.dyne.danielsan.openblockchain.entities

/**
  * All/OP_RETURN/Non-OP_RETURN
  */
case class AllOrNorPoint[T](x: T, all: T, opReturn: T, nonOpReturn: T) {

  def toMap: Map[String, T] = Map(
    ("x", x),
    ("all", all),
    ("opReturn", opReturn),
    ("nonOpReturn", nonOpReturn)
  )

}

object AllOrNorPoint {

  def fromMap[T](map: Map[String, T]) =
    AllOrNorPoint(map("x"), map("all"), map("opReturn"), map("nonOpReturn"))

}

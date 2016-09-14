package org.dyne.danielsan.openblockchain.entities

case class AllOrNorPoint(x: Long, all: Long, opReturn: Long) {

  val nonOpReturn = all - opReturn

  def toMap = {
    Map[String, Long](
      "x" -> x,
      "all" -> all,
      "op_return" -> opReturn,
      "non_op_return" -> nonOpReturn
    )
  }

  def +(that: AllOrNorPoint): AllOrNorPoint = {
    AllOrNorPoint(this.x, this.all + that.all, this.opReturn + that.opReturn)
  }

}

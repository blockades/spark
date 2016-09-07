package org.dyne.danielsan.openblockchain.entities

case class Point[T](x: T, y: T) {

  def toMap: Map[String, T] = Map(
    ("x", x),
    ("y", y)
  )

}

object Point {

  def fromMap[T](map: Map[String, T]) =
    Point(map("x"), map("y"))

}

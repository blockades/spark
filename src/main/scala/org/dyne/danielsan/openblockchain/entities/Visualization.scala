package org.dyne.danielsan.openblockchain.entities

case class Visualization[T](id: String,
                         period: String,
                         unit: String,
                         data: List[T])

package org.dyne.danielsan.openblockchain.scripts.line

import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.VizScript

object All extends VizScript[Map[String, Long]] {

  override def generate(): List[Visualization[Map[String, Long]]] = {
    List() ++
      BlocksViz.generate() ++
      TransactionsViz.generate() ++
      SignalsViz.generate()
  }

}

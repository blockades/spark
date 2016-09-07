package org.dyne.danielsan.openblockchain.scripts.misc

import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.VizScript

object All extends VizScript[Map[String, Double]] {

  override def generate(): List[Visualization[Map[String, Double]]] = {
    List() ++
      StatsViz.generate()
  }

}

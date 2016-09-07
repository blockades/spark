package org.dyne.danielsan.openblockchain.scripts.misc

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.VizScript

object All extends VizScript[Map[String, Double]] {

  override def generate(sc: SparkContext): List[Visualization[Map[String, Double]]] = {
    List() ++
      StatsViz.generate(sc)
  }

}

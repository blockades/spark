package org.dyne.danielsan.openblockchain.scripts.line

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.VizScript

object All extends VizScript[Map[String, Long]] {

  override def generate(sc: SparkContext): List[Visualization[Map[String, Long]]] = {
    List() ++
      BlocksViz.generate(sc) ++
      TransactionsViz.generate(sc) ++
      SignalsViz.generate(sc)
  }

}

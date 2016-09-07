package org.dyne.danielsan.openblockchain.scripts.line

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.gen.Signals
import org.dyne.danielsan.openblockchain.scripts.VizScript

import scala.language.postfixOps

object SignalsViz extends VizScript[Map[String, Long]] {

  override def generate(sc: SparkContext): List[Visualization[Map[String, Long]]] = {
    List("day", "week", "month", "year")
      .map(granularity => {
        val data = Signals.allOrNor(granularity)(sc)
        Visualization("signals_all_or_nor", granularity, "num", data)
      })
  }

}

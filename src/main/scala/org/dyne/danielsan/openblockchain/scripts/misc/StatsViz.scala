package org.dyne.danielsan.openblockchain.scripts.misc

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.gen.{Blocks, Signals, Transactions}
import org.dyne.danielsan.openblockchain.scripts.VizScript

object StatsViz extends VizScript[Map[String, Double]] {

  override def generate(sc: SparkContext): List[Visualization[Map[String, Double]]] = {
    val data = List("day", "week", "month", "year")
      .flatMap(granularity => {
        Seq() ++
          Blocks.average(granularity)(sc).toSeq ++
          Transactions.average(granularity)(sc).toSeq ++
          Signals.average(granularity)(sc).toSeq
      })
      .toMap

    List(
      Visualization("stats_all_or_nor", "alltime", "num", List(data))
    )
  }

}

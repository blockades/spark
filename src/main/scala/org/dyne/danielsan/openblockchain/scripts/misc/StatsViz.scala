package org.dyne.danielsan.openblockchain.scripts.misc

import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.gen.{Blocks, Signals, Transactions}
import org.dyne.danielsan.openblockchain.scripts.VizScript

object StatsViz extends VizScript[Map[String, Double]] {

  override def generate(): List[Visualization[Map[String, Double]]] = {
    val data = List("day", "week", "month", "year")
      .flatMap(granularity => {
        Seq() ++
          Blocks.average(granularity).toSeq ++
          Transactions.average(granularity).toSeq ++
          Signals.average(granularity).toSeq
      })
      .toMap

    List(
      Visualization("stats_all_or_nor", "alltime", "num", List(data))
    )
  }

}

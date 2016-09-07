package org.dyne.danielsan.openblockchain.scripts.line

import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.gen.Blocks
import org.dyne.danielsan.openblockchain.scripts.VizScript

import scala.language.postfixOps

object BlocksViz extends VizScript[Map[String, Long]] {

  override def generate(): List[Visualization[Map[String, Long]]] = {
    List("day", "week", "month", "year")
      .map(granularity => {
        val data = Blocks.allOrNor(granularity)
        Visualization("blocks_all_or_nor", granularity, "num", data)
      })
  }

}

package org.dyne.danielsan.openblockchain.scripts

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.Helpers.{ListExtra, aggregate}
import org.dyne.danielsan.openblockchain.VizScript
import org.dyne.danielsan.openblockchain.entities.{AllOrNorPoint, Visualization}
import org.dyne.danielsan.openblockchain.gen.Blocks

import scala.language.postfixOps

object BlocksViz extends VizScript[Map[String, Long]] {

  var dayData: List[AllOrNorPoint] = _

  override def generate(sc: SparkContext): List[Visualization[Map[String, Long]]] = {
    dayData = Blocks.allOrNor("day")(sc)

    List(
      Visualization("blocks_all_or_nor", "day", "num", dayData.toMapElems),
      Visualization("blocks_all_or_nor", "week", "num", aggregate(dayData, "week").toMapElems),
      Visualization("blocks_all_or_nor", "month", "num", aggregate(dayData, "month").toMapElems),
      Visualization("blocks_all_or_nor", "year", "num", aggregate(dayData, "year").toMapElems)
    )
  }

}

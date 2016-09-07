package org.dyne.danielsan.openblockchain.scripts

import org.dyne.danielsan.openblockchain.entities.{AllOrNorPoint, Point, Visualization}
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

trait VizGraph {

  implicit val formats = Serialization.formats(NoTypeHints)

  implicit val vizName: String

  implicit var dataAll: List[Point]
  implicit var dataOpReturn: List[Point]
  implicit var dataNonOpReturn: List[Point]

  def aggregateForGraph(granularity: String): Visualization = {
    val aggData = ListBuffer[AllOrNorPoint]()

    // TODO generate all x points?

    for (i <- dataAll.indices) {
      val allPoint = dataAll(i)

      aggData += AllOrNorPoint(
        allPoint.x,
        allPoint.y,
        if (i < dataOpReturn.length) dataOpReturn(i).y else 0,
        if (i < dataNonOpReturn.length) dataNonOpReturn(i).y else 0
      )
    }

    val dataPoints = aggData.toList.map(pt => write(pt))
    Visualization(vizName + "_all_or_nor", granularity, "num", dataPoints)
  }

}

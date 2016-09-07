package org.dyne.danielsan.openblockchain.scripts

import org.dyne.danielsan.openblockchain.entities.{AllOrNorPoint, Point, Visualization}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

trait VizGraph extends VizCache[Map[String, Long]] {

  implicit val vizName: String

  def aggregateForGraph(granularity: String): Unit = {
    val aggData = ListBuffer[AllOrNorPoint[Long]]()

    // TODO generate all x points?

    val dataAll = cacheGet(vizName + "_all", granularity, "num").data.map(Point.fromMap)
    val dataOpReturn = cacheGet(vizName + "_op_return", granularity, "num").data.map(Point.fromMap)
    val dataNonOpReturn = cacheGet(vizName + "_non_op_return", granularity, "num").data.map(Point.fromMap)

    for (i <- dataAll.indices) {
      val allPoint = dataAll(i)

      aggData += AllOrNorPoint[Long](
        allPoint.x,
        allPoint.y,
        if (i < dataOpReturn.length) dataOpReturn(i).y else 0,
        if (i < dataNonOpReturn.length) dataNonOpReturn(i).y else 0
      )
    }

    val data = aggData.toList.map(_.toMap)
    cachePut(Visualization(vizName + "_all_or_nor", granularity, "num", data))
  }

}

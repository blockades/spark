package org.dyne.danielsan.openblockchain.scripts.line

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.{Point, Visualization}
import org.dyne.danielsan.openblockchain.scripts.{VizGraph, VizScript}
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.language.postfixOps

/**
  * Blocks over time
  */
object BlocksViz extends VizScript with VizGraph {

  override implicit val formats = Serialization.formats(NoTypeHints)

  val vizName = "blocks"

  var dataAll: List[Point] = _
  var dataOpReturn: List[Point] = _
  var dataNonOpReturn: List[Point] = _

  override def generate(): Seq[Visualization] = Seq(
    generateAll("day"),
    generateAll("month"),
    generateOpReturn("day", opReturn = true),
    generateOpReturn("month", opReturn = true),
    generateOpReturn("day", opReturn = false),
    generateOpReturn("month", opReturn = false),
    aggregateForGraph("day"),
    aggregateForGraph("month")
  )

  def generateAll(granularity: String): Visualization = {
    dataAll = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .collect()
      .map(xy => Point(xy._1, xy._2))
      .sortBy(_.x)
      .toList

    val dataPoints = dataAll.map(dataPoint => write(dataPoint))
    Visualization(vizName + "_all", granularity, "num", dataPoints)
  }

  def generateOpReturn(granularity: String, opReturn: Boolean): Visualization = {
    val data = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .where("is_op_return = ?", opReturn)
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .collect()
      .map(xy => Point(xy._1, xy._2))
      .sortBy(_.x)
      .toList

    if (opReturn) {
      dataOpReturn = data
      val dataPoints = dataOpReturn.map(dataPoint => write(dataPoint))
      Visualization(vizName + "_op_return", granularity, "num", dataPoints)
    } else {
      dataNonOpReturn = data
      val dataPoints = dataNonOpReturn.map(dataPoint => write(dataPoint))
      Visualization(vizName + "_non_op_return", granularity, "num", dataPoints)
    }
  }

}

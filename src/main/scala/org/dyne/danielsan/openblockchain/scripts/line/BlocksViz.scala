package org.dyne.danielsan.openblockchain.scripts.line

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.{Point, Visualization}
import org.dyne.danielsan.openblockchain.scripts.{VizGraph, VizScript}

import scala.language.postfixOps

/**
  * Blocks over time
  */
object BlocksViz extends VizScript[Long] with VizGraph {

  val vizName = "blocks"

  override def generate(): Seq[Visualization[Map[String, Long]]] = {
    val spans = Seq("day", "week", "month", "year")
    spans.foreach(generateAll)
    spans.foreach(s => generateOpReturn(s, opReturn = true))
    spans.foreach(s => generateOpReturn(s, opReturn = false))
    spans.foreach(aggregateForGraph)
    cache.values
      .map(viz => viz.copy(data = viz.data.map(_.toMap)))
      .toSeq
  }

  def generateAll(granularity: String): Unit = {
    val data = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .collect()
      .map(xy => Point(xy._1, xy._2))
      .sortBy(_.x)
      .map(_.toMap)
      .toList

    cachePut(Visualization(vizName + "_all", granularity, "num", data))
  }

  def generateOpReturn(granularity: String, opReturn: Boolean): Unit = {
    val data = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .where("is_op_return = ?", opReturn)
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .collect()
      .map(xy => Point(xy._1, xy._2))
      .sortBy(_.x)
      .map(_.toMap)
      .toList

    if (opReturn) {
      cachePut(Visualization(vizName + "_op_return", granularity, "num", data))
    } else {
      cachePut(Visualization(vizName + "_non_op_return", granularity, "num", data))
    }
  }

}

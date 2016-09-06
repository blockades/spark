package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

/**
  * 1 Blocks which contain OR Transactions over time (aka OR Blocks)
  */
object OpReturnBlocksPerDayViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val dataPoints = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .where("is_op_return = ?", true) // filter by is_op_return column
      .map(s => (s - (s % 1.day.toSeconds), 1L))
      .countByKey()
      .map(xy => Map(("x", xy._1), ("y", xy._2)))
      .map(dataPoint => write(dataPoint))
      .toList

    val viz = Visualization("op_return_blocks", "day", "num", "OP_RETURN Blocks per Day", dataPoints)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

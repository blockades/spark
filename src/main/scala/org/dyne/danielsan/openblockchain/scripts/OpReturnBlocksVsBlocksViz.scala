package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

/**
  * 0 % of OR Blocks vs Blocks
  */
object OpReturnBlocksVsBlocksViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val countAll = sc.cassandraTable("openblockchain", "blocks")
      .cassandraCount()

    val countOpReturn = sc.cassandraTable("openblockchain", "blocks")
      .where("is_op_return = ?", true)
      .cassandraCount()

    val dataPoints = List(Map(("x", countOpReturn), ("y", countAll)))
    val dataPointsJson = dataPoints.map(pt => write(pt))

    val viz = Visualization("op_return_blocks_vs_blocks", "alltime", "percentage",
      "OP_RETURN Blocks vs Blocks", dataPointsJson)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

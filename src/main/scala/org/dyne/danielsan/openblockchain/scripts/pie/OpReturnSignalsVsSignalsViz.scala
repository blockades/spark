package org.dyne.danielsan.openblockchain.scripts.pie

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.Script
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

/**
  * 0 % of OR Signals vs Signals
  */
object OpReturnSignalsVsSignalsViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val countAll = sc.cassandraTable[(List[String])]("openblockchain", "transactions")
      .select("vout")
      .map(_.length)
      .sum()
      .toLong

    val countOpReturn = sc.cassandraTable[(List[String])]("openblockchain", "transactions")
      .select("vout")
      .where("is_op_return = ?", true)
      .map(_.count(_.contains("OP_RETURN")))
      .sum()
      .toLong

    val dataPoints = List(Map(("x", countOpReturn), ("y", countAll)))
    val dataPointsJson = dataPoints.map(pt => write(pt))

    val viz = Visualization("op_return_signals_vs_signals", "alltime", "percentage", dataPointsJson)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

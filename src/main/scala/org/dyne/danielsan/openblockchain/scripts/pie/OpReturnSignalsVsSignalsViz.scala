package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
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
      .filter(_.exists(_.contains("OP_RETURN")))
      .map(_.length)
      .sum()
      .toLong

    val countOpReturn = sc.cassandraTable[(List[String])]("openblockchain", "transactions")
      .select("vout")
      .where("is_op_return = ?", true)
      .filter(_.exists(_.contains("OP_RETURN")))
      .map(_.length)
      .sum()
      .toLong

    val dataPoints = List(Map(("x", countOpReturn), ("y", countAll)))
    val dataPointsJson = dataPoints.map(pt => write(pt))

    val viz = Visualization("op_return_signals_vs_signals", "alltime", "percentage",
      "OP_RETURN Signals vs Signals", dataPointsJson)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

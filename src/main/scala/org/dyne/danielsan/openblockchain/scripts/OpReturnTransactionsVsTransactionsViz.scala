package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

/**
  * 0 % of OR Transactions vs Transactions
  */
object OpReturnTransactionsVsTransactionsViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val countAll = sc.cassandraTable("openblockchain", "transactions")
      .cassandraCount()

    val countOpReturn = sc.cassandraTable("openblockchain", "transactions")
      .where("is_op_return = ?", true)
      .cassandraCount()

    val dataPoints = List(Map(("x", countOpReturn), ("y", countAll)))
    val dataPointsJson = dataPoints.map(pt => write(pt))

    val viz = Visualization("op_return_transactions_vs_transactions", "alltime", "percentage",
      "OP_RETURN Transactions vs Transactions", dataPointsJson)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

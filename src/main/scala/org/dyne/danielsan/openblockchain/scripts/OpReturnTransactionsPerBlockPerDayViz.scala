package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

/**
  * 3 number of OR Transaction per Block over time (aka OR Transactions)
  */
object OpReturnTransactionsPerBlockPerDayViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val dataPoints = sc.cassandraTable[(String, Long)]("openblockchain", "transactions")
      .select("blockhash", "blocktime")
      .where("is_op_return = ?", true)
      .map(x => (x, 1)) // map everything to ((hash, time), 1)
      // so we can count the number of unique keys, i.e. key = (hash, time)
      // the number of unique keys is the number of transactions per key
      .reduceByKey(_ + _)
      .map { case ((hash, s), numTx) => (s - (s % 1.day.toSeconds), numTx) } // remove hash from the key
      .reduceByKey(_ + _) // same as in viz 2, sum by timestamp
      .collect()
      .map(xy => Map(("x", xy._1), ("y", xy._2)))
      .map(dataPoint => write(dataPoint))
      .toList

    val viz = Visualization("op_return_transactions_per_block", "day", "num",
      "OP_RETURN Transactions per Block per Day", dataPoints)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

/**
  * 5 number of OR signals per Transactionn per Block over time (aka OR transfers, aka total number of OR's)
  */
object OpReturnSignalsPerTransactionPerBlockPerDayViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val dataPoints = sc.cassandraTable[(String, Long, List[String])]("openblockchain", "transactions")
      .select("blockhash", "blocktime", "vout")
      .map { case (hash, s, voutList) => ((hash, s), voutList.count(_.contains("OP_RETURN"))) }
      .reduceByKey(_ + _)
      .map { case ((hash, s), numTx) => (s - (s % 1.day.toSeconds), numTx) } // remove hash from the key
      .reduceByKey(_ + _) // same as in viz 2, sum by timestamp
      .collect()
      .map(xy => Map(("x", xy._1), ("y", xy._2)))
      .map(dataPoint => write(dataPoint))
      .toList

    val viz = Visualization("op_return_signals_per_transaction_per_block", "day", "num",
      "OP_RETURN Signals per Transaction per Block per Day", dataPoints)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

/**
  * 4 number of signals per Transaction per Block over time
  */
object SignalsPerTransactionPerBlockPerDayViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val dataPoints = sc.cassandraTable[(String, Long, List[String])]("openblockchain", "transactions")
      .select("blockhash", "blocktime", "vout")
      .map { case (hash, s, voutList) => ((hash, s), voutList.length) } // => ((hash, ts), numSignals)
      .reduceByKey(_ + _)
      .map { case ((hash, s), numTx) => (s - (s % 1.day.toSeconds), numTx) } // remove hash from the key
      .reduceByKey(_ + _) // same as in viz 2, sum by timestamp
      .collect()
      .map(xy => Map(("x", xy._1), ("y", xy._2)))
      .map(dataPoint => write(dataPoint))
      .toList

    // different way to visualise this (in your head (or in mine, if you can :P ))
//      .select("blockhash", "blocktime", "vout") => list of rows (transactions)
//      .map { case (hash, s, voutList) => ((hash, s), voutList.length) } => list of transactions
//      .reduceByKey(_ + _) => list of blocks
//      .map { case ((hash, s), numTx) => (s - (s % 1.day.toSeconds), numTx) } => list of blocks
//      .reduceByKey(_ + _) => list of days
//      .collect()
//      .map(xy => Map(("x", xy._1), ("y", xy._2)))
//      .map(dataPoint => write(dataPoint))
//      .toList

    val viz = Visualization("signals_per_transaction_per_block", "day", "num",
      "Signals per Transaction per Block per Day", dataPoints)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

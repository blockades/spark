package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

/**
  * 2 number of Transactions per Block over time
  */
object TransactionsPerBlockPerDayViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val dataPoints = sc.cassandraTable[(Long, List[String])]("openblockchain", "blocks")
      .select("time", "tx")
      .map { case (s, txList) => (s - (s % 1.day.toSeconds), txList.length) } // map everything to (dayTimestamp, numTx)
      .reduceByKey(_ + _) // SELECT TIME, SUM(numTx) FROM blocks GROUP BY TIME // time is the "key" from reduceByKey
      .collect() // until this point, data was in RDDs; collect "downloads" that data to the driver
      .map(xy => Map(("x", xy._1), ("y", xy._2)))
      .map(dataPoint => write(dataPoint))
      .toList

    val viz = Visualization("transactions_per_block", "day", "num", "Transactions per Block per Day", dataPoints)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

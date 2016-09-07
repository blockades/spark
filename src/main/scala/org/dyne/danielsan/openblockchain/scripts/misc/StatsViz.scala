package org.dyne.danielsan.openblockchain.scripts.misc

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.VizScript
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

object StatsViz extends VizScript {

  override def generate(): Seq[Visualization] = {
    val oneDayAgoMs = System.currentTimeMillis() - 1.day.toMillis

    val blocksMined = sc.cassandraTable("openblockchain", "blocks")
      .where("time >= ?", oneDayAgoMs)
      .cassandraCount()

    val blockTimes = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .where("time >= ?", oneDayAgoMs)
      .collect()
      .toList

    val totalTimeBetweenBlocks = blockTimes.zip(blockTimes.tail).map(pair => pair._2 - pair._1).sum
    val averageTimeBetweenBlocks = totalTimeBetweenBlocks.toDouble / blocksMined

    //    val bitcoinsMined = 0
    //    val totalTransactionFees = 0

    val numTransactions = sc.cassandraTable("openblockchain", "transactions")
      .where("blocktime >= ?", oneDayAgoMs)
      .cassandraCount()

    val data = Map(
      ("blocksMined", blocksMined),
      ("averageTimeBetweenBlocks", averageTimeBetweenBlocks),
      //      ("bitcoinsMined", bitcoinsMined),
      //      ("totalTransactionFees", totalTransactionFees),
      ("numTransactions", numTransactions)
    )

    Seq(
      Visualization("stats", "day", "num", List(write(data)))
    )
  }

}

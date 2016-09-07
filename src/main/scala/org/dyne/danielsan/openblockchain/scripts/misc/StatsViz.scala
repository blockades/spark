package org.dyne.danielsan.openblockchain.scripts.misc

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.scripts.VizScript
import org.json4s.jackson.Serialization._
import scala.concurrent.duration._

object StatsViz extends VizScript[Double] {

  override def generate(): Seq[Visualization[Map[String, Double]]] = {
    val data = Map(
      ("averageBlocksPerDay", averageBlocks("day")),
      ("averageBlocksPerMonth", averageBlocks("month")),
      ("averageOpReturnBlocksPerDay", averageBlocks("day", opReturn = true)),
      ("averageOpReturnBlocksPerMonth", averageBlocks("month", opReturn = true)),
      ("averageNonOpReturnBlocksPerDay", averageBlocks("day", opReturn = false)),
      ("averageNonOpReturnBlocksPerMonth", averageBlocks("month", opReturn = false)),

      ("averageTransactionsPerDay", averageTransactions("day")),
      ("averageTransactionsPerMonth", averageTransactions("month")),
      ("averageOpReturnTransactionsPerDay", averageTransactions("day", opReturn = true)),
      ("averageOpReturnTransactionsPerMonth", averageTransactions("month", opReturn = true)),
      ("averageNonOpReturnTransactionsPerDay", averageTransactions("day", opReturn = false)),
      ("averageNonOpReturnTransactionsPerMonth", averageTransactions("month", opReturn = false)),

      ("averageSignalsPerDay", averageSignals("day")),
      ("averageSignalsPerMonth", averageSignals("month")),
      ("averageOpReturnSignalsPerDay", averageSignals("day", opReturn = true)),
      ("averageOpReturnSignalsPerMonth", averageSignals("month", opReturn = true)),
      ("averageNonOpReturnSignalsPerDay", averageSignals("day", opReturn = false)),
      ("averageNonOpReturnSignalsPerMonth", averageSignals("month", opReturn = false))
    )

    Seq(
      Visualization("stats", "alltime", "num", List(data))
    )
  }

  def averageBlocks(granularity: String): Double = {
    val numBlocks = sc.cassandraTable("openblockchain", "blocks")
      .cassandraCount()
      .toDouble

    val numX = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .count()

    numBlocks / numX
  }

  def averageBlocks(granularity: String, opReturn: Boolean): Double = {
    val numBlocks = sc.cassandraTable("openblockchain", "blocks")
      .where("is_op_return = ?", opReturn)
      .cassandraCount()
      .toDouble

    val numX = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time")
      .where("is_op_return = ?", opReturn)
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .count()

    numBlocks / numX
  }

  def averageTransactions(granularity: String): Double = {
    val numTransactions = sc.cassandraTable("openblockchain", "transactions")
      .cassandraCount()
      .toDouble

    val numX = sc.cassandraTable[(Long)]("openblockchain", "transactions")
      .select("blocktime")
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .count()

    numTransactions / numX
  }

  def averageTransactions(granularity: String, opReturn: Boolean): Double = {
    val numTransactions = sc.cassandraTable("openblockchain", "transactions")
      .where("is_op_return = ?", opReturn)
      .cassandraCount()
      .toDouble

    val numX = sc.cassandraTable[(Long)]("openblockchain", "transactions")
      .select("blocktime")
      .where("is_op_return = ?", opReturn)
      .map(s => (s.floorTimestamp(granularity), 1L))
      .reduceByKey(_ + _)
      .count()

    numTransactions / numX
  }

  def averageSignals(granularity: String): Double = {
    val numSignals = sc.cassandraTable[(List[String])]("openblockchain", "transactions")
      .select("vout")
      .map(_.length)
      .sum()

    val numX = sc.cassandraTable[(Long, List[String])]("openblockchain", "transactions")
      .select("blocktime", "vout")
      .map {
        case (s, voutList) => (s.floorTimestamp(granularity), voutList.length.toLong)
      }
      .reduceByKey(_ + _)
      .count()

    numSignals / numX
  }

  def averageSignals(granularity: String, opReturn: Boolean): Double = {
    val numSignals = sc.cassandraTable[(List[String])]("openblockchain", "transactions")
      .select("vout")
      .where("is_op_return = ?", opReturn)
      .map(_.count(_.contains("OP_RETURN") == opReturn))
      .sum()

    val numX = sc.cassandraTable[(Long, List[String])]("openblockchain", "transactions")
      .select("blocktime", "vout")
      .where("is_op_return = ?", opReturn)
      .map {
        case (s, voutList) => (
          s.floorTimestamp(granularity),
          voutList.count(_.contains("OP_RETURN") == opReturn).toLong
          )
      }
      .reduceByKey(_ + _)
      .count()

    numSignals / numX
  }

//  def blocksMined(): Double = {
//    val oneDayAgoMs = System.currentTimeMillis() - 1.day.toMillis
//    val blocksMined = sc.cassandraTable("openblockchain", "blocks")
//      .where("time >= ?", oneDayAgoMs)
//      .cassandraCount()
//  }

//  def averageTimeBetweenBlocks(): Double = {
//    val oneDayAgoMs = System.currentTimeMillis() - 1.day.toMillis
//    val blockTimes = sc.cassandraTable[(Long)]("openblockchain", "blocks")
//      .select("time")
//      .where("time >= ?", oneDayAgoMs)
//      .collect()
//      .toList
//
//    val totalTimeBetweenBlocks = blockTimes.zip(blockTimes.tail).map(pair => pair._2 - pair._1).sum
//    val averageTimeBetweenBlocks = totalTimeBetweenBlocks.toDouble / blocksMined
//  }

//  def numTransactions(): Double = {
//    val oneDayAgoMs = System.currentTimeMillis() - 1.day.toMillis
//    val numTransactions = sc.cassandraTable("openblockchain", "transactions")
//      .where("blocktime >= ?", oneDayAgoMs)
//      .cassandraCount()
//  }

}

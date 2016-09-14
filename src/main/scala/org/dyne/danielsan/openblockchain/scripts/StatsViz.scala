package org.dyne.danielsan.openblockchain.scripts

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.Helpers.{aggregate, reduceStats}
import org.dyne.danielsan.openblockchain.VizScript
import org.dyne.danielsan.openblockchain.entities.{AllOrNorPoint, Visualization}
import org.dyne.danielsan.openblockchain.gen.{Blocks, Signals, Transactions}

object StatsViz extends VizScript[Map[String, Double]] {

  override def generate(sc: SparkContext): List[Visualization[Map[String, Double]]] = {
    val data = generateVizAgg(Blocks.allOrNor("day")(sc), "blocks") ++
      generateVizAgg(Transactions.allOrNor("day")(sc), "transactions") ++
      generateVizAgg(Signals.allOrNor("day")(sc), "signals")

    List(
      Visualization("stats_all_or_nor", "alltime", "num", List(data))
    )
  }

  def generateVizAgg(dayData: List[AllOrNorPoint], name: String): Map[String, Double] = {
    generateViz(dayData, name + "_per_day") ++
      generateViz(aggregate(dayData, "week"), name + "_per_week") ++
      generateViz(aggregate(dayData, "month"), name + "_per_month") ++
      generateViz(aggregate(dayData, "year"), name + "_per_year")
  }

  def generateViz(data: List[AllOrNorPoint], name: String): Map[String, Double] = {
    val pt = reduceStats(data)
    val units = pt.x.toDouble

    Map[String, Double](
      s"avg_all_$name" -> pt.all / units,
      s"avg_op_return_$name" -> pt.opReturn / units,
      s"avg_non_op_return_$name" -> pt.nonOpReturn / units
    )
  }

}

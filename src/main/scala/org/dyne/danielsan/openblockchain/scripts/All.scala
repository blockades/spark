package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.Script
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.io.StdIn

object All extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val vizList = generate(sc) ++ generateStats(sc)
      .map(viz => viz.copy(data = viz.data.map(pt => write(pt))))
    println(s"GENERATED ${vizList.length} visualizations")

    sc.parallelize(vizList)
      .saveToCassandra("openblockchain", "visualizations")

    vizList.foreach { viz =>
      println("SAVED: " + viz.copy(data = viz.data.take(20)))
    }

    val isLocal = sys.env.get("OBC_SPARK_MASTER").exists(_.contains("local"))
    if (isLocal) {
      StdIn.readLine()
    }

    sc.stop()
  }

  def generate(sc: SparkContext): List[Visualization[Map[String, Long]]] = {
    List() ++
      BlocksViz.generate(sc) ++
      TransactionsViz.generate(sc) ++
      SignalsViz.generate(sc)
  }

  def generateStats(sc: SparkContext): List[Visualization[Map[String, Double]]] = {
    val data = StatsViz.generateVizAgg(BlocksViz.dayData, "blocks") ++
      StatsViz.generateVizAgg(TransactionsViz.dayData, "transactions") ++
      StatsViz.generateVizAgg(SignalsViz.dayData, "signals")

    List(
      Visualization("stats_all_or_nor", "alltime", "num", List(data))
    )
  }

}

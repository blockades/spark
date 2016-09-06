package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.Visualization

import scala.concurrent.duration._

trait VizScript extends Script {

  implicit class BetterLong(l: Long) {
    def floorTimestamp(granularity: String): Long = {
      granularity match {
        case "day" => l - (l % 1.day.toSeconds)
        case "month" => l - (l % 1.day.toSeconds) // TODO
        case _ => l
      }
    }
  }

  override def main(args: Array[String]) {
    super.main(args)
    val allViz = generate()
    sc.parallelize(allViz).saveToCassandra("openblockchain", "visualizations")
    allViz.foreach { viz =>
      println("SAVED: " + viz.copy(data = viz.data.take(10)))
    }
    sc.stop()
  }

  def generate(): Seq[Visualization]

}

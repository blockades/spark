package org.dyne.danielsan.openblockchain.scripts

import java.util.Calendar

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.concurrent.duration._

trait VizScript[T] extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  implicit class BetterLong(l: Long) {
    def floorTimestamp(granularity: String): Long = {
      granularity match {
        case "day" => l - (l % 1.day.toSeconds)
        case "week" =>
          val cal = Calendar.getInstance()
          cal.setTimeInMillis(l * 1000)
          cal.set(Calendar.SECOND, 0)
          cal.set(Calendar.MINUTE, 0)
          cal.set(Calendar.HOUR_OF_DAY, 0)
          cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
          cal.getTimeInMillis / 1000
        case "month" =>
          val cal = Calendar.getInstance()
          cal.setTimeInMillis(l * 1000)
          cal.set(Calendar.SECOND, 0)
          cal.set(Calendar.MINUTE, 0)
          cal.set(Calendar.HOUR_OF_DAY, 0)
          cal.set(Calendar.DAY_OF_MONTH, 1)
          cal.getTimeInMillis / 1000
        case "year" =>
          val cal = Calendar.getInstance()
          cal.setTimeInMillis(l * 1000)
          cal.set(Calendar.SECOND, 0)
          cal.set(Calendar.MINUTE, 0)
          cal.set(Calendar.HOUR_OF_DAY, 0)
          cal.set(Calendar.DAY_OF_YEAR, 1)
          cal.getTimeInMillis / 1000
        case _ => l
      }
    }
  }

  override def main(args: Array[String]) {
    super.main(args)
    val allViz = generate()
      .map(viz => viz.copy(data = viz.data.map(pt => write(pt))))
    sc.parallelize(allViz).saveToCassandra("openblockchain", "visualizations")
    allViz.foreach { viz =>
      println("SAVED: " + viz.copy(data = viz.data.take(10)))
    }
    sc.stop()
  }

  def generate(): Seq[Visualization[Map[String, T]]]

}

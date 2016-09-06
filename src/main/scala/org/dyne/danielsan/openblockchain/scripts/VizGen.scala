package org.dyne.danielsan.openblockchain.scripts

import java.util.Calendar

import com.datastax.spark.connector._
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

object VizGen extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val dataPoints = sc.cassandraTable("openblockchain", "blocks")
      .map(row => row.getLong("time"))
      .map(toDayTimestamp)
      .countByValue()
      .map(x => Map[String, Long](("x", x._1), ("y", x._2)))
      .toList
      .map(x => write(x))

    val viz = ("blocks", "day", "num", dataPoints, "Blocks per Day")

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    println("saved to cassandra: " + viz)

    sc.stop()
  }

  def toDayTimestamp(timestamp: Long): Long = {
    val cal = Calendar.getInstance
    cal.setTimeInMillis(timestamp * 1000)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTimeInMillis
  }

}

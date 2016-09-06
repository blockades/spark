package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.models.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._

import scala.concurrent.duration._

/**
  * 0 Blocks over time
  */
object BlocksPerDayViz extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val rows = sc.cassandraTable[(Long)]("openblockchain", "blocks")
      .select("time") // column time
      .map(s => (s - (s % 1.day.toSeconds), 1L)) // timestamp => day timestamp

    println(s"found ${rows.count()} rows in ${rows.getNumPartitions} partitions")

    val counts = rows
      .countByKey()
    //      .reduceByKey(_ + _) // count of each unique day timestamp // SELECT TIME, COUNT(*) FROM blocks GROUP BY TIME
    //      .collect()
    //      .toMap

    println(s"found ${counts.size} entries (counts)")

    val dataPoints = counts
      .map(xy => Map(("x", xy._1), ("y", xy._2))) // xy is a (timestamp, count) pair, convert it to an object
      .map(dataPoint => write(dataPoint)) // scala Maps => JSON objects (stringified)
      .toList

    val viz = Visualization("blocks", "day", "num", "Blocks per Day", dataPoints)
    println("generated: " + viz)

    sc.parallelize(Seq(viz))
      .saveToCassandra("openblockchain", "visualizations")

    sc.stop()
  }

}

package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._

object VizGen extends Script {

  override def main(args: Array[String]) {
    super.main(args)

    val numRows = sc.cassandraTable("openblockchain", "blocks")
      .map(row => row.getLong("time"))
      .count()

    println(s"NUM ROWS $numRows")

    sc.stop()
  }

}

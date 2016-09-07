package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._

object Counter extends Script {

  override def main(args: Array[String]) {
    super.main(args)

    val blocksCount = sc.cassandraTable("openblockchain", "blocks").cassandraCount()
    val transactionsCount = sc.cassandraTable("openblockchain", "transactions").cassandraCount()

    println(s"Blocks: $blocksCount")
    println(s"Transactions: $transactionsCount")

    sc.stop()
  }

}

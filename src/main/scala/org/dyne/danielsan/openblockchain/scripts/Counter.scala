package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._

object Counter extends Script {

  override def main(args: Array[String]) {
    super.main(args)

    val blocksCount = sc.cassandraTable("openblockchain", "blocks").count()
    val transactionsCount = sc.cassandraTable("openblockchain", "transactions").count()

    println(s"Blocks: $blocksCount")
    println(s"Transactions: $transactionsCount")

    sc.stop()
  }

}

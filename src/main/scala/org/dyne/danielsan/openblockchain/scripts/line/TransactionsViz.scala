package org.dyne.danielsan.openblockchain.scripts.line

import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.dyne.danielsan.openblockchain.gen.Transactions
import org.dyne.danielsan.openblockchain.scripts.VizScript

import scala.language.postfixOps

object TransactionsViz extends VizScript[Map[String, Long]] {

  override def generate(sc: SparkContext): List[Visualization[Map[String, Long]]] = {
    List("day", "week", "month", "year")
      .map(granularity => {
        val data = Transactions.allOrNor(granularity)(sc)
        Visualization("transactions_all_or_nor", granularity, "num", data)
      })
  }

}

package org.dyne.danielsan.openblockchain.gen

import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.Helpers.LongExtra
import org.dyne.danielsan.openblockchain.entities.AllOrNorPoint

object Signals {

  def allOrNor(granularity: String)(implicit sc: SparkContext): List[AllOrNorPoint] = {
    sc.cassandraTable[(Long, List[String])]("openblockchain", "transactions")
      .select("blocktime", "vout")
      .map {
        case (time, voutList) =>
          val ts = time.floorTs(granularity)
          val countAll = voutList.length
          val countOpReturn = voutList.count(_.contains("OP_RETURN"))
          val pt = AllOrNorPoint(ts, countAll, countOpReturn)
          (ts, pt)
      }
      .reduceByKey(_ + _)
      .map(_._2)
      .sortBy(_.x)
      .collect()
      .toList
  }

}

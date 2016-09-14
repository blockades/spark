package org.dyne.danielsan.openblockchain.gen

import com.datastax.spark.connector._
import org.apache.spark.SparkContext
import org.dyne.danielsan.openblockchain.Helpers.{BooleanExtra, LongExtra}
import org.dyne.danielsan.openblockchain.entities.AllOrNorPoint

object Blocks {

  def allOrNor(granularity: String)(implicit sc: SparkContext): List[AllOrNorPoint] = {
    sc.cassandraTable[(Long, Boolean)]("openblockchain", "blocks")
      .select("time", "is_op_return")
      .map {
        case (time, isOpReturn) =>
          val ts = time.floorTs(granularity)
          val pt = AllOrNorPoint(ts, 1, isOpReturn.toLong)
          (ts, pt)
      }
      .reduceByKey(_ + _)
      .map(_._2)
      .sortBy(_.x)
      .collect()
      .toList
  }

}

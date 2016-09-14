package org.dyne.danielsan.openblockchain

import java.util.Calendar

import org.dyne.danielsan.openblockchain.entities.AllOrNorPoint

object Helpers {

  def aggregate(data: List[AllOrNorPoint], granularity: String): List[AllOrNorPoint] = {
    data
      .map(pt => pt.copy(x = pt.x.floorTs(granularity)))
      .groupBy(_.x)
      .map(_._2.reduce(_ + _))
      .toList
      .sortBy(_.x)
  }

  def reduceStats(data: List[AllOrNorPoint]): AllOrNorPoint = {
    data
      .reduce(_ + _)
      .copy(x = data.length)
  }

  implicit class ListExtra(list: List[AllOrNorPoint]) {
    def toMapElems = list.map(_.toMap)
  }

  implicit class LongExtra(l: Long) {
    def floorTs(granularity: String): Long = granularity match {
      case "day" => toDayTimestamp(l)
      case "week" => toWeekTimestamp(l)
      case "month" => toMonthTimestamp(l)
      case "year" => toYearTimestamp(l)
    }
  }

  implicit class BooleanExtra(b: Boolean) {
    def toLong: Long = if (b) 1L else 0L
  }

  private def toDayTimestamp(l: Long): Long = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(l)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.getTimeInMillis
  }

  private def toWeekTimestamp(l: Long): Long = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(l)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    cal.getTimeInMillis
  }

  private def toMonthTimestamp(l: Long): Long = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(l)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    cal.getTimeInMillis
  }

  private def toYearTimestamp(l: Long): Long = {
    val cal = Calendar.getInstance()
    cal.setTimeInMillis(l)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.DAY_OF_YEAR, 1)
    cal.getTimeInMillis
  }

}

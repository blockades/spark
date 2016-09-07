package org.dyne.danielsan.openblockchain.gen

import java.util.Calendar

trait Helpers {

  def floorTimestamp(l: Long, granularity: String): Long = {
    granularity match {
      case "day" => toDayTimestamp(l)
      case "week" => toWeekTimestamp(l)
      case "month" => toMonthTimestamp(l)
      case "year" => toYearTimestamp(l)
    }
  }

  def booleanToLong(b: Boolean): Long = {
    if (b) 1L else 0L
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

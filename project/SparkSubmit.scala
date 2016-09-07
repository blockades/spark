import sbt._
import sbtassembly.AssemblyKeys._
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

import scala.collection.mutable

object SparkSubmit {

  private lazy val sparkMaster = sys.env.getOrElse("OBC_SPARK_MASTER", "local[*]")

  private sealed case class Script(scriptName: String, scriptCls: String, memorySize: String) {
    def toSparkSubmit = {
      val params = mutable.MutableList(
        "--class", s"org.dyne.danielsan.openblockchain.scripts.$scriptCls",
        "--executor-memory", memorySize,
        "--master", sparkMaster
      )

      SparkSubmitSetting(s"submit-$scriptName", params)
    }
  }

  // Define sbt tasks for every script
  private lazy val configs = Seq(
    Script("Counter", "Counter", "6g"),
    Script("BlocksViz", "line.BlocksViz", "6g"),
    Script("TransactionsViz", "line.TransactionsViz", "6g"),
    Script("SignalsViz", "line.SignalsViz", "6g"),
    Script("LineAll", "line.All", "6g"),
    Script("StatsViz", "misc.StatsViz", "6g"),
    Script("MiscAll", "misc.All", "6g")
  )

  lazy val configurations = SparkSubmitSetting(configs.map(_.toSparkSubmit): _*)

}

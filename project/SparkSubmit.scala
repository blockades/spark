import SparkSubmit.Script
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

import scala.collection.mutable

object SparkSubmit {

  private lazy val sparkMaster = sys.env.getOrElse("OBC_SPARK_MASTER", "local[*]")
  private lazy val executorHighMem = "2g"
  private lazy val executorLowMem = "500m"

  private sealed case class Script(scriptName: String, highMem: Boolean = false, lowMem: Boolean = true) {
    def toSparkSubmit = {
      val params = mutable.MutableList(
        "--class", s"org.dyne.danielsan.openblockchain.scripts.$scriptName",
        "--master", sparkMaster
      )

      if (highMem) {
        params ++= "--executor-memory" :: executorHighMem :: Nil
      } else if (lowMem) {
        params ++= "--executor-memory" :: executorLowMem :: Nil
      }

      SparkSubmitSetting(s"submit-$scriptName", params)
    }
  }

  // Define sbt tasks for every script
  private lazy val configs = Seq(
    Script("MainTest", lowMem = true),
    Script("VizGen")
  )

  lazy val configurations = SparkSubmitSetting(configs.map(_.toSparkSubmit): _*)

}

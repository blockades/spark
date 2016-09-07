import sbt._
import sbtassembly.AssemblyKeys._
import sbtsparksubmit.SparkSubmitPlugin.autoImport._

import scala.collection.mutable

object SparkSubmit {

  private lazy val masterAddress = sys.env("OBC_SPARK_MASTER_ADDRESS")
  private lazy val sparkMaster = sys.env.getOrElse("OBC_SPARK_MASTER", "local[*]")

  private sealed case class Script(scriptName: String, scriptCls: String, memorySize: String) {

    val isLocal = sparkMaster.contains("local")

    def toSparkSubmit = {
      val params = mutable.MutableList(
        "--class", s"org.dyne.danielsan.openblockchain.scripts.$scriptCls",
        "--executor-memory", memorySize,
        "--deploy-mode", if (isLocal) "client" else "cluster",
        "--master", sparkMaster
      )

      val task = SparkSubmitSetting(s"submit-$scriptName", params)
      if (!isLocal) {
        task.settings(sparkSubmitJar in task := {
          val file = assembly.value
          val filename = file.getName

          // should upload to every master?
          //        println(s"upload jar manually to hdfs:///jars/$filename :P")
          //        readLine("press enter when done")
          println(s"Uploading $filename to master...")
          println(ssh(masterAddress)("mkdir -p /openblockchain/jars/"))
          println(scp(masterAddress, file))
          //
          //        println(s"Uploading $filename to HDFS...")
          //        def hadoopFS(cmd: String) = s"docker exec spark-master hadoop fs $cmd"
          //        println(ssh(masterAddress)(
          //          hadoopFS(s"-mkdir -p /jars/"),
          //          hadoopFS(s"-put -f /jars/$filename /jars/")
          //        ))
          s"/jars/$filename"
        })
      }
      task
    }
  }

  // Define sbt tasks for every script
  private lazy val configs = Seq(
    Script("Counter", "Counter", "1g"),
    Script("BlocksViz", "line.BlocksViz", "1g"),
    Script("TransactionsViz", "line.TransactionsViz", "1g"),
    Script("SignalsViz", "line.SignalsViz", "1g"),
    Script("StatsViz", "misc.StatsViz", "1g")
  )

  lazy val configurations = SparkSubmitSetting(configs.map(_.toSparkSubmit): _*)

  private def scp(address: String, file: File): Int = {
    val cmds = Seq(
      "scp",
      file.getAbsolutePath,
      s"root@$address:/openblockchain/jars/${file.getName}"
    )
    val res = cmds !

    println("$ " + cmds.mkString(" "))
    if (res != 0) sys.error("Failed to upload job jar to master.")
    res
  }

  private def ssh(address: String)(commands: String*): String = {
    val cmds = Seq(
      "ssh",
      "-o", "UserKnownHostsFile=/dev/null",
      "-o", "StrictHostKeyChecking=no",
      s"root@$address",
      commands.mkString("; ")
    )

    println("$ " + cmds.mkString(" "))
    cmds !!
  }

}

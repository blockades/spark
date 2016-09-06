package org.dyne.danielsan.openblockchain.scripts

import java.io.File

import org.apache.log4j.PropertyConfigurator
import org.apache.spark.{SparkConf, SparkContext}

trait Script {

  protected lazy val scriptName = "OpenBlockChain_" + getClass.getSimpleName.stripSuffix("$")
  protected lazy val sc = new SparkContext(
    new SparkConf()
      .setIfMissing("spark.app.name", scriptName)
      .setIfMissing("spark.eventLog.dir", "/openblockchain/logs")
      .setIfMissing("spark.eventLog.enabled", "true")
      .setIfMissing("spark.cassandra.connection.host", sys.env.getOrElse("CASSANDRA_HOST", "localhost"))
//      .setIfMissing("spark.cleaner.ttl", SparkCleanerTtl.toString)
  )

  //  protected lazy val hdfs = FileSystem.get(sc.hadoopConfiguration)
//    protected lazy val sqlc = new SQLContext(sc)

  def main(args: Array[String]) {
    // Log4j properties
    Option(getClass.getResource("/org/dyne/danielsan/openblockchain/res/log4j.properties")) match {
      case Some(url) => PropertyConfigurator.configure(url)
      case None => System.err.println("Unable to load log4j.properties")
    }

    // Ensure the event log directory exists
    new File("/openblockchain/logs").mkdirs()
  }

}

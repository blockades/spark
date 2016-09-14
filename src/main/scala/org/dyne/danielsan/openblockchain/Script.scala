package org.dyne.danielsan.openblockchain

import org.apache.log4j.PropertyConfigurator
import org.apache.spark.{SparkConf, SparkContext}

// TODO: fix logging
trait Script {

  protected lazy val scriptName = "OpenBlockChain_" + getClass.getSimpleName.stripSuffix("$")
  protected lazy val sc = new SparkContext(
    new SparkConf()
      .setIfMissing("spark.app.name", scriptName)
      .setIfMissing("spark.cassandra.connection.host", sys.env.getOrElse("CASSANDRA_HOST", "localhost"))
  )

  def main(args: Array[String]) {
    // Log4j properties
    Option(getClass.getResource("/org/dyne/danielsan/openblockchain/res/log4j.properties")) match {
      case Some(url) => PropertyConfigurator.configure(url)
      case None => System.err.println("Unable to load log4j.properties")
    }
  }

}

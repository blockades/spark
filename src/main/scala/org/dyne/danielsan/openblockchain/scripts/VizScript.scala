package org.dyne.danielsan.openblockchain.scripts

import com.datastax.spark.connector._
import org.dyne.danielsan.openblockchain.entities.Visualization
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

import scala.io.StdIn

trait VizScript[T <: AnyRef] extends Script {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def main(args: Array[String]) {
    super.main(args)

    val vizList = generate()
      .map(viz => viz.copy(data = viz.data.map(pt => write(pt))))
    println(s"GENERATED ${vizList.length} visualizations")

    sc.parallelize(vizList)
      .saveToCassandra("openblockchain", "visualizations")
    vizList.foreach { viz =>
      println("SAVED: " + viz.copy(data = viz.data.take(10)))
    }

    val isLocal = sys.env.get("OBC_SPARK_MASTER").exists(_.contains("local"))
    if (isLocal) {
      StdIn.readLine()
    }

    sc.stop()
  }

  def generate(): List[Visualization[T]]

}

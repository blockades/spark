package org.dyne.danielsan.openblockchain.scripts

// TODO: fix logging
object MainTest extends Script {

  val NUM_SAMPLES = 100

  override def main(args: Array[String]) {
    super.main(args)

    val count = sc.parallelize(1 to NUM_SAMPLES).map { i =>
      val x = Math.random()
      val y = Math.random()
      if (x * x + y * y < 1) 1 else 0
    }.reduce(_ + _)
    println("Pi is roughly " + 4.0 * count / NUM_SAMPLES)

    sc.stop()
  }

}

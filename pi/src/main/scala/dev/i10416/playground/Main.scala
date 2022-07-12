package dev.i10416
package playground
import org.apache.spark.sql.SparkSession
object Program {
  def main(args: Array[String]): Unit = {
    // val Right(n) = parse(args)
    val spark = SparkSession
      .builder()
      .appName("pi")
      // .master("local[2]")
      .getOrCreate()
    import spark.implicits._
    val n = 200000
    val cnt = spark.sparkContext
      .parallelize(0 until n)
      .map { i =>
        // [0.0,1.0] => [-1.0,1.0]

        val x = scala.math.random() * 2 - 1
        val y = scala.math.random() * 2 - 1
        if (x * x + y * y <= 1) 1 else 0
      }
      .reduce(_ + _)
    println(s"Pi is roughly ${4.0 * cnt / n}")
  }
}

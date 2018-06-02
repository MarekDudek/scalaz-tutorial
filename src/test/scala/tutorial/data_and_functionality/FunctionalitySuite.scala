package tutorial.data_and_functionality

import java.lang.Math.PI

import org.scalatest.{FlatSpec, Matchers}

import scala.language.implicitConversions


class FunctionalitySuite extends FlatSpec with Matchers {

  "Functions" should "or are typically defined on an object" in {
    functionality.math.sin(PI / 2) shouldBe 1.0
  }

  implicit class DoubleOps(x: Double) {
    def sin: Double = java.lang.Math.sin(x)
  }

  "implicit class" should "give us better style" in {
    (PI / 2).sin shouldBe 1.0
  }

  implicit def DoubleOps2(x: Double): DoubleOps2 =
    new DoubleOps2(x)

  class DoubleOps2(x: Double) {
    def sin2: Double = java.lang.Math.sin(x)
  }

  "implicit class" should "be only syntax sugar for implicit conversion" in {
    (PI / 2).sin2 shouldBe 1.0
  }

  "this version" should "avoid allocations" in {
    import ImplicitHolder._
    (PI / 2).sin3 shouldBe 1.0
  }

  "function with my numeric" should "also work" in {
    import tutorial.data_and_functionality.functionality.FunctionsForMyNumeric._
    signOfTheTimes4(12) shouldBe (-144)
  }


  "function with my second numeric" should "also work" in {
    import tutorial.data_and_functionality.functionality.FunctionsForMySecondNumeric._
    import tutorial.data_and_functionality.functionality.MySecondNumericImpl._
    signOfTheTimes(12) shouldBe (-144)
  }
}

object ImplicitHolder {

  implicit final class DoubleOps3(val x: Double) extends AnyVal {
    def sin3: Double = java.lang.Math.sin(x)
  }

}
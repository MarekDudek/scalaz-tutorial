package tutorial.data_and_functionality.functionality

import scala.language.implicitConversions

package object math {

  def sin(x: Double): Double = java.lang.Math.sin(x)
}

trait MyOrdering[T] {

  def compare(x: T, y: T): Int

  def lt(x: T, y: T): Boolean = compare(x, y) < 0

  def gt(x: T, y: T): Boolean = compare(x, y) > 0
}

trait MyNumeric[T] extends MyOrdering[T] {

  def plus(x: T, y: T): T

  def times(x: T, y: T): T

  def negate(x: T): T

  def zero: T

  def abs(x: T): T = if (lt(x, zero)) negate(x) else x
}

object FunctionsForMyNumeric {

  def signOfTheTimes[T](t: T)(implicit N: MyNumeric[T]): T = {
    import N._
    times(negate(abs(t)), t)
  }

  class MyNumericInt(i: Int) extends MyNumeric[Int] {

    override def plus(x: Int, y: Int): Int = x + y

    override def times(x: Int, y: Int): Int = x * y

    override def negate(x: Int): Int = -x

    override def zero: Int = 0

    override def compare(x: Int, y: Int): Int = x - y
  }

  implicit def intToMyNumeric(i: Int): MyNumericInt = new MyNumericInt(i)

}



package tutorial.data_and_functionality.functionality

import simulacrum.op

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


object MyNumeric {

  implicit object MyNumericInt extends MyNumeric[Int] {

    override def plus(x: Int, y: Int): Int = x + y

    override def times(x: Int, y: Int): Int = x * y

    override def negate(x: Int): Int = -x

    override def zero: Int = 0

    override def compare(x: Int, y: Int): Int = x - y
  }

  def apply[T](implicit numeric: MyNumeric[T]): MyNumeric[T] = numeric

  object ops {

    implicit class NumericOps[T](t: T)(implicit N: MyNumeric[T]) {

      def +(o: T): T = N.plus(t, o)

      def *(o: T): T = N.times(t, o)

      def unary_- : T = N.negate(t)

      def abs: T = N.abs(t)
    }

  }

}

object FunctionsForMyNumeric {

  import MyNumeric.ops._

  def signOfTheTimes4[T: MyNumeric](t: T): T = -t.abs * t
}


import simulacrum._

@typeclass trait MySecondOrdering[T] {

  def compare(x: T, y: T): Int

  @op("<") def lt(x: T, y: T): Boolean = compare(x, y) < 0

  @op(">") def gt(x: T, y: T): Boolean = compare(x, y) > 0
}

@typeclass trait MySecondNumeric[T] extends MySecondOrdering[T] {

  @op("+") def plus(x: T, y: T): T

  @op("*") def times(x: T, y: T): T

  @op("unary_-") def negate(x: T): T

  def zero: T

  def abs(x: T): T = if (lt(x, zero)) negate(x) else x
}

object MySecondNumericImpl {

  implicit val MySecondNumericInt: MySecondNumeric[Int] = new MySecondNumeric[Int] {

    def plus(x: Int, y: Int): Int = x + y

    def times(x: Int, y: Int): Int = x * y

    def negate(x: Int): Int = -x

    def zero: Int = 0

    def compare(x: Int, y: Int): Int = java.lang.Integer.compare(x, y)
  }
}

object FunctionsForMySecondNumeric {

  import MySecondNumeric.ops._

  def signOfTheTimes[T: MySecondNumeric](t: T): T = (-t.abs) * t
}

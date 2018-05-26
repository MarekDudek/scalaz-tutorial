package tutorial.for_comprehension

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Incomprehensible extends FunSuite {

  def option: Option[Int] = 2.pure[Option]

  def future: Future[Int] = 3.pure[Future]

  test("assertions") {
    val a = 1
    a should equal(1)
  }

  test("sth") {
    """
    for {
        a <- option
        b <- future
    } yield a * b
    """ shouldNot compile
  }
}
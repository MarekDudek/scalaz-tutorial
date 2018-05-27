package tutorial.for_comprehension

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import scalaz.OptionT
import scalaz.Scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, duration}

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

  def futureOption1: Future[Option[Int]] =
    2.pure[Option].pure[Future]

  def futureOption2: Future[Option[Int]] =
    3.pure[Option].pure[Future]

  test("monad transformers") {
    // when
    val transformer: OptionT[Future, Int] =
      for {
        a <- OptionT(futureOption1)
        b <- OptionT(futureOption2)
      } yield a * b
    val futureMaybeResult: Future[Option[Int]] = transformer.run
    val maybeResult = Await.result(futureMaybeResult, duration.Duration.Inf)
    // then
    assert(maybeResult.contains(6))
    // when
    val futureResult: Future[Int] = transformer getOrElse 0
    val result = Await.result(futureResult, duration.Duration.Inf)
    // then
    assert(result == 6)
    // when
    val futureResult2: Future[Int] = transformer getOrElseF 0.pure[Future]
    val result2 = Await.result(futureResult2, duration.Duration.Inf)
    // then
    assert(result2 == 6)
  }
}
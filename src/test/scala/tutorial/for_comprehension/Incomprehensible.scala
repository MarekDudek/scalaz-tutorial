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

  test("mixing monad transformers with method returning plain outer monad") {
    // when
    val transformer =
      for {
        a <- OptionT(futureOption1)
        b <- OptionT(futureOption2)
        c <- future.liftM[OptionT]
      } yield a * b / c
    val futureResult: Future[Int] = transformer getOrElse 0
    // then
    val result = Await.result(futureResult, duration.Duration.Inf)
    assert(result == 2)
  }

  test("mixing monad transformers with method returning plain inner monad") {
    // when
    val transformer =
      for {
        a <- OptionT(futureOption1)
        b <- OptionT(futureOption2)
        c <- future.liftM[OptionT]
        d <- OptionT(option.pure[Future])
      } yield (a * b) / (c * d)
    val futureResult: Future[Int] = transformer getOrElse 0
    // then
    val result = Await.result(futureResult, duration.Duration.Inf)
    assert(result == 1)
  }

  def liftFutureOption[A](f: Future[Option[A]]): OptionT[Future, A] = OptionT(f)

  def liftFuture[A](f: Future[A]): OptionT[Future, A] = f.liftM[OptionT]

  def liftOption[A](o: Option[A]): OptionT[Future, A] = OptionT(o.pure[Future])

  def lift[A](a: A): OptionT[Future, A] = liftOption(Option(a))

  test("mixing monad transformers with methods returning plain monads with DSL") {
    // given
    val transformer: OptionT[Future, Int] =
      for {
        a <- futureOption1 |> liftFutureOption
        b <- futureOption2 |> liftFutureOption
        c <- future |> liftFuture
        d <- option |> liftOption
        e <- 10 |> lift
      } yield e * (a * b) / (c * d)
    // then
    val futureResult: Future[Int] = transformer getOrElse 0
    // then
    val result = Await.result(futureResult, duration.Duration.Inf)
    assert(result == 10)
  }
}
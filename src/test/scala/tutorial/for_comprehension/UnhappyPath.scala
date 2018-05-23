package tutorial.for_comprehension

import org.scalatest.FunSuite

import scala.concurrent.{Await, Future, duration}

class UnhappyPath extends FunSuite {

  test("unhappy path for Either") {
    // when
    val res = for {
      i <- Right(1)
      j <- Left("a"): Either[String, Int]
      k <- Left("b"): Either[String, Int]
    } yield i + j + k
    // then
    assert(res == Left("a"))
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  test("unhappy path for Future") {
    // when
    val res = for {
      i <- Future.failed[Int](new Throwable)
      j <- Future {
        println("hello")
        1
      }
    } yield i + j
    // then
    assertThrows[Throwable] {
      Await.result(res, duration.Duration.Inf)
    }
  }
}

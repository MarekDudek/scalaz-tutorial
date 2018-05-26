package tutorial.for_comprehension

import org.scalatest.FunSuite
import scalaz.Scalaz._
import scalaz._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class GymnasticsSuite extends FunSuite {

  def getFromRedis(s: String): Option[String] = {
    if (s == "sth") {
      println("getting from Redis 1")
      None
    }
    else {
      println("getting from SQL 1")
      Some(s)
    }
  }

  def getFromSql(s: String): Option[String] = {
    Some(s + s)
  }

  test("fallback to another method") {
    // given
    val key = "sth"
    // when
    val value = getFromRedis(key) orElse getFromSql(key)
    // then
    assert(value contains (key + key))
  }

  def getFromRedis2(s: String): Future[Option[String]] = {
    Future {
      println("getting from Redis 2")
      if (s == "sth")
        None
      else
        Some(s)
    }
  }

  def getFromSql2(s: String): Future[Option[String]] = {
    Future {
      println("getting from SQL 2")
      Some(s + s)
    }
  }

  test("asynchronous fallback to another method, wrong way") {
    // given
    val key = "sth"
    // when
    val future = for {
      cache <- getFromRedis2(key)
      sql <- getFromSql2(key)
    } yield cache orElse sql
    val value: Option[String] = Await.result(future, duration.Duration.Inf)
    // then
    assert(value contains (key + key))
  }

  test("asynchronous fallback to another method, good way") {
    // given
    val key = "sth2"
    // when
    val future: Future[Option[String]] = for {
      cache <- getFromRedis2(key)
      response <- cache match {
        case Some(_) => Future.successful(cache)
        case None => getFromSql2(key)
      }
    } yield response
    val value: Option[String] = Await.result(future, duration.Duration.Inf)
    // then
    println(value)
    //assert(value contains (key + key))
  }

  def getA1: Int = -1

  def getB1: Int = 10

  test("early exit with error, OOP way") {
    val a = getA1
    assertThrows[IllegalArgumentException] {
      require(a > 0, s"$a must be positive")
      val b = a * 10
      assert(b == -10)
    }
  }

  def getA2: Future[Int] = Future {
    -1
  }

  def getB2: Future[Int] = Future {
    10
  }

  def error(msg: String): Future[Nothing] =
    Future.failed(new RuntimeException(msg))

  test("early exit with error, asynch way") {
    // when
    val result = for {
      a <- getA2
      b <- if (a <= 0) error(s"$a must be positive")
      else Future.successful(a)
    } yield b * 10
    // then
    assertThrows[RuntimeException] {
      Await.result(result, duration.Duration.Inf)
    }
  }

  test("early exit with success, OOP way") {
    // when
    val a = getA1
    val result =
      if (a <= 0) 0
      else a * getB1
    // then
    assert(result == 0)
  }

  test("early exit with success, asynch way") {
    // when
    val futureResult = for {
      a <- getA2
      c <- if (a <= 0) Future.successful(0)
      else for {
        b <- getB2
      } yield a * b
    } yield c
    // then
    val result = Await.result(futureResult, duration.Duration.Inf)
    assert(result == 0)
  }


  test("early exit with success, asynch, monadic way") {
    // when
    val futureResult =
      for {
        a <- getA2
        c <- if (a <= 0) 0.pure[Future]
        else
          for {
            b <- getB2
          } yield a * b
      } yield c
    // then
    val result = Await.result(futureResult, duration.Duration.Inf)
    assert(result == 0)
  }
}

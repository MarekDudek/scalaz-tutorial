package tutorial.for_comprehension

import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, duration}

class GymnasticsSuite extends FunSuite {

  def getFromRedis(s: String): Option[String] = {
    if (s == "sth")
      None
    else
      Some(s)
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
      println("getting from Redis")
      if (s == "sth")
        None
      else
        Some(s)
    }
  }

  def getFromSql2(s: String): Future[Option[String]] = {
    Future {
      println("getting from SQL")
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
    val key = "sth"
    // when
    val future: Future[Option[String]] = for {
      cache <- getFromRedis2(key)
      res <- cache match {
        case Some(_) => Future.successful(cache)
        case None => getFromSql2(key)
      }
    } yield res
    val value: Option[String] = Await.result(future, duration.Duration.Inf)
    // then
    assert(value contains (key + key))
  }
}

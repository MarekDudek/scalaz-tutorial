package tutorial.introduction

import scala.concurrent.Future
import scala.language.higherKinds

object AbstractingOverExecution {
}


// two traits for single functionality

trait TerminalSync {

  def read(): String

  def write(t: String): Unit
}

trait TerminalAsynch {

  def read(): Future[String]

  def write(t: String): Future[Unit]
}


// higher-kinded types

trait Foo[C[_]] {
  def create(i: Int): C[Int]
}

object FooList extends Foo[List] {
  def create(i: Int): List[Int] = List(i)
}

// type alias trick

package object Package1 {
  type EitherString[T] = Either[String, T]
  type Id[T] = T
  type Now[X] = X
}

object FooEitherString extends Foo[Package1.EitherString] {
  def create(i: Int): Package1.EitherString[Int] = Right(i)
}

object FooEitherString2 extends Foo[Either[String, ?]] {
  def create(i: Int): Either[String, Int] = Right(i)
}

object FooId extends Foo[Package1.Id] {
  def create(i: Int): Int = i
}

// single train for functionality

trait Terminal[C[_]] {

  def read(): C[String]

  def write(t: String): C[Unit]
}

object TerminalSync extends Terminal[Package1.Now] {

  override def read(): String = ???

  override def write(t: String): Unit = ???
}

object TerminalAsynch extends Terminal[Future] {

  override def read(): Future[String] = ???

  override def write(t: String): Future[Unit] = ???
}

trait Execution[C[_]] {

  def doAndThen[A, B](c: C[A])(f: A => C[B]): C[B]

  def create[B](b: B): C[B]
}

package object Package2 {

  def echo[C[_]](t: Terminal[C], e: Execution[C]): C[String] =
    e.doAndThen(t.read) { in: String =>
      e.doAndThen(t.write(in)) { _: Unit =>
        e.create(in)
      }
    }
}

object Execution {

  implicit class Ops[A, C[_]](c: C[A]) {

    def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] =
      e.doAndThen(c)(f)

    def map[B](f: A => B)(implicit e: Execution[C]): C[B] =
      e.doAndThen(c)(f andThen e.create)
  }


  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    t.read().flatMap { in: String =>
      t.write(in).map { _: Unit =>
        in
      }
    }

  def echo2[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read()
      _ <- t.write(in)
    } yield in
}

package tutorial.introduction

import scala.concurrent.Future
import scala.language.higherKinds

object AbstractingOverExecution {
}

package object SomeObject {
  type EitherString[T] = Either[String, T]
  type Id[T] = T
}


trait TerminalSync {
  def read(): String

  def write(t: String): Unit
}

trait TerminalAsynch {
  def read(): Future[String]

  def write(t: String): Future[Unit]
}

trait Foo[C[_]] {
  def create(i: Int): C[Int]
}

object FooList extends Foo[List] {
  def create(i: Int): List[Int] = List(i)
}


object FooEither extends Foo[SomeObject.EitherString] {
  def create(i: Int): SomeObject.EitherString[Int] = Right(i)
}

object FooId extends Foo[SomeObject.Id] {
  def create(i: Int): Int = i
}


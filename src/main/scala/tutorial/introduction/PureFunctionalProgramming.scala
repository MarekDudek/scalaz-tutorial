package tutorial.introduction


class IO[A](val interpret: () => A) {

  def map[B](f: A => B): IO[B] = IO(f(interpret()))

  def flatMap[B](f: A => IO[B]): IO[B] = IO(f(interpret()).interpret())
}

object IO {

  def apply[A](a: => A): IO[A] = new IO(() => a)
}

object TerminalIO extends Terminal[IO] {

  def read(): IO[String] = IO {
    io.StdIn.readLine
  }

  def write(t: String): IO[Unit] = IO {
    println(t)
  }
}

object PureFunctionalProgramming extends App {
  //val futureEcho: Future[String] = Execution.echo[Future]
  //val delayed: IO[String] = Execution.echo[IO](TerminalIO, Execution)
  //delayed.interpret()
}
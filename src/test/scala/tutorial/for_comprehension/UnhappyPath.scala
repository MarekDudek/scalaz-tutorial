package tutorial.for_comprehension

import org.scalatest.FunSuite

class UnhappyPath extends FunSuite {

  test("unhappy path for option") {
    val v = for {
      i <- Option(1)
    } yield i
    assert(
       v == Option(1)
    )
  }
}

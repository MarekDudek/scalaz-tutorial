package tutorial.for_comprehension

import org.scalatest.FunSuite

class SyntaxSugarSuite extends FunSuite {

  test("for comprehension on options") {
    // given
    val a = Option(1)
    val b = Option(2)
    val c = Option(3)
    // when
    val r = for {
      i <- a
      j <- b
      k <- c
    } yield i + j + k
    // then
    assert(r.contains(6))
  }
}

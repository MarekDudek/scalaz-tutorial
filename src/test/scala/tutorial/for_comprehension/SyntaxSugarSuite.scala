package tutorial.for_comprehension

import org.scalatest.FunSuite

class SyntaxSugarSuite extends FunSuite {

  test("for comprehension on options") {
    // given
    val a = Option(1)
    val b = Option(2)
    val c = Option(3)
    // when
    val r1 = for {
      i <- a
      j <- b
      k <- c
    } yield i + j + k
    // when
    val r2 = a.flatMap {
      i =>
        b.flatMap {
          j =>
            c.map {
              k => i + j + k
            }
        }
    }
    // then
    assert(r1 == r2)
    assert(r1.contains(6))
  }
}
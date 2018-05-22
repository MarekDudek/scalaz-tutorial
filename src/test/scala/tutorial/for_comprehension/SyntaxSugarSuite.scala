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

  test("assignments") {
    // given
    val a = Option(1)
    val b = Option(2)
    val c = Option(3)
    // when
    val r1 = for {
      i <- a
      j <- b
      ij = i + j
      k <- c
    } yield ij + k
    // when
    val r2 = a.flatMap {
      i =>
        b.map {
          j =>
            (j, i + j)
        }.flatMap {
          case (j, ij) =>
            c.map {
              k => ij + k
            }
        }
    }
    // then
    assert(r1 == r2)
    assert(r1.contains(6))
  }

  test("filter in for-comprehension") {
    // given
    val a = Option(1)
    val b = Option(2)
    val c = Option(3)
    // when
    var r1 = for {
      i <- a
      j <- b
      if i < j
      k <- c
    } yield i + j + k
    var r2 = a.flatMap {
      i =>
        b.withFilter {
          j => i < j
        }.flatMap {
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

  test("for-each") {
    // given
    val a = Option(1)
    val b = Option(2)
    // when
    for {
      i <- a
      j <- b
    } println(s"$i-$j")
    // when
    a.foreach {
      i => b.foreach {
        j => println(s"$i-$j")
      }
    }
  }
}

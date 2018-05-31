package tutorial.data_and_functionality

import org.scalatest.{FlatSpec, Matchers}
import scalaz.{IList, INil}

class Data extends FlatSpec with Matchers {

  "List" should "be equivalent to standard" in {
    val list: IList[Int]  =   1 :: 2 :: 3 :: INil()
    list.length shouldBe 3
  }
}

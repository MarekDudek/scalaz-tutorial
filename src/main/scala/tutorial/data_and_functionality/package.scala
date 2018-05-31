package tutorial

package object data_and_functionality {

  // values
  case object A

  type B = String
  type C = Int

  // product
  final case class ABC(a: A.type, b: B, c: C)

  // coproduct
  sealed abstract class XYZ

  case object X extends XYZ

  case object Y extends XYZ

  final case class Z(b: B) extends XYZ

  // some caveats
  final case class UserConfiguration(accepts: Int => Boolean)

  // exhaustivity
  sealed abstract class Foo

  final case class Bar(flag: Boolean) extends Foo

  final object Baz extends Foo

  def thing(foo: Foo): Boolean =
    foo match {
      case Bar(_) => true
      case Baz => false
    }

  // alternative product

  type ABC_alt = (A.type, B, C)

  // alternative coproduct

  type XYZ_alt = Either[X.type, Either[Y.type, Z]]

  type |:[L, R] = Either[L, R]

  type XYZ_alt_2 = X.type |: Y.type |: Z

  val xyz1: XYZ_alt = Left(X)
  val xyz2: XYZ_alt = Right(Left(Y))
  val xyz3: XYZ_alt = Right(Right(Z("str")))

  val xyz4: XYZ_alt_2 = Left(X)
  val xyz5: XYZ_alt_2 = Right(Left(Y))
  val xyz6: XYZ_alt_2 = Right(Right(Z("str")))

  type Accepted = String |: Long |: Boolean

  sealed abstract class Accepted2

  final case class AcceptedString(value: String) extends Accepted2

  final case class AcceptedLong(value: Long) extends Accepted2

  final case class AcceptedBoolean(value: Boolean) extends Accepted2

}



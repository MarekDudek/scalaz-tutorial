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
}



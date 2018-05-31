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

  // convey information

  final case class Person private(name: String, age: Int)

  object Person {
    def apply(name: String, age: C): Either[String, Person] = {
      if (name.nonEmpty && age > 0) Right(new Person(name, age))
      else Left(s"bad input: $name, $age")
    }
  }

  val person: Either[String, Person] = Person("Marek", 42)

  def welcome(person: Person): String =
    s"${person.name} you look wonderfull at ${person.age}"

  val w: Either[String, String] =
    for {
      person <- Person("", -1)
    } yield welcome(person)

  import eu.timepit.refined
  import eu.timepit.refined.collection.NonEmpty
  import eu.timepit.refined.numeric.Positive
  import refined.api.Refined

  final case class Person2
  (
    name: String Refined NonEmpty,
    age: Int Refined Positive
  )

  import refined.refineV

  val name: Either[String, Refined[B, NonEmpty]] = refineV[NonEmpty]("Sam")
  val age: Either[String, Refined[C, Positive]] = refineV[Positive](23)

  import refined.auto._

  val name2: String Refined NonEmpty = "Sam"
  val age2: Int Refined Positive = 23

  val p2: Person2 = Person2(name2, age2)
  val p3: Person2 = Person2("Sam", 23)
  //val p4: Person2 = Person2("", 23)

  import refined.W
  import refined.boolean.And
  import refined.collection.MaxSize

  type Name = NonEmpty And MaxSize[W.`10`.T]
  type Age = Positive And MaxSize[W.`120`.T]

  final case class Person3
  (
    name: String Refined Name,
    age: Int Refined Age
  )
}



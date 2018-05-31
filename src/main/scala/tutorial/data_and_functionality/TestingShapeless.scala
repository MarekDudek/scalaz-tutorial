package tutorial.data_and_functionality

object TestingShapeless {

  final case class Foo
  (
    a: String,
    b: Long
  )

  import shapeless._

  val foo = Foo("some", 23)
  val genericFoo: String :: Long :: HNil = Generic[Foo].to(foo)
  val foo2: Foo = Generic[Foo].from("other" :: 27L :: HNil);

  sealed abstract class Bar
  case object Irish extends Bar
  case object English extends Bar

  val bar: English.type :+: Irish.type :+: CNil = Generic[Bar].to(Irish)
  //Generic[Bar].from(Inl(Inl(Irish)))
}

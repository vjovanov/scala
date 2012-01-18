class Test extends EmbeddedControls {
  type Rep[T]
  def __new[T](args: (String, Rep[T] => Rep[_])*): Rep[T] = error("")
  //tests if it's possible to create two anynomous classes for Struct[Rep]
  //and if they names do not clash
  val foo = new Struct[Rep] {}
  val foo2 = new Struct[Rep] {}
}

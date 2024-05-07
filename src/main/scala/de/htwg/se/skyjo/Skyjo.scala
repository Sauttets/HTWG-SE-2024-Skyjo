import de.htwg.se.skyjo.model._


@main def hello(): Unit =
  println("Hello world!")
  val table = new Table()
  println(table.getPlayerMatricesString())
  println(table.getCardStackString())
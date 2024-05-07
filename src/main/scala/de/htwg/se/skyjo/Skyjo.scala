import de.htwg.se.skyjo.model._
import de.htwg.se.skyjo.controller._
import de.htwg.se.skyjo.aview._


@main def main(): Unit =
  var table = new PlayerTable()
  //println(table.getPlayerMatricesString())
  //println(table.getCardStackString())
  //table = table.drawFromStack()
  //print(table.getTableString())
  /*
  var tableController = new TableController(table)
  tableController.drawFromStack()
  print(tableController.table.getTableString())

  var move = new Move(true, true, 0, 0)
  tableController.doMove(move)
  print(tableController.table.getTableString())


  move = new Move(true, false, 0, 1)
  tableController.doMove(move)
  print(tableController.table.getTableString())
  */

  var tableController = new TableController(table)
  var tui = new TUI(tableController)
  tui.run
import de.htwg.se.skyjo.model._
import de.htwg.se.skyjo.controller._
import de.htwg.se.skyjo.aview._


@main def main(): Unit =
  var table = new PlayerTable()
  var tableController = new TableController(table)
  var tui = new TUI(tableController)
  tui.run


import de.htwg.se.skyjo.model.PlayerTable
import de.htwg.se.skyjo.controller.TableController
import de.htwg.se.skyjo.aview.TUI


@main def main(): Unit =
  var table = new PlayerTable()
  var tableController = new TableController(table)
  var tui = new TUI(tableController)
  tui.run


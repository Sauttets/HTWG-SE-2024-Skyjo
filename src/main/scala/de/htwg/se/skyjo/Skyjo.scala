package de.htwg.se.skyjo

import de.htwg.se.skyjo.model.modelComponent._
import de.htwg.se.skyjo.controller.TableController
import de.htwg.se.skyjo.aview.{TUI, GUI}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.swing.Swing

@main def main(): Unit = {
  val table = new PlayerTable(4,4,3)
  val tableController = new TableController(table)
  
  // Initialize TUI
  val tui = new TUI(tableController)

  // Initialize GUI in a separate Future to run concurrently
  Future {
    Swing.onEDT {
      val gui = new GUI(tableController)
      gui.visible = true
    }
  }

  // Start TUI
  tui.run
}

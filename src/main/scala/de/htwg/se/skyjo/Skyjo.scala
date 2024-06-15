package de.htwg.se.skyjo

import de.htwg.se.skyjo.model.modelComponent.modelImplementation._
import de.htwg.se.skyjo.aview.{TUI, GUI}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.swing.Swing
import controller.controllerComponent.controllerimplementation.TableController

@main def main(): Unit = {
  val table = new PlayerTable(2,4,4)
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

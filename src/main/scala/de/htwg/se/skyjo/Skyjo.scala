package de.htwg.se.skyjo

import com.google.inject.Guice
import de.htwg.se.skyjo.aview.{TUI, GUI}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.swing.Swing
import de.htwg.se.skyjo.controller.controllerComponent.ControllerInterface

@main def main(): Unit = {
  val injector = Guice.createInjector(new SkyjoModule)
  val controller = injector.getInstance(classOf[ControllerInterface])

  // Initialize TUI
  val tui = new TUI(controller)

  // Initialize GUI in a separate Future to run concurrently
  Future {
    Swing.onEDT {
      val gui = new GUI(controller)
      gui.visible = true
    }
  }

  // Start TUI
  tui.run
}

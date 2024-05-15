package de.htwg.se.skyjo.controller

case class DrawFromStackCommand(tableController: TableController) extends Command {
  override def execute(): Unit = {
    tableController.drawFromStack()
  }
}

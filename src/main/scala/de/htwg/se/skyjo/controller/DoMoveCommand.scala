package de.htwg.se.skyjo.controller
import de.htwg.se.skyjo.model.Move

case class DoMoveCommand(tableController: TableController, move: Move) extends Command {
  override def execute(): Unit = {
    tableController.doMove(move)
  }
}

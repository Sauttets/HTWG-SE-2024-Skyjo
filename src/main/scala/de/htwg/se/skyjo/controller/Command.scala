package de.htwg.se.skyjo
package controller

import model._
import util._

class MoveCommand(controller: TableController, move: Move) extends Command:
  private var previousState: PlayerTable = _

  override def execute(): Unit =
    previousState = controller.table.copy()
    controller.executeMove(move)
  
  override def undo(): Unit =
    controller.table = previousState
    controller.notifyObservers
  
  override def redo(): Unit =
    execute()

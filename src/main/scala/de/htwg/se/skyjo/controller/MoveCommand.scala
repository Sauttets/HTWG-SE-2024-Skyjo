package de.htwg.se.skyjo
package controller

import model._
import util._

class MoveCommand(controller: TableController, move: Move) extends Command:
  private var previousState: Option[PlayerTable] = None

  override def execute(): Unit =
    previousState = Some(controller.table.copy())
    controller.executeMove(move)
    controller.notifyObservers
  
  override def undo(): Unit =
    previousState match
      case Some(s) =>
        controller.table = s.copy(s.Tabletop, s.cardstack.closeStackTop(), s.playerCount, s.currentPlayer)
        controller.notifyObservers
      case None => 
  
  override def redo(): Unit =
    execute()

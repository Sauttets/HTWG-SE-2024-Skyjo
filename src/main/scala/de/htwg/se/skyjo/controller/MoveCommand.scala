package de.htwg.se.skyjo
package controller

import model._
import util._

class MoveCommand(controller: TableController, move: Move) extends Command:
  private var previousState: Option[PlayerTable] = None

  override def execute(): Unit =
    previousState = Some(controller.table.copy())
    controller.executeMove(move)
    controller.notifyObservers()
  
  override def undo(): Unit =
    previousState.foreach { state =>
      controller.table = state
      controller.notifyObservers()
    }
  
  override def redo(): Unit =
    execute()

package de.htwg.se.skyjo
package controller.controllerComponent.controllerimplementation

import model.modelComponent._
import util._

class MoveCommand(controller: TableController, move: Move) extends Command:
  private var previousState: Option[ModelInterface] = None

  override def execute(): Unit =
    previousState = Some(controller.table)
    controller.executeMove(move)
    controller.notifyObservers
  
  override def undo(): Unit =
    previousState match
      case Some(s) =>
        controller.table=s
        controller.notifyObservers
      case None => print("no previous state")
  
  override def redo(): Unit =
    execute()

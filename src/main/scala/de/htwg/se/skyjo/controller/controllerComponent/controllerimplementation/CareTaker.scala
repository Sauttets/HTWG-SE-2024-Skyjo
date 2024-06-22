package de.htwg.se.skyjo
package controller.controllerComponent.controllerimplementation

import scala.collection.mutable.Stack
import util.Memento

class CareTaker {
  private val undoStack: Stack[Memento] = Stack()
  private val redoStack: Stack[Memento] = Stack()

  def undo(current:Memento): Option[Memento] = {
  if undoStack.nonEmpty then
    val snapshot = undoStack.pop()
    redoStack.push(current)
    Some(snapshot)
  else 
    None
  }

  def redo(current:Memento): Option[Memento] = {
    if redoStack.nonEmpty then
      val snapshot = redoStack.pop()
      undoStack.push(current)
      Some(snapshot)
    else 
      None
  }
  def save(snapshot:Memento):Unit = {
    undoStack.push(snapshot)
    redoStack.clear()
  }
}

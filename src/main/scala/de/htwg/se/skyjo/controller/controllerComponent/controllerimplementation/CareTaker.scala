package de.htwg.se.skyjo
package controller.controllerComponent.controllerimplementation

import scala.collection.mutable.Stack
import util.Memento

class CareTaker {
  private val undoStack: Stack[Memento] = Stack()
  private val redoStack: Stack[Memento] = Stack()

  def undo(): Unit = {
  if undoStack.nonEmpty then
    val snapshot = undoStack.pop()
    snapshot.state.undo()
    redoStack.push(snapshot)
  }

  def redo(): Unit = {
    if redoStack.nonEmpty then
      val snapshot = redoStack.pop()
      snapshot.state.redo()
      undoStack.push(snapshot)
  }
  def save(snapshot:Memento):Unit = {
    undoStack.push(snapshot)
    redoStack.clear()
  }
}

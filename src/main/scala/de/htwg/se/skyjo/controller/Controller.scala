package de.htwg.se.skyjo
package controller

import model._
import util._

import scala.collection.mutable.Stack

case class TableController(var table: PlayerTable) extends Observable:
  
  private val undoStack: Stack[Command] = Stack()
  private val redoStack: Stack[Command] = Stack()

  def drawFromStack(): Unit = {
    table = table.drawFromStack()
    notifyObservers
  }

  def doMove(move: Move): Unit = {
    val command = new MoveCommand(this, move)
    command.execute()
    undoStack.push(command)
    redoStack.clear()
  }

  def executeMove(move: Move): Unit = {
    val handCard = if move.drawnFromStack then table.cardstack.getStackCard() else table.cardstack.getTrashCard()
    if move.swapped then
      val tupel = table.Tabletop(table.currentPlayer).changeCard(move.row, move.col, handCard)
      table = table.updateMatrix(table.currentPlayer, tupel(0))
      table = table.updateCardstack(tupel(1), move.drawnFromStack)
    else
      table = table.updateMatrix(table.currentPlayer, table.Tabletop(table.currentPlayer).flipCard(move.row, move.col))
      table = table.updateCardstack(handCard, move.drawnFromStack)
    table = table.copy(currentPlayer = (table.currentPlayer + 1) % table.playerCount)
  }

  def undo(): Unit = {
    if undoStack.nonEmpty then
      val command = undoStack.pop()
      command.undo()
      redoStack.push(command)
  }

  def redo(): Unit = {
    if redoStack.nonEmpty then
      val command = redoStack.pop()
      command.redo()
      undoStack.push(command)
  }

  def gameEnd(): Boolean = {
    table.Tabletop.exists(_.checkFinished())
  }

  override def toString = table.getTableString()

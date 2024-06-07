package de.htwg.se.skyjo
package controller

import model._
import util._


case class TableController(var table: PlayerTable) extends Observable:
  
  val careTaker=new CareTaker()

  def drawFromStack(): Unit = {
    table = table.drawFromStack()
    notifyObservers
  }

    def drawFromTrash(): Unit = {
    table = table.drawFromTrash()
    notifyObservers
  }



  def doMove(move: Move): Unit = {
    val command = new MoveCommand(this, move)
    command.execute()
    careTaker.save(Memento(command))
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


  def gameEnd(): Boolean = {
    table.Tabletop.exists(_.checkFinished())
  }

  override def toString = table.getTableString()

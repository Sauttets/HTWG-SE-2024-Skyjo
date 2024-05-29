package de.htwg.se.skyjo
package controller

import model.*
import util.Command

class PutCommand(move: Move) extends Command[PlayerTable]:
  override def doStep(table: PlayerTable): PlayerTable = {
    val handCard = if move.drawnFromStack then table.cardstack.getStackCard() else table.cardstack.getTrashCard()
    var result=table
    if move.swapped then
        val tupel = result.Tabletop(result.currentPlayer).changeCard(move.row, move.col, handCard)
        result = result.updateMatrix(result.currentPlayer, tupel(0))
        result = result.updateCardstack(tupel(1), move.drawnFromStack)
    else
        result = result.updateMatrix(result.currentPlayer, result.Tabletop(result.currentPlayer).flipCard(move.row, move.col))
        result = result.updateCardstack(handCard, move.drawnFromStack)
    result.copy(currentPlayer = (result.currentPlayer + 1) % result.playerCount)
  }
  override def undoStep(table: PlayerTable): PlayerTable = table
  override def redoStep(table: PlayerTable): PlayerTable = table
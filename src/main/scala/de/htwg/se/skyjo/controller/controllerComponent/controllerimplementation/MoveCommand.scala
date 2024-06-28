package de.htwg.se.skyjo
package controller.controllerComponent.controllerimplementation

import model.modelComponent._
import util._

class MoveCommand(var state: ModelInterface, move: Move) extends Command:
  override def execute(): Memento =
    val handCard = if (move.drawnFromStack) state.getStackCard() else state.getTrashCard()
    if (move.swapped) {
      val tupel = state.swapCard(state.currentPlayer, move.row, move.col, handCard)
      state = tupel(0)
      state = state.discardCard(tupel(1), move.drawnFromStack)
    } else {
      state = state.flipCard(state.currentPlayer, move.row, move.col)
      state = state.discardCard(handCard, move.drawnFromStack)
    }
    Memento(state.nextPlayer())

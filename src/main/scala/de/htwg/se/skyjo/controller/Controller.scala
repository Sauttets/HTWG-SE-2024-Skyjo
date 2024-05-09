

package de.htwg.se.skyjo
package controller

import model._
import util._



case class TableController(var table: PlayerTable) extends Observable:
    
    def drawFromStack(): Unit = {
        table = table.drawFromStack()
        notifyObservers
    }

    def doMove(move: Move): Unit = {
        val handCard = if move.drawnFromStack then table.cardstack.stackCard else table.cardstack.trashCard
        if move.swapped then
            val tupel = table.Tabletop(table.currentPlayer).changeCard(move.row, move.col, handCard)
            table = table.updateMatrix(table.currentPlayer, tupel(0))
            table = table.updateCardstack(tupel(1), move.drawnFromStack)
        else
            table = table.updateMatrix(table.currentPlayer, table.Tabletop(table.currentPlayer).flipCard(move.row, move.col))
            table = table.updateCardstack(handCard, move.drawnFromStack)
        table = table.copy(currentPlayer = (table.currentPlayer + 1) % table.playerCount)
        notifyObservers
    }
<<<<<<< HEAD
    def checkGameEnd():Boolean={
        import scala.util.boundary, boundary.break
        boundary:
            table.Tabletop.foreach(x=>if x.checkFinished() then break(true))
            false
=======
    def gameEnd(): Boolean = {
        table.Tabletop.exists(_.checkFinished())
>>>>>>> f4c0123aad5e65b4796923a668db60aa55140f03
    }
    override def toString = table.getTableString() 
    /* 
    
        takeReplaceAndThrow
            takeCardFromFresh || takeCardFromTrash
            replaceCard
            throwCard
            setNextPlayer

        takeThrowAndTurn
            takeCardFromFresh
            throwCard
            turnCard
            setNextPlayer

        checkremoveColumn
            checkColumn
                removeColumn    
            
        calculateWinner
            checkFinished
            getPlayerPoints
            
     */
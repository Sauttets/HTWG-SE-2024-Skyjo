

package de.htwg.se.skyjo
package controller

import model._
import util._


case class TableController(var table: PlayerTable) extends Observable:
    val undoManager = new UndoManager[PlayerTable]    

    def drawFromStack(): Unit = {
        table = table.drawFromStack()
        notifyObservers
    }

    def setCardStackStrategy(strat:CardStackStrategy)=table=table.setCardStackStrategy(strat)

    def doMove(move: Move): Unit = {
        table = undoManager.doStep(table, PutCommand(move))
        notifyObservers
    }
    def undo: PlayerTable = undoManager.undoStep(table)
    def redo: PlayerTable = undoManager.redoStep(table)
    def gameEnd(): Boolean = {
        table.Tabletop.exists(_.checkFinished())
    }
    override def toString = table.getTableString() 
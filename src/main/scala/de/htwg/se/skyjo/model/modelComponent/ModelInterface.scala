package de.htwg.se.skyjo
package model.modelComponent

import de.htwg.se.skyjo.model.modelComponent._
import util.CardStackStrategy
import modelImplementation._

trait ModelInterface{
    def currentPlayer:Int
    def playerCount:Int
    def Tabletop:List[PlayerMatrix]
    def setCardStackStrategy(strat:CardStackStrategy): ModelInterface
    def getCardStackString(): String
    def getPlayerMatricesString(): String
    def getCurrenPlayerString(): String
    def getPlayerString(player:Int): String
    def getTableString(): String
    def closeStackTop():ModelInterface
    def drawFromStack(): ModelInterface
    def drawFromTrash(): ModelInterface
    def getScores(): List[(Int, Int)]
    def swapCard(player: Int, row:Int,col:Int,card:CardInterface): (ModelInterface,CardInterface)
    def flipCard(player: Int, row:Int,col:Int): (ModelInterface)
    def updateCardstack(card: CardInterface, drawnFromStack: Boolean): ModelInterface
    def nextPlayer():ModelInterface
    def getStackCard():CardInterface
    def getTrashCard():CardInterface
    def gameEnd():Boolean
    def getParitys():List[(Int,Int)]
    def openAll():ModelInterface
}
    

trait CardInterface:
    def value: Int
    def opened: Boolean
    def open(): CardInterface
    def close(): CardInterface
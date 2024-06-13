package htwg.se.skyjo.model.modelComponent

import htwg.se.skyjo.util.CardStackStrategy
import htwg.se.skyjo.model.modelComponent._

trait ModelInterface{
    def setCardStackStrategy(strat:CardStackStrategy): ModelInterface
    def getCardStackString(): String
    def getPlayerMatricesString(): String
    def getCurrenPlayerString(): String
    def getPlayerString(player:Int): String
    def getTableString(): String
    def drawFromStack(): ModelInterface
    def drawFromTrash(): ModelInterface
    def getScores(): List[(Int, Int)]
    def updateMatrix(player: Int, matrix: PlayerMatrix): ModelInterface
    def updateCardstack(card: CardInterface, drawnFromStack: Boolean): ModelInterface
}
    

trait CardInterface:
    def open(): CardInterface
    def close(): CardInterface
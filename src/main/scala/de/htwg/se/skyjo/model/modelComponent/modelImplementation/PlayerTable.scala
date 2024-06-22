package de.htwg.se.skyjo.model.modelComponent.modelImplementation

import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import de.htwg.se.skyjo.util.CardStackStrategy
import de.htwg.se.skyjo.model.modelComponent._
import scala.collection.mutable.ListBuffer

@Singleton
case class PlayerTable @Inject() (@Named("DefaultPlayerCount") playerCount: Int,
                                  @Named("DefaultWidth") width: Int,
                                  @Named("DefaultHeight") height: Int,
                                  @Named("DefaultCurrentPlayer") currentPlayer: Int,
                                  @Named("CardStackStrategy") cardstack: CardStackStrategy,
                                  @Named("Tabletop") Tabletop: List[PlayerMatrix]) extends ModelInterface {

  def setCardStackStrategy(strat: CardStackStrategy): PlayerTable = copy(cardstack = strat)

  private def padValue(card: CardInterface): String = {
    if (card.opened) {
      if (card.value >= 0 && card.value < 10) {
        "│ 0" + card.value + " "
      } else {
        "│ " + card.value.toString + " "
      }
    } else {
      "│ xx "
    }
  }

  def getCardStackString(): String = {
    s"""\nCurrent card stack:
       |  ┌────┐  ┌────┐
       | ┌${padValue(cardstack.getStackCard())}│  ${padValue(cardstack.getTrashCard())}│
       | │└───┬┘  └────┘
       | └────┘""".stripMargin
  }

  def getPlayerMatricesString(): String = {
    val colors = Array("\u001B[31m", "\u001B[32m", "\u001B[35m", "\u001B[36m", "\u001B[33m")
    val str = Tabletop.zipWithIndex.map { case (playerMatrix, index) =>
      val color = colors(index % colors.length)
      s"""${color}\nPlayer ${(index + 1)}:
         |${"+----" * playerMatrix.rows.head.length}+
         |${playerMatrix.rows.map { row =>
        row.map { card =>
          padValue(card)
        }.mkString("") + ("│\n" + "+----" * (row.length))
      }.mkString("+\n")}""".stripMargin
    }.mkString("+\n")
    str + "+\u001B[0m" // Reset color
  }

  def getCurrenPlayerString(): String = getPlayerString(currentPlayer)

  def getPlayerString(player: Int): String = {
    val colors = Array("\u001B[31m", "\u001B[32m", "\u001B[35m", "\u001B[36m", "\u001B[33m")
    colors(player % colors.length) + "Player " + (player + 1) + "\u001B[0m"
  }

  def getTableString(): String = {
    getPlayerMatricesString() + getCardStackString()
  }
  def closeStackTop():ModelInterface={
    copy(cardstack = cardstack.closeStackTop())
  }
  def drawFromStack(): PlayerTable = {
    copy(cardstack = cardstack.openStackTop())
  }

  def drawFromTrash(): PlayerTable = {
    copy(cardstack = cardstack)
  }

  def getScores(): List[(Int, Int)] = {
    Tabletop.map(matrix => matrix.getScore()).zipWithIndex
  }

  def swapCard(player: Int, row: Int, col: Int, card: CardInterface): (PlayerTable, CardInterface) = {
    val tupel = Tabletop(player).changeCard(row, col, card)
    (copy(Tabletop = Tabletop.updated(player, tupel._1)), tupel._2)
  }

  def flipCard(player: Int, row: Int, col: Int): PlayerTable = {
    copy(Tabletop = Tabletop.updated(player, Tabletop(player).flipCard(row, col)))
  }

  def updateCardstack(card: CardInterface, drawFromStack: Boolean): PlayerTable = {
    if (drawFromStack) copy(cardstack = cardstack.discard(card).removeStackTop())
    else copy(cardstack = cardstack.removeTrashTop().discard(card))
  }

  def nextPlayer(): PlayerTable = {
    copy(currentPlayer = (currentPlayer + 1) % playerCount)
  }

  def getStackCard(): CardInterface = cardstack.getStackCard()
  def getTrashCard(): CardInterface = cardstack.getTrashCard()

  def gameEnd(): Boolean = Tabletop.exists(_.checkFinished())

  def getParitys(): List[(Int, Int)] = {
    val paritys = ListBuffer[(Int, Int)]()
    Tabletop.zipWithIndex.foreach { case (m, player) =>
      m.rows.zipWithIndex.foreach { case (row, idx) =>
        if (row.forall(card => card.value == row.head.value)) paritys += ((player, idx))
      }
    }
    paritys.toList
  }

  def openAll(): PlayerTable = {
    copy(Tabletop = Tabletop.map(mat => PlayerMatrix(mat.rows.map(row => row.map(c => c.open())))))
  }
}

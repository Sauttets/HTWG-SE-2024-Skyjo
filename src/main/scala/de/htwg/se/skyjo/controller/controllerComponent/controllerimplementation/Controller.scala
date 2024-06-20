package de.htwg.se.skyjo
package controller.controllerComponent.controllerimplementation

import com.google.inject.name.Names
import com.google.inject.{Guice, Inject}
import net.codingwell.scalaguice.InjectorExtensions._

import model.modelComponent._
import model.modelComponent.modelImplementation._
import util._
import util.Memento
import util.Move
import de.htwg.se.skyjo.controller.controllerComponent.ControllerInterface


case class TableController @Inject() (var table: ModelInterface) extends Observable, ControllerInterface:
  
  val injector = Guice.createInjector(new SudokuModule)
  val careTaker=new CareTaker()

  def createPlayerTable: Unit = {
    table = injector.instance[ModelInterface](Names.named("tiny"))
    notifyObservers
  }

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
    val handCard = if move.drawnFromStack then table.getStackCard() else table.getTrashCard()
    if move.swapped then
      val tupel = table.swapCard(table.currentPlayer,move.row,move.col,handCard)
      table = tupel(0)
      table = table.updateCardstack(tupel(1), move.drawnFromStack)
    else
      table = table.flipCard(table.currentPlayer,move.row,move.col)
      table = table.updateCardstack(handCard, move.drawnFromStack)
    table = table.nextPlayer()
  }


  def gameEnd()=table.gameEnd()
  

  override def toString = table.getTableString()

  def getPlayerString(player: Int): String = table.getPlayerString(player)

  def getScores(): List[(Int, Int)] = table.getScores()

  def getCurrenPlayerString(): String = table.getCurrenPlayerString()

  def getCurrenPlayer(): Int = table.currentPlayer

  def getPLayerCount(): Int = table.playerCount

  def getStackCard(): CardInterface = table.getStackCard()

  def getTrashCard(): CardInterface = table.getTrashCard()

  def getTabletop(): List[PlayerMatrix] = table.Tabletop

  def undo()=careTaker.undo()
  def redo()=careTaker.redo()

  def reset()={table=table.reset()}

  def getParitys()=table.getParitys()

  def openAll()={table=table.openAll();notifyObservers}
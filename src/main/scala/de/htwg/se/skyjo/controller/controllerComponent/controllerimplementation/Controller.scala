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

class TableController @Inject()(var table: ModelInterface) extends Observable, ControllerInterface {

  val careTaker = new CareTaker()
  val injector= Guice.createInjector(new SkyjoModule)

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
    val command = new MoveCommand(this.table, move)
    careTaker.save(Memento(table))
    table=command.execute().state
    notifyObservers
  }

  def gameEnd() = table.gameEnd()
  
  override def toString = table.getTableString()

  def getPlayerString(player: Int): String = table.getPlayerString(player)

  def getScores(): List[(Int, Int)] = table.getScores()

  def getCurrenPlayerString(): String = table.getCurrenPlayerString()

  def getCurrenPlayer(): Int = table.currentPlayer

  def getPLayerCount(): Int = table.playerCount

  def getStackCard(): CardInterface = table.getStackCard()

  def getTrashCard(): CardInterface = table.getTrashCard()

  def getTabletop(): List[PlayerMatrix] = table.Tabletop

  def undo() =
    careTaker.undo(Memento(table)) match{
      case Some(m)=>table=m.state.closeStackTop()
      case None => 
    }
    notifyObservers
  def redo() =
    careTaker.redo(Memento(table)) match{
      case Some(m)=>table=m.state
      case None => 
    }
    notifyObservers

  def reset() = { table = injector.getInstance(classOf[ModelInterface])}

  def getParitys() = table.getParitys()

  def openAll() = { table = table.openAll(); notifyObservers }
}

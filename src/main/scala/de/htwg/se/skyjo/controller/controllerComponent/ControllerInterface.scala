package de.htwg.se.skyjo
package controller.controllerComponent

import util.Move
import model.modelComponent.CardInterface
import model.modelComponent.modelImplementation._
import de.htwg.se.skyjo.util.Observable
import de.htwg.se.skyjo.model.modelComponent.ModelInterface

trait ControllerInterface extends Observable{
  def table:ModelInterface
  def drawFromStack(): Unit
  def drawFromTrash(): Unit
  def doMove(move: Move): Unit
  def executeMove(move: Move): Unit
  def gameEnd():Boolean
  def toString :String
  def getPlayerString(player: Int): String
  def getScores(): List[(Int, Int)]
  def getCurrenPlayerString(): String
  def getCurrenPlayer(): Int
  def getPLayerCount(): Int
  def getStackCard(): CardInterface
  def getTrashCard(): CardInterface
  def getTabletop(): List[PlayerMatrix]
  def undo():Unit
  def redo():Unit
  def reset():Unit
  def getParitys():List[(Int,Int)]
  def openAll():Unit
}

package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.model._

trait CardStackStrategy {
  def openStackTop() :CardStackStrategy
  def closeStackTop():CardStackStrategy
  def removeTrashTop(): CardStackStrategy
  def discard(card: Card): CardStackStrategy 
  def removeStackTop(): CardStackStrategy
  def getStackCard(): Card
  def getTrashCard(): Card
}

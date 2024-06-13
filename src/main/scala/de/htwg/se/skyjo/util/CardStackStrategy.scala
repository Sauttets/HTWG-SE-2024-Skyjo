package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.model._

trait CardStackStrategy {
  def openStackTop() :CardStackStrategy
  def closeStackTop():CardStackStrategy
  def removeTrashTop(): CardStackStrategy
  def discard(card: CardInterface): CardStackStrategy 
  def removeStackTop(): CardStackStrategy
  def getStackCard(): CardInterface
  def getTrashCard(): Card
}

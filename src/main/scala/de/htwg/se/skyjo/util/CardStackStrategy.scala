package de.htwg.se.skyjo
package util
import model.modelComponent._

trait CardStackStrategy {
  def openStackTop() :CardStackStrategy
  def closeStackTop():CardStackStrategy
  def removeTrashTop(): CardStackStrategy
  def discard(card: CardInterface): CardStackStrategy 
  def removeStackTop(): CardStackStrategy
  def getStackCard(): CardInterface
  def getTrashCard(): CardInterface
}

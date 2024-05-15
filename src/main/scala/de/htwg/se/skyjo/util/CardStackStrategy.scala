package de.htwg.se.skyjo.util
import de.htwg.se.skyjo.model.Card

trait CardStackStrategy {
  def drawFromStack() :(CardStackStrategy)
  def drawFromTrash(): (Card, CardStackStrategy) 
  def discard(card: Card): CardStackStrategy 
  def newStackCard(): CardStackStrategy
  def getStackCard(): Card
  def getTrashCard(): Card
}

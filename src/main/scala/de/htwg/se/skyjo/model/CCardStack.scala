package de.htwg.se.skyjo.model
import scala.util.Random
import de.htwg.se.skyjo.util.CardStackStrategy

case class CCardStack(stackCard: Card, trashCard: Card) extends CardStackStrategy:
    def this() = this(new Card(), new Card(true))    
    override def drawFromStack() = {
        (copy(new Card(stackCard.value, true), trashCard)) 
    }
    override def drawFromTrash(): (Card, CCardStack) = (trashCard, copy(stackCard, Card(1, opened = true))) //1 needs to be fixed to a random number
    override def discard(card: Card): CCardStack = copy(stackCard, new Card(card.value, opened = true))
    override def newStackCard(): CCardStack = copy(new Card(), trashCard)
    override def getStackCard()=stackCard
    override def getTrashCard()=trashCard

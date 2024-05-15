package de.htwg.se.skyjo.model
import scala.util.Random
import de.htwg.se.skyjo.util.CardStackStrategy

case class CCardStack(stackCard: Card, trashCard: Card) extends CardStackStrategy:
    def this() = this(CardBuilder().build(), CardBuilder().opened(true).build())    
    override def drawFromStack() = {
        (copy(CardBuilder().value(stackCard.value).opened(true).build(), trashCard)) 
    }
    override def drawFromTrash(): CCardStack = copy(stackCard, Card(1, opened = true)) //1 needs to be fixed to a random number
    override def discard(card: Card): CCardStack = copy(stackCard, CardBuilder().value(card.value).opened(true).build())
    override def newStackCard(): CCardStack = copy(CardBuilder().build(), trashCard)
    override def getStackCard()=stackCard
    override def getTrashCard()=trashCard

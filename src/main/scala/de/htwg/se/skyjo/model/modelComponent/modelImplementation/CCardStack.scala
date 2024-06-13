package de.htwg.se.skyjo.model.modelComponent.modelImplementation
import de.htwg.se.skyjo.util.CardStackStrategy

case class CCardStack(stackCard: Card, trashCard: Card) extends CardStackStrategy:
    def this() = this(CardBuilder().build(), CardBuilder().opened(true).build())    
    override def openStackTop() = copy(stackCard.open(), trashCard)
    override def closeStackTop()=copy(stackCard.close(),trashCard)
    override def removeTrashTop(): CCardStack = copy(stackCard, Card(1, opened = true)) //1 needs to be fixed to a random number
    override def discard(card: Card): CCardStack = copy(stackCard, CardBuilder().value(card.value).opened(true).build())
    override def removeStackTop(): CCardStack = copy(CardBuilder().build(), trashCard)
    override def getStackCard()=stackCard
    override def getTrashCard()=trashCard

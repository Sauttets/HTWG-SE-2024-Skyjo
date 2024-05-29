package de.htwg.se.skyjo.model
import scala.util.Random
import de.htwg.se.skyjo.util.CardStackStrategy
import de.htwg

case class LCardStack(stack: List[Card], trashstack: List[Card]) extends CardStackStrategy:
    def this(stackList:List[Card])  =   this(stackList,List(CardBuilder().opened(true).build()))
    def this() = this(LCardStack.simulatedCards())  
    override def flipStackTop() = (copy(stack.updated(0,stack(0).flip()), trashstack)) 
    override def removeTrashTop(): LCardStack = copy(stack, trashstack.dropRight(1)) 
    override def discard(card: Card): LCardStack = copy(stack, trashstack:+card.flip())
    override def removeStackTop(): LCardStack = copy(if(stack.size>0) stack.drop(1) else stack, trashstack)
    override def getStackCard()=stack(0)
    override def getTrashCard()=trashstack.last

object LCardStack:
    def simulatedCards():List[Card]={
        val cardCounts=List(5,10,15,10,10,10,10,10,10,10,10,10,10,10,10)
        val cardValues=cardCounts.zipWithIndex.map((value,ind)=>List.tabulate(value)(x=>CardBuilder().value(ind-2).build())).flatten    
        scala.util.Random.shuffle(cardValues)
    }
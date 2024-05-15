package de.htwg.se.skyjo.model
import scala.util.Random
import de.htwg.se.skyjo.util.CardStackStrategy

case class LCardStack(stack: List[Card], trashstack: List[Card]) extends CardStackStrategy:
    def this(stacksize:Int=100) = {this(List.tabulate(stacksize) { _ =>new Card()},List(new Card()))}   
    override def drawFromStack() = {
        (copy(stack.updated(0,stack(0).flip()), trashstack)) 
    }
    override def drawFromTrash(): (Card, LCardStack) = (getTrashCard(), copy(stack, trashstack.dropRight(1))) 
    override def discard(card: Card): LCardStack = copy(stack, trashstack:+card.flip())
    override def newStackCard(): LCardStack = copy(if(stack.size>0) stack.drop(1) else stack, trashstack)
    override def getStackCard()=stack(0)
    override def getTrashCard()=trashstack.last

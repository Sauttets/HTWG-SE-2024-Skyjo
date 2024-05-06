package de.htwg.se.skyjo.model
import scala.util.Random


case class Cardstack(stackCard: Card, trashCard: Card):
    def this() = this(new Card(), new Card())    
    def drawFromStack(): (Card, Cardstack) = {
        (stackCard, copy(new Card(), trashCard)) 
    }
    def drawFromTrash(): (Card, Cardstack) = (trashCard, copy(stackCard, Card(1, opened = true))) //1 needs to be fixed to a random number
    def discard(card: Card): Cardstack = copy(stackCard, new Card(card, opened = true))
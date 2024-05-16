package de.htwg.se.skyjo.model
import scala.util.Random

case class Card(value: Int, opened: Boolean = false) {
  def flip() = copy(value = this.value, opened = true)
}

object CardBuilder {
  def apply() = {new CardBuilder()}
}

case class CardBuilder(value:Int,opened:Boolean,simulate:Boolean) {
  def this()={
    this(Random.nextInt(15)-2,false,true)
    // this(CardBuilder.shuffledValues(0),false,true)
  }
  def value(value: Int): CardBuilder = {
    if( value>12 || value < -2) then throw new java.lang.IndexOutOfBoundsException
    copy(value,this.opened,false)
  }

  def opened(open: Boolean): CardBuilder = {
    copy(this.value,open,this.simulate)
  }

  def build(): Card = {
    // if(this.simulate){
    //     val card=Card(CardBuilder.shuffledValues(0),false)
    //     CardBuilder.shuffledValues=CardBuilder.shuffledValues.drop(1)
    //     card
    // }
    // else
      Card(value, opened)
  }
}

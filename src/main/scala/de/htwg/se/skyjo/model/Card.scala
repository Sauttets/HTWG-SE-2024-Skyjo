package de.htwg.se.skyjo.model
import scala.util.Random

case class Card(value: Int, opened: Boolean = false) {
  def open() = copy(value, true)
  def close()= copy(value,false)
}

object CardBuilder {
  def apply() = {new CardBuilder()}
}

case class CardBuilder(value:Int,opened:Boolean) {
  def this()={
    this(Random.nextInt(15)-2,false)
  }
  def value(value: Int): CardBuilder = {
    if( value>12 || value < -2) then throw new java.lang.IndexOutOfBoundsException
    copy(value,this.opened)
  }

  def opened(open: Boolean): CardBuilder = {
    copy(this.value,open)
  }

  def build(): Card = {
      Card(value, opened)
  }
}

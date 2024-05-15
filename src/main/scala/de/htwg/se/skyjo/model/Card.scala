package de.htwg.se.skyjo.model
import scala.util.Random

case class Card(value: Int, opened: Boolean = false) {
  def flip() = copy(value = this.value, opened = true)
}

object CardBuilder {
  def apply() = {new CardBuilder(Random.nextInt(15) - 2,false)}
}

case class CardBuilder(value:Int,opened:Boolean) {

  def value(value: Int): CardBuilder = {
    if( value>12 || value < -2) then throw new java.lang.IndexOutOfBoundsException
    copy(value,this.opened)
  }

  def opened(opened: Boolean): CardBuilder = {
    copy(this.value,opened)
  }

  def build(): Card = {
    Card(value, opened)
  }
}

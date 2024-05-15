package de.htwg.se.skyjo.model
import scala.util.Random

case class Card(value: Int, opened: Boolean = false) {
  def flip() = copy(value = this.value, opened = true)
}

object CardBuilder {
  def apply() = new CardBuilder()
}

class CardBuilder {
  private var value: Int = Random.nextInt(15) - 2
  private var opened: Boolean = false

  def value(value: Int): CardBuilder = {
    this.value = value
    this
  }

  def opened(opened: Boolean): CardBuilder = {
    this.opened = opened
    this
  }

  def build(): Card = {
    Card(value, opened)
  }
}

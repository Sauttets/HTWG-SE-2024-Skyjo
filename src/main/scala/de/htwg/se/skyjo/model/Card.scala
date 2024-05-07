package de.htwg.se.skyjo.model
import scala.util.Random

case class Card (value: Int, opened: Boolean = false):
    def this() = this(Random.nextInt(15) - 2, false)
    def this(opened: Boolean) = this(Random.nextInt(15) - 2, opened)
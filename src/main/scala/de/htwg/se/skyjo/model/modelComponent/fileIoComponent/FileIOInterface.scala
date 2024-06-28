package de.htwg.se.skyjo.model.modelComponent
import java.io.File
import de.htwg.se.skyjo.util.Move
import scala.collection.mutable.Buffer

trait FileIOInterface {
  def load(path:File): (ModelInterface,Buffer[Move])
  def save(table: ModelInterface,moves :Buffer[Move],path:File): Unit
}

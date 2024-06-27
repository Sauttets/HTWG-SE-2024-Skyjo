package de.htwg.se.skyjo.model.modelComponent
import java.io.File

trait FileIOInterface {
  def load(path:File): ModelInterface
  def save(table: ModelInterface,path:File): Unit
}

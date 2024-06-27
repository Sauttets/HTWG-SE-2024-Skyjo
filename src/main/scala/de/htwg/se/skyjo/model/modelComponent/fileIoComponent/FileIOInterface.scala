// File: de/htwg/se/skyjo/model/modelComponent/FileIOInterface.scala
package de.htwg.se.skyjo.model.modelComponent

trait FileIOInterface {
  def load: ModelInterface
  def save(table: ModelInterface): Unit
}

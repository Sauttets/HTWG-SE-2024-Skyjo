package de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoXmlImpl

import de.htwg.se.skyjo.model.modelComponent.{FileIOInterface, ModelInterface}
import scala.xml._

class FileIO extends FileIOInterface {
  override def load: ModelInterface = {
    val file = XML.loadFile("table.xml")
    // Deserialize XML to ModelInterface (implement your deserialization logic)
    ???
  }

  override def save(table: ModelInterface): Unit = {
    val xml = <table>
      // Serialize ModelInterface to XML (implement your serialization logic)
    </table>
    XML.save("table.xml", xml)
  }
}

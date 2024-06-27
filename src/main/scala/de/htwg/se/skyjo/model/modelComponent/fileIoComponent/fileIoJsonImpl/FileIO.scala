package de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoJsonImpl

import de.htwg.se.skyjo.model.modelComponent.{FileIOInterface, ModelInterface}
import play.api.libs.json._
import java.io._
import scala.io.Source

class FileIO extends FileIOInterface {
  override def load: ModelInterface = {
    val source: String = Source.fromFile("table.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    // Deserialize JSON to ModelInterface (implement your deserialization logic)
    // val table = ...
    // table
    ???
  }

  override def save(table: ModelInterface): Unit = {
    val pw = new PrintWriter(new File("table.json"))
    // Serialize ModelInterface to JSON (implement your serialization logic)
    // val json = ...
    // pw.write(Json.prettyPrint(json))
    pw.close()
  }
}

// File: de/htwg/se/skyjo/model/modelComponent/fileIoComponent/fileIoJsonImpl/FileIO.scala
package de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoJsonImpl

import de.htwg.se.skyjo.model.modelComponent.{FileIOInterface, ModelInterface, CardInterface}
import play.api.libs.json._
import java.io._
import scala.io.Source
import de.htwg.se.skyjo.model.modelComponent.modelImplementation._
import de.htwg.se.skyjo.util.CardStackStrategy
import com.google.inject.Guice
import com.google.inject.name.Names
import de.htwg.se.skyjo.SkyjoModule

class FileIO extends FileIOInterface {

  implicit val cardWrites: Writes[CardInterface] = new Writes[CardInterface] {
    def writes(card: CardInterface): JsValue = Json.obj(
      "value" -> card.value,
      "opened" -> card.opened
    )
  }

  implicit val cardReads: Reads[Card] = Json.reads[Card]

  implicit val vectorCardWrites: Writes[Vector[CardInterface]] = new Writes[Vector[CardInterface]] {
    def writes(vector: Vector[CardInterface]): JsValue = Json.toJson(vector.map(_.asInstanceOf[Card]))
  }

  implicit val vectorCardReads: Reads[Vector[CardInterface]] = new Reads[Vector[CardInterface]] {
    def reads(json: JsValue): JsResult[Vector[CardInterface]] = json.validate[Seq[Card]].map(_.toVector)
  }

  implicit val playerMatrixWrites: Writes[PlayerMatrix] = new Writes[PlayerMatrix] {
    def writes(matrix: PlayerMatrix): JsValue = Json.obj(
      "rows" -> matrix.rows
    )
  }

  implicit val playerMatrixReads: Reads[PlayerMatrix] = new Reads[PlayerMatrix] {
    def reads(json: JsValue): JsResult[PlayerMatrix] = for {
      rows <- (json \ "rows").validate[Vector[Vector[Card]]]
    } yield PlayerMatrix(rows)
  }

  implicit val lCardStackWrites: Writes[LCardStack] = new Writes[LCardStack] {
    def writes(stack: LCardStack): JsValue = Json.obj(
      "stack" -> stack.stack,
      "trashstack" -> stack.trashstack
    )
  }

  implicit val lCardStackReads: Reads[LCardStack] = new Reads[LCardStack] {
    def reads(json: JsValue): JsResult[LCardStack] = for {
      stack <- (json \ "stack").validate[List[Card]]
      trashstack <- (json \ "trashstack").validate[List[Card]]
    } yield LCardStack(stack, trashstack)
  }

  implicit val cardStackStrategyWrites: Writes[CardStackStrategy] = new Writes[CardStackStrategy] {
    def writes(strategy: CardStackStrategy): JsValue = strategy match {
      case stack: LCardStack => Json.toJson(stack)
      case _ => throw new IllegalArgumentException("Unsupported CardStackStrategy type")
    }
  }

  implicit val cardStackStrategyReads: Reads[CardStackStrategy] = new Reads[CardStackStrategy] {
    def reads(json: JsValue): JsResult[CardStackStrategy] = json.validate[LCardStack]
  }

  implicit val playerTableWrites: Writes[PlayerTable] = new Writes[PlayerTable] {
    def writes(table: PlayerTable): JsValue = Json.obj(
      "playerCount" -> table.playerCount,
      "width" -> table.width,
      "height" -> table.height,
      "currentPlayer" -> table.currentPlayer,
      "cardstack" -> table.cardstack,
      "tabletop" -> table.Tabletop
    )
  }

  implicit val playerTableReads: Reads[PlayerTable] = new Reads[PlayerTable] {
    def reads(json: JsValue): JsResult[PlayerTable] = for {
      playerCount <- (json \ "playerCount").validate[Int]
      width <- (json \ "width").validate[Int]
      height <- (json \ "height").validate[Int]
      currentPlayer <- (json \ "currentPlayer").validate[Int]
      cardstack <- (json \ "cardstack").validate[CardStackStrategy]
      tabletop <- (json \ "tabletop").validate[List[PlayerMatrix]]
    } yield PlayerTable(playerCount, width, height, currentPlayer, cardstack, tabletop)
  }

  override def load(path:File): ModelInterface = {
    var table: PlayerTable = null
    val source: String = Source.fromFile(path.toString()+".json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val playerCount = (json \ "playerCount").as[Int]
    val width = (json \ "width").as[Int]
    val height = (json \ "height").as[Int]
    val currentPlayer = (json \ "currentPlayer").as[Int]
    val cardstack = (json \ "cardstack").as[LCardStack]
    val tabletop = (json \ "tabletop").as[List[PlayerMatrix]]

    table = PlayerTable(playerCount, width, height, currentPlayer, cardstack, tabletop)
    table
  }

  override def save(model: ModelInterface,path:File): Unit = {
    val table = model.asInstanceOf[PlayerTable]
    val pw = new PrintWriter(new File(path.toString()+".json"))
    val json = Json.obj(
      "playerCount" -> table.playerCount,
      "width" -> table.width,
      "height" -> table.height,
      "currentPlayer" -> table.currentPlayer,
      "cardstack" -> Json.toJson(table.cardstack.asInstanceOf[LCardStack]),
      "tabletop" -> Json.toJson(table.Tabletop)
    )
    pw.write(Json.prettyPrint(json))
    pw.close()
  }
}

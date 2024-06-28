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
import scala.collection.mutable.Buffer
import de.htwg.se.skyjo.util.Move

class FileIO extends FileIOInterface {

  implicit val cardWrites: Writes[CardInterface] = new Writes[CardInterface] {
    def writes(card: CardInterface): JsValue = Json.obj(
      "value" -> card.value,
      "opened" -> card.opened
    )
  }

  implicit val cardReads: Reads[Card] = Json.reads[Card]

  implicit val moveReads: Reads[Move] = Json.reads[Move]

  implicit val vectorCardWrites: Writes[Vector[CardInterface]] = new Writes[Vector[CardInterface]] {
    def writes(vector: Vector[CardInterface]): JsValue = Json.toJson(vector.map(_.asInstanceOf[Card]))
  }

  implicit val vectorCardReads: Reads[Vector[CardInterface]] = new Reads[Vector[CardInterface]] {
    def reads(json: JsValue): JsResult[Vector[CardInterface]] = json.validate[Vector[Card]]
  }

  implicit val playerMatrixWrites: Writes[PlayerMatrix] = new Writes[PlayerMatrix] {
    def writes(matrix: PlayerMatrix): JsValue = Json.obj(
      "rows" -> matrix.rows
    )
  }

  implicit val playerMatrixReads: Reads[PlayerMatrix] = new Reads[PlayerMatrix] {
    def reads(json: JsValue): JsResult[PlayerMatrix] = for {
      rows <- (json \ "rows").validate[Vector[Vector[CardInterface]]]
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

  // implicit val cardStackStrategyReads: Reads[CardStackStrategy] = new Reads[CardStackStrategy] {
  //   def reads(json: JsValue): JsResult[CardStackStrategy] = json.validate[LCardStack]
  // }
  implicit val moveWrites: Writes[Move] = new Writes[Move] {
      def writes(move: Move): JsValue =Json.obj(
        "drawnFromStack" -> move.drawnFromStack,
        "swapped" -> move.swapped,
        "row" ->move.row,
        "col" ->move.col
      )
  }
  implicit val movesReads: Reads[Buffer[Move]] = new Reads[Buffer[Move]] {
    def reads(json: JsValue): JsResult[Buffer[Move]] = {
      json.validate[Seq[Move]].map(Buffer(_*))
    }
  }

  // implicit val moveReads: Reads[Move] = new Reads[Move] {
  //   def reads(json: JsValue): JsResult[Move] = for {
  //     drawnFromStack <- (json \ "fromStack").validate[Boolean]
  //     swapped <- (json \ "swapped").validate[Boolean]
  //     col <- (json \ "col").validate[Int]
  //     row <- (json \ "row").validate[Int]
  //   } yield Move(drawnFromStack, swapped, col, row)
  // }

  override def load(path:File): (ModelInterface,Buffer[Move]) = {
    var table: PlayerTable = null
    val source: String = Source.fromFile(path.toString()+".json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val playerCount = (json \ "playerCount").as[Int]
    val width = (json \ "width").as[Int]
    val height = (json \ "height").as[Int]
    val currentPlayer = (json \ "currentPlayer").as[Int]
    val cardstack = (json \ "cardstack").as[LCardStack]
    val tabletop = (json \ "tabletop").as[List[PlayerMatrix]]
    val moves =(json \ "moves").as[Buffer[Move]]
    print(moves)

    (PlayerTable(playerCount, width, height, currentPlayer, cardstack, tabletop),moves.toBuffer)
  }

  override def save(model: ModelInterface,moves:Buffer[Move],path:File): Unit = {
    val table = model.asInstanceOf[PlayerTable]
    val pw = new PrintWriter(new File(path.toString()+".json"))
    val json = Json.obj(
      "playerCount" -> table.playerCount,
      "width" -> table.width,
      "height" -> table.height,
      "currentPlayer" -> table.currentPlayer,
      "cardstack" -> Json.toJson(table.cardstack.asInstanceOf[LCardStack]),
      "tabletop" -> Json.toJson(table.Tabletop),
      "moves" ->Json.toJson(moves)
    )
    pw.write(Json.prettyPrint(json))
    pw.close()
  }
}

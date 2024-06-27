// File: de/htwg/se/skyjo/model/modelComponent/fileIoComponent/fileIoXmlImpl/FileIO.scala
package de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoXmlImpl

import de.htwg.se.skyjo.model.modelComponent.{FileIOInterface, ModelInterface}
import de.htwg.se.skyjo.model.modelComponent._
import de.htwg.se.skyjo.model.modelComponent.modelImplementation._
import scala.xml._

class FileIO extends FileIOInterface {

  def cardToXml(card: CardInterface): Elem = {
    <card>
      <value>{card.value}</value>
      <opened>{card.opened}</opened>
    </card>
  }

  def cardFromXml(node: Node): Card = {
    val value = (node \ "value").text.toInt
    val opened = (node \ "opened").text.toBoolean
    Card(value, opened)
  }

  def matrixToXml(matrix: PlayerMatrix): Elem = {
    <matrix>
      {
        matrix.rows.map { row =>
          <row>
            {row.map(cardToXml)}
          </row>
        }
      }
    </matrix>
  }

  def matrixFromXml(node: Node): PlayerMatrix = {
    val rows = (node \ "row").map { rowNode =>
      (rowNode \ "card").map(cardFromXml).toVector
    }.toVector
    PlayerMatrix(rows)
  }

  def stackToXml(stack: LCardStack): Elem = {
    <cardstack>
      <stack>
        {stack.stack.map(cardToXml)}
      </stack>
      <trashstack>
        {stack.trashstack.map(cardToXml)}
      </trashstack>
    </cardstack>
  }

  def stackFromXml(node: Node): LCardStack = {
    val stack = (node \ "stack" \ "card").map(cardFromXml).toList
    val trashstack = (node \ "trashstack" \ "card").map(cardFromXml).toList
    LCardStack(stack, trashstack)
  }

  def tableToXml(table: PlayerTable): Elem = {
    <table>
      <playerCount>{table.playerCount}</playerCount>
      <width>{table.width}</width>
      <height>{table.height}</height>
      <currentPlayer>{table.currentPlayer}</currentPlayer>
      {stackToXml(table.cardstack.asInstanceOf[LCardStack])}
      <tabletop>
        {table.Tabletop.map(matrixToXml)}
      </tabletop>
    </table>
  }

  def tableFromXml(node: Node): PlayerTable = {
    val playerCount = (node \ "playerCount").text.toInt
    val width = (node \ "width").text.toInt
    val height = (node \ "height").text.toInt
    val currentPlayer = (node \ "currentPlayer").text.toInt
    val cardstack = stackFromXml((node \ "cardstack").head)
    val tabletop = (node \ "tabletop" \ "matrix").map(matrixFromXml).toList
    PlayerTable(playerCount, width, height, currentPlayer, cardstack, tabletop)
  }

  override def load: ModelInterface = {
    val file = XML.loadFile("table.xml")
    tableFromXml(file)
  }

  override def save(table: ModelInterface): Unit = {
    val xml = tableToXml(table.asInstanceOf[PlayerTable])
    XML.save("table.xml", xml)
  }
}

package de.htwg.se.skyjo
package aview

import model.{Move, Card}
import controller.TableController

import scala.util.{Try, Success, Failure}
import scala.swing._
import scala.swing.event._
import util.Observer
import de.htwg.se.skyjo.model.CardBuilder.apply
import javax.swing.border.LineBorder
import java.awt.Color
import scala.util.boundary
import javax.swing.border.{EmptyBorder, Border, CompoundBorder}
import de.htwg.se.skyjo.model.PlayerMatrix

class GUI(controller: TableController) extends MainFrame with Observer:
  title = "Skyjo"
  controller.add(this)

  def update: Unit = {
    refreshCards()
    println(controller)
  }

  val drawFromTrashButton, drawFromStackButton = new Button() {
    preferredSize = new Dimension(50, 70)
    minimumSize = new Dimension(50, 70)
    maximumSize = new Dimension(50, 70)
  }
  drawFromTrashButton.text = "Trash Card: XX"
  drawFromStackButton.text = "Stack Card: XX"

  val stackLabel, trashLabel = new Label()
  stackLabel.text = "Stack"
  trashLabel.text = "Trash"

  val drawPanel = new BoxPanel(Orientation.Horizontal) {
    contents += (new BoxPanel(Orientation.Vertical) {
      contents += stackLabel
      contents += drawFromStackButton
    }, new BoxPanel(Orientation.Vertical) {
      contents += trashLabel
      contents += drawFromTrashButton
    })
    border = Swing.EmptyBorder(10, 0, 10, 0)
  }

  val undoMenu = new MenuItem("Undo") { mnemonic = Key.Z }
  val redoMenu = new MenuItem("Redo") { mnemonic = Key.Y }
  val quitMenu = new MenuItem("Quit") { mnemonic = Key.Q }
  menuBar = new MenuBar {
    contents += new Menu("Util") {
      mnemonic = Key.U
      contents += (undoMenu, redoMenu, quitMenu)
    }
  }

  def updateMatrix(player: Int) = {
    val grid = playerGridList(player)
    val mat = controller.table.Tabletop(player)
    grid.contents.clear()
    for (r <- 0 until grid.rows; c <- 0 until grid.columns)
      grid.contents += new CardGUI(mat.getCard(r, c))
    grid.background = new Color(0, 0, 0, 0)
    val fixedSize = new Dimension((CardGUI.width + CardGUI.vpadding * 2) * grid.columns, (CardGUI.height + CardGUI.hpadding * 2) * grid.rows)
    grid.preferredSize_=(fixedSize)
    grid.minimumSize_=(fixedSize)
    grid.maximumSize_=(fixedSize)
    grid.border = EmptyBorder(10, 5, 10, 5)
  }

  val playerGridList = controller.table.Tabletop.map(mat => new GridPanel(mat.size, mat.rows.size))
  val table = new FlowPanel() {
    background = Color.black
    for (i <- 0 until playerGridList.length)
      updateMatrix(i)
      contents += playerGridList(i)
  }
  contents = new BoxPanel(Orientation.Vertical) {
    contents += (drawPanel, Swing.VStrut(20), table)
  }

  listenTo(drawFromTrashButton, drawFromStackButton, menuBar, quitMenu, undoMenu, redoMenu)

  reactions += {
    case ButtonClicked(`drawFromTrashButton`) =>
      controller.drawFromTrash()
      showMovePopup(false)
    case ButtonClicked(`drawFromStackButton`) =>
      controller.drawFromStack()
      showMovePopup(true)
    case ButtonClicked(`undoMenu`) =>
      controller.careTaker.undo()
    case ButtonClicked(`redoMenu`) =>
      controller.careTaker.redo()
    case ButtonClicked(`quitMenu`) =>
      sys.exit(0)
  }

  def showMovePopup(draw: Boolean): Unit = {
    val dialog = new Dialog {
      title = "Enter Move Details"
      var swapped: Boolean = false
      val rowField, colField = new TextField { columns = 5 }
      val replaceButton = new Button { text = "Replace" }
      val throwButton = new Button { text = "Throw" }
      val executeButton = new Button { text = "Execute" }

      contents = new BoxPanel(Orientation.Vertical) {
        contents ++= Seq(
          new BoxPanel(Orientation.Horizontal) {
            contents ++= Seq(replaceButton, throwButton)
          },
          new Label("Row:"), rowField,
          new Label("Column:"), colField,
          executeButton
        )
        border = Swing.EmptyBorder(10, 10, 10, 10)
      }

      listenTo(replaceButton, throwButton, executeButton)

      reactions += {
        case ButtonClicked(`replaceButton`) =>
          swapped = true
        case ButtonClicked(`throwButton`) =>
          swapped = false
        case ButtonClicked(`executeButton`) =>
          val moveInputString = s" ${if (swapped) "S" else "T"} ${rowField.text} ${colField.text}"
          moveInput(moveInputString, draw) match {
            case None =>
              Dialog.showMessage(contents.head, "Invalid move input", title = "Error", Dialog.Message.Error)
            case Some(move) =>
              controller.doMove(move)
              close()
          }
      }

      centerOnScreen()
      open()
    }
  }

  def moveInput(input: String, draw: Boolean): Option[Move] = {
    val Input = (" " + input).toUpperCase().replaceAll("\\s+", " ")
    if (Input.replaceAll(" ", "") == "Q") sys.exit(0)

    val parts = Input.split(" ")
    if (parts.length < 4) None
    else {
      Try {
        val swapped = parts(1) match {
          case "S" | "SWAP" => true
          case "T" | "D" | "DISCARD" => false
          case _ => throw new IllegalArgumentException("Invalid action")
        }
        Move(draw, swapped, parts(2).toInt, parts(3).toInt)
      } match {
        case Success(move) => Some(move)
        case Failure(_) => None
      }
    }
  }

  def refreshCards(): Unit = {
    val cardStack = controller.table.cardstack
    drawFromStackButton.text = if (cardStack.getStackCard().opened) s"${cardStack.getStackCard().value}" else "XX"
    drawFromTrashButton.text = s"${cardStack.getTrashCard().value}"
    val currentPlayer = controller.table.currentPlayer
    for(grid<-0 until controller.table.Tabletop.length) updateMatrix(grid)
    validate();
    repaint();
  }

  pack()
  minimumSize_=(size)
  centerOnScreen()
  open()
  refreshCards()
end GUI

class CardGUI(value: Int, open: Boolean, dim: Dimension = CardGUI.fixedSize) extends BorderPanel {
  import CardGUI._
  def this(card: Card) = this(card.value, card.opened)
  val cardsurface = new BorderPanel() {
    if (open) {
      add(new Label(value.toString), BorderPanel.Position.Center)
      background = Color.green
    } else
      background = Color.blue
    val wborder = new LineBorder(Color.white, 3)
    val eborder = new EmptyBorder(2, 2, 2, 2)
    border = new CompoundBorder(eborder, wborder)
    preferredSize_=(dim)
    minimumSize_=(dim)
    maximumSize_=(dim)
  }
  add(cardsurface, BorderPanel.Position.Center)
  border = new EmptyBorder(hpadding, vpadding, hpadding, vpadding)
  val paddedDim = new Dimension(cardsurface.minimumSize.width + 2 * vpadding, cardsurface.maximumSize.height + 2 * hpadding)
  background = new Color(0, 0, 0, 0)
  preferredSize_=(paddedDim)
  minimumSize_=(paddedDim)
  maximumSize_=(paddedDim)
}

object CardGUI {
  val hpadding = 2
  val vpadding = 5
  val fixedSize = new Dimension(50, 70)
  val width = fixedSize.width
  val height = fixedSize.height
}

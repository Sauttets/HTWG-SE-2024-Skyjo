package de.htwg.se.skyjo
package aview

import model.{Move, Card, PlayerTable}
import controller.TableController

import scala.util.{Try, Success, Failure}
import scala.swing._
import scala.swing.event._
import util.Observer

class GUI(controller: TableController) extends MainFrame with Observer:
  title = "Skyjo"

  controller.add(this)

  def update: Unit = {
    refreshCards()
    println(controller)
  }

  val drawFromTrashButton = new Button {
    text = "Draw from Trash"
  }

  val drawFromStackButton = new Button {
    text = "Draw from Stack"
  }

  val undoButton = new Button {
    text = "Undo"
  }

  val redoButton = new Button {
    text = "Redo"
  }

  val quitButton = new Button {
    text = "Quit"
  }

  val stackCardLabel = new Label {
    text = "Stack Card: XX"
  }

  val trashCardLabel = new Label {
    text = "Trash Card: XX"
  }

  contents = new BoxPanel(Orientation.Vertical) {
    contents += drawFromTrashButton
    contents += drawFromStackButton
    contents += undoButton
    contents += redoButton
    contents += quitButton
    contents += Swing.VStrut(20)
    contents += stackCardLabel
    contents += trashCardLabel
    border = Swing.EmptyBorder(30, 30, 10, 30)
  }

  listenTo(drawFromTrashButton, drawFromStackButton, undoButton, redoButton, quitButton)

  reactions += {
    case ButtonClicked(`drawFromTrashButton`) =>
      refreshCards()
      showMovePopup(false)
    case ButtonClicked(`drawFromStackButton`) =>
      controller.drawFromStack()
      refreshCards()
      showMovePopup(true)
    case ButtonClicked(`undoButton`) =>
      controller.careTaker.undo()
      refreshCards()
    case ButtonClicked(`redoButton`) =>
      controller.careTaker.redo()
      refreshCards()
    case ButtonClicked(`quitButton`) =>
      sys.exit(0)
  }

  def showMovePopup(draw: Boolean): Unit = {
    val dialog = new Dialog {
      title = "Enter Move Details"
      val swappedField = new TextField { columns = 5 }
      val rowField = new TextField { columns = 5 }
      val colField = new TextField { columns = 5 }
      val executeButton = new Button { text = "Execute" }

      contents = new BoxPanel(Orientation.Vertical) {
        contents += new Label("Swapped (S or T):")
        contents += swappedField
        contents += new Label("Row:")
        contents += rowField
        contents += new Label("Column:")
        contents += colField
        contents += executeButton
        border = Swing.EmptyBorder(10, 10, 10, 10)
      }

      listenTo(executeButton)

      reactions += {
        case ButtonClicked(`executeButton`) =>
          val moveInputString = s" ${swappedField.text} ${rowField.text} ${colField.text}"
          moveInput(moveInputString, draw) match
            case None       =>
              Dialog.showMessage(contents.head, "Invalid move input", title = "Error", Dialog.Message.Error)
            case Some(move) =>
              controller.doMove(move)
              refreshCards()
              close()
      }

      centerOnScreen()
      open()
    }
  }

  def moveInput(input: String, draw: Boolean): Option[Move] = {
    val Input = (" " + input).toUpperCase().replaceAll("\\s+", " ")

    if (Input.replaceAll(" ", "") == "Q") {
      sys.exit(0)
    }

    val parts = Input.split(" ")

    if (parts.length < 4) {
      None // Not enough parts to parse
    } else {
      Try {
        val swapped = parts(1) match {
          case "S" => true
          case "T" => false
          case "D" => false
          case "SWAP" => true
          case "DISCARD" => false
          case _ => throw new IllegalArgumentException("Invalid action")
        }

        val row = parts(2).toInt
        val col = parts(3).toInt

        Move(draw, swapped, row, col)
      } match {
        case Success(move) => Some(move)
        case Failure(_) => None
      }
    }
  }

  def refreshCards(): Unit = {
    val cardStack = controller.table.getCardStack()
    val stackCard = cardStack.getStackCard()
    val trashCard = cardStack.getTrashCard()

    stackCardLabel.text = if (stackCard.opened) s"Stack Card: ${stackCard.value}" else "Stack Card: XX"
    trashCardLabel.text = if (trashCard.opened) s"Trash Card: ${trashCard.value}" else "Trash Card: XX"
  }

  size = new Dimension(300, 300)
  centerOnScreen()
  open()

  refreshCards()
end GUI

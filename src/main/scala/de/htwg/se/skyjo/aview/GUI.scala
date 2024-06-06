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

  val drawFromTrashButton, drawFromStackButton, undoButton, redoButton, quitButton = new Button()
  drawFromTrashButton.text = "Draw from Trash"
  drawFromStackButton.text = "Draw from Stack"
  undoButton.text = "Undo"
  redoButton.text = "Redo"
  quitButton.text = "Quit"

  val stackCardLabel, trashCardLabel = new Label()
  stackCardLabel.text = "Stack Card: XX"
  trashCardLabel.text = "Trash Card: XX"

  contents = new BoxPanel(Orientation.Vertical) {
    contents ++= Seq(drawFromTrashButton, drawFromStackButton, undoButton, redoButton, quitButton, Swing.VStrut(20), stackCardLabel, trashCardLabel)
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
      val swappedField, rowField, colField = new TextField { columns = 5 }
      val executeButton = new Button { text = "Execute" }

      contents = new BoxPanel(Orientation.Vertical) {
        contents ++= Seq(new Label("Swapped (S or T):"), swappedField, new Label("Row:"), rowField, new Label("Column:"), colField, executeButton)
        border = Swing.EmptyBorder(10, 10, 10, 10)
      }

      listenTo(executeButton)

      reactions += {
        case ButtonClicked(`executeButton`) =>
          val moveInputString = s" ${swappedField.text} ${rowField.text} ${colField.text}"
          moveInput(moveInputString, draw) match {
            case None       =>
              Dialog.showMessage(contents.head, "Invalid move input", title = "Error", Dialog.Message.Error)
            case Some(move) =>
              controller.doMove(move)
              refreshCards()
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
    val cardStack = controller.table.getCardStack()
    stackCardLabel.text = if (cardStack.getStackCard().opened) s"Stack Card: ${cardStack.getStackCard().value}" else "Stack Card: XX"
    trashCardLabel.text = if (cardStack.getTrashCard().opened) s"Trash Card: ${cardStack.getTrashCard().value}" else "Trash Card: XX"
  }

  size = new Dimension(300, 300)
  centerOnScreen()
  open()
  refreshCards()
end GUI

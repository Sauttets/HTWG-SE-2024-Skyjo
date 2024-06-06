package de.htwg.se.skyjo
package aview

import model.{Move, Card, PlayerTable}
import controller.TableController

import scala.util.{Try, Success, Failure}
import scala.swing._
import scala.swing.event._
import util.Observer
import de.htwg.se.skyjo.model.CardBuilder.apply
import javax.swing.border.LineBorder
import java.awt.Color;
import scala.util.boundary
import javax.swing.border.EmptyBorder
import javax.swing.border.Border
import scala.annotation.TypeConstraint
import javax.swing.border.CompoundBorder
import de.htwg.se.skyjo.model.PlayerMatrix


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

  val drawPanel=new BoxPanel(Orientation.Horizontal){
      contents+=(drawFromTrashButton,drawFromStackButton)
      border=Swing.EmptyBorder(10,0,10,0)
  }
  val undoMenu=new MenuItem("Undo"){mnemonic=Key.Z}
  val redoMenu=new MenuItem("Redo"){mnemonic=Key.Y}
  val quitMenu= new MenuItem("Quit"){mnemonic=Key.Q}
  menuBar=new MenuBar{
    contents+= new Menu("Util"){
      mnemonic=Key.U
      contents+=(undoMenu,redoMenu,quitMenu)
    }
  }
  val playerMatrixGui=controller.table.Tabletop.map(mat=>{
    val grid =new GridPanel(mat.size,mat.rows.size)
    val cardPanel=new CardGUI(0,false)
    for(r<-0 until grid.rows;c<-0 until grid.columns)
      val card=mat.getCard(r,c)
      val cardPanel=new CardGUI(card.value,card.opened)
      grid.contents+=cardPanel
    val fixedSize=new Dimension(cardPanel.fixedSize.width*grid.columns,cardPanel.fixedSize.height*grid.rows)
    grid.preferredSize_=(fixedSize)
    grid.minimumSize_=(fixedSize)
    grid.maximumSize_=(fixedSize)
    grid
    }
  )

  contents = new BoxPanel(Orientation.Vertical) {
    contents += (drawPanel,undoButton, redoButton, quitButton, Swing.VStrut(20),stackCardLabel, trashCardLabel)
    playerMatrixGui.foreach(panel=>contents+=(panel,Swing.VStrut(20)))
    border = Swing.EmptyBorder(30, 30, 10, 30)
  }

  listenTo(drawFromTrashButton, drawFromStackButton,menuBar,quitMenu,undoMenu,redoMenu)

  reactions += {
    case ButtonClicked(`drawFromTrashButton`) =>
      refreshCards()
      showMovePopup(false)
    case ButtonClicked(`drawFromStackButton`) =>
      controller.drawFromStack()
      refreshCards()
      showMovePopup(true)
    case ButtonClicked(`undoMenu`) =>
      controller.careTaker.undo()
      refreshCards()
    case ButtonClicked(`redoMenu`) =>
      controller.careTaker.redo()
      refreshCards()
    case ButtonClicked(`quitMenu`) =>
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

  pack()
  centerOnScreen()
  open()
  refreshCards()
end GUI

class  CardGUI(value:Int,open:Boolean) extends BorderPanel{
  val text=new Label(value.toString)
  add(text,BorderPanel.Position.Center)
  background=Color.green
  visible_=(true)
  val wborder= new LineBorder(Color.white,3)
  val gborder= new EmptyBorder(2,2,2,2)
  border=new CompoundBorder(gborder,wborder)
  visible_=(true)
  val fixedSize=new Dimension(50,70)
  preferredSize_=(fixedSize)
  minimumSize_=(fixedSize)
  maximumSize_=(fixedSize)
  // override def preferredSize: Dimension = new Dimension(400,120)
}
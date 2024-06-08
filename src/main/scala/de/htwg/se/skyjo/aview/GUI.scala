package de.htwg.se.skyjo
package aview

import model.{Move, Card}
import controller.TableController

import scala.util.{Try, Success, Failure}
import scala.swing._
import scala.swing.Component._
import scala.swing.event._
import util.Observer
import de.htwg.se.skyjo.model.CardBuilder.apply
import javax.swing.border.LineBorder
import java.awt.Color
import scala.util.boundary
import javax.swing.border.{EmptyBorder, Border, CompoundBorder}
import de.htwg.se.skyjo.model.PlayerMatrix
import javax.swing.UIManager
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import java.awt.image.BufferedImage
import scala.collection.MapView.Keys
import javax.swing.JLayeredPane
import javax.swing.JComponent
import javax.swing.JPanel
import de.htwg.se.skyjo.aview.CardGUI.buttonBorder
import javax.swing.text.Highlighter.Highlight
import javax.swing.text.Highlighter
import scala.collection.mutable.Buffer

class GUI(controller: TableController) extends MainFrame with Observer:
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  title = "Skyjo"
  controller.add(this)

  def update: Unit = {
    refreshCards()
    println(controller)
  }
  val stackCardButton = new ButtonPanel(0,-1)
  val trashCardButton=new ButtonPanel(1,-1){
    reactions+={
      case MousePressed(_,_,_,_,_)=>
        if (active)
          publish(new DrawEvent(false))
          stackCardButton.active_(false)
        }
  }
  stackCardButton.reactions+={case MousePressed(_,_,_,_,_)=>{
      if(stackCardButton.active) 
        publish(new DrawEvent(true))
        trashCardButton.active_(false)
    }
  }
  case class DrawEvent(stack:Boolean) extends Event
  val stackLabel, trashLabel = new Label()
  stackLabel.text = "Stack"
  trashLabel.text = "Trash"

  val drawPanel = new BoxPanel(Orientation.Horizontal) {
    val stackPanel=new BoxPanel(Orientation.Vertical) {
      contents += stackLabel += stackCardButton
    }
    val trashPanel=new BoxPanel(Orientation.Vertical) {
      contents += trashLabel += trashCardButton
    }
    contents +=stackPanel+=trashPanel
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
  def initMatrix(player:Int)={
    val grid = playerGridList(player)
    for (r <- 0 until grid.rows; c <- 0 until grid.columns)
      grid.contents+=new ButtonPanel(r,c)
    grid.opaque_=(false)
    val fixedSize = new Dimension((CardGUI.width+3)  * grid.columns, (CardGUI.height+3)* grid.rows)
    grid.preferredSize_=(fixedSize)
    grid.minimumSize_=(fixedSize)
    grid.maximumSize_=(fixedSize)
    grid.border = EmptyBorder(10, 5, 10, 5)
    updateMatrix(player)
  }
  def updateMatrix(player: Int) = {
    val grid = playerGridList(player)
    val mat = controller.table.Tabletop(player)
    for (r <- 0 until grid.rows; c <- 0 until grid.columns)
      grid.contents(r*grid.columns+c).asInstanceOf[ButtonPanel].setContent(new CardGUI(mat.getCard(r, c)))
  }

  val playerGridList = controller.table.Tabletop.map(mat => new GridPanel(mat.size, mat.rows.size))
  val table = new FlowPanel() {
    background = Color.black
    for (i <- 0 until playerGridList.length)
      initMatrix(i)
      contents += playerGridList(i)
  }
  val mainPanel=new BoxPanel(Orientation.Vertical){
    focusable_=(true)
    contents += (drawPanel, Swing.VStrut(20), table)
    requestFocus()
  }
  contents = mainPanel

  listenTo(trashCardButton, stackCardButton, menuBar, quitMenu, undoMenu, redoMenu,mainPanel.keys)

  reactions += {
    case DrawEvent(false) =>
      controller.drawFromTrash()
      showMovePopup(false)
    case DrawEvent(true) =>
      controller.drawFromStack()
      showMovePopup(true)
    case ButtonClicked(`undoMenu`) | KeyPressed(_, Key.Z, Key.Modifier.Control, _)=>
      controller.careTaker.undo()
    case ButtonClicked(`redoMenu`) | KeyPressed(_, Key.Y, Key.Modifier.Control, _) =>
      controller.careTaker.redo()
    case ButtonClicked(`quitMenu`) | KeyPressed(_, Key.Q, Key.Modifier.Control, _)=>
      sys.exit(0)
  }

  def showMovePopup(draw: Boolean): Unit = {
    val dialog = new Dialog {
      title = "Enter Move Details"
      var swapped: Boolean = false
      val rowField, colField = new TextField { columns = 5 }
      val replaceButton = new Button { text = "Replace" }
      val throwButton = new Button { text = "Throw"; enabled_=(false); background=Color.green}
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
          replaceButton.enabled_=(false)
          replaceButton.background=Color.green
          throwButton.enabled_=(true)
          throwButton.background=new Button().background
        case ButtonClicked(`throwButton`) =>
          swapped = false
          throwButton.enabled_=(false)
          throwButton.background=Color.green
          replaceButton.enabled_=(true)
          replaceButton.background=new Button().background
        case ButtonClicked(`executeButton`) =>
          val moveInputString = s" ${if (swapped) "S" else "T"} ${rowField.text} ${colField.text}"
          moveInput(moveInputString, draw) match {
            case None =>
              Dialog.showMessage(contents.head, "Invalid move input", title = "Error", Dialog.Message.Error)
            case Some(move) =>
              controller.doMove(move)
              stackCardButton.active_(true).highlight_(false)
              trashCardButton.active_(true).highlight_(false)
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
    stackCardButton.setContent(new CardGUI(cardStack.getStackCard()))
    trashCardButton.setContent(new CardGUI(cardStack.getTrashCard()))
    val currentPlayer = controller.table.currentPlayer
    for(grid<-0 until controller.table.Tabletop.length) updateMatrix(grid)
    validate();
    repaint();
    mainPanel.requestFocus()
  }
  refreshCards()
  pack()
  minimumSize_=(size)
  centerOnScreen()
  open()
end GUI

class CardGUI(value: Int, open: Boolean, dim: Dimension = CardGUI.cardDim) extends BorderPanel {
  import CardGUI._
  def this(card: Card) = this(card.value, card.opened)
  if (open) {
    add(new Label(value.toString){font_=(font.deriveFont(20.0f));foreground_=(Color.black)}, BorderPanel.Position.Center)
    add(new Label(value.toString,null,Alignment.Left){foreground_=(Color.black)}, BorderPanel.Position.North)
    add(new Label(value.toString,null,Alignment.Right){foreground_=(Color.black)}, BorderPanel.Position.South)
    background_=(Color.green)
  } else
    background_=(Color.blue)
  val wborder = new LineBorder(Color.white, 3)
  val eborder = new EmptyBorder(2, 2, 2, 2)
  border_=(new CompoundBorder(eborder, wborder))
  preferredSize_=(dim)
  minimumSize_=(dim)
  maximumSize_=(dim)
  preferredSize_=(cardDim)
  minimumSize_=(cardDim)
  maximumSize_=(cardDim)
}

object CardGUI {
  val hpadding = 5
  val vpadding = 8
  val cardDim = new Dimension(50, 70)
  val width = cardDim.width+2*vpadding
  val height = cardDim.height+2*hpadding
  val buttonBorder=new EmptyBorder(5,8,5,8)
  val cardButtonDim = new Dimension(width, height)
}

case class ButtonPanel(var active:Boolean,var highlight:Boolean,x:Int,y:Int)extends BoxPanel(Orientation.Vertical){
  def this(x:Int,y:Int)=this(true,false,x,y)
  border_=(CardGUI.buttonBorder)
  opaque_=(false)
  def highlight_(high:Boolean)={
    border_= (if (high)new CompoundBorder(new LineBorder(Color.yellow,3),new EmptyBorder(2,5,2,5)) 
    else CardGUI.buttonBorder)
    highlight=high
  }
  def active_(act:Boolean)={
    contents.foreach(c=>c.enabled_=(act))
    active=act;enabled_=(act)
    this
  }
  def setContent(comp:Component)={contents.clear();contents+=comp; this}
  listenTo(mouse.clicks)
  reactions+={case MousePressed(_,_,_,_,_)=>if(active)highlight_(!highlight)}
}

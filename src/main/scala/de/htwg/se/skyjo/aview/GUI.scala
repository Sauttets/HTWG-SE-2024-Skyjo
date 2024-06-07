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

class GUI(controller: TableController) extends MainFrame with Observer:
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  title = "Skyjo"
  controller.add(this)

  def update: Unit = {
    refreshCards()
    println(controller)
  }

  val drawFromTrashButton, drawFromStackButton = new Button() {
    // var c = new java.awt.Component(){contents=new CardGUI(controller.table.cardstack.getTrashCard())}
    // var im = new BufferedImage(CardGUI.width, CardGUI.height, BufferedImage.TYPE_INT_ARGB);
    // c.printAll(im.getGraphics())
    // print(im.getHeight()+":"+im.getWidth())
    // icon_=(new ImageIcon(im));
    // borderPainted_=(false)
    // contentAreaFilled_=(false)
    preferredSize = CardGUI.fixedSize
    minimumSize = CardGUI.fixedSize
    maximumSize = CardGUI.fixedSize
  }

  val stackLabel, trashLabel = new Label()
  stackLabel.text = "Stack"
  trashLabel.text = "Trash"

  val drawPanel = new BoxPanel(Orientation.Horizontal) {
    contents += (new BoxPanel(Orientation.Vertical) {
      contents += stackLabel
      // val layers=new JComponent(){add (new JLayeredPane(){
      //   add(drawFromStackButton.peer,JLayeredPane.PALETTE_LAYER);
      //   add(new CardGUI(controller.table.cardstack.getStackCard()).peer,JLayeredPane.DEFAULT_LAYER);
      //   }
      //   )
      // }
      // contents += wrap(layers)
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
    val fixedSize = new Dimension(CardGUI.width  * grid.columns, CardGUI.height* grid.rows)
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
  val mainPanel=new BoxPanel(Orientation.Vertical){
    focusable_=(true)
    contents += (drawPanel, Swing.VStrut(20), table)
    requestFocus()
  }
  contents = mainPanel

  listenTo(drawFromTrashButton, drawFromStackButton, menuBar, quitMenu, undoMenu, redoMenu,mainPanel.keys)

  reactions += {
    case ButtonClicked(`drawFromTrashButton`) =>
      controller.drawFromTrash()
      showMovePopup(false)
    case ButtonClicked(`drawFromStackButton`) =>
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
    mainPanel.requestFocus()
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
      add(new Label(value.toString){font_=(font.deriveFont(20.0f));foreground_=(Color.black)}, BorderPanel.Position.Center)
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
  val width = fixedSize.width+2*vpadding
  val height = fixedSize.height+2*hpadding
}

package de.htwg.se.skyjo
package aview

import model.modelComponent.modelImplementation.{Card}
import util.Move
import controller.controllerComponent.controllerimplementation.TableController

import scala.util.{Try, Success, Failure}
import scala.swing._
import scala.swing.Component._
import scala.swing.event._
import util.Observer
import javax.swing.border.LineBorder
import java.awt.Color
import scala.util.boundary
import javax.swing.border.{EmptyBorder, Border, CompoundBorder}
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
import java.awt.Graphics
import java.awt.RenderingHints
import javax.swing.JLabel
import scala.compiletime.ops.float
import java.awt.Paint
import java.awt.TexturePaint
import java.awt.geom.Rectangle2D
import de.htwg.se.skyjo.model.modelComponent.CardInterface
import de.htwg.se.skyjo.controller.controllerComponent.ControllerInterface

class GUI(controller: ControllerInterface) extends MainFrame with Observer:
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  title = "Skyjo"
  controller.add(this)

  def update: Unit = {
    refreshCards()
  }
  val stackCardButton = new ButtonPanel(0,-1)
  val trashCardButton=new ButtonPanel(1,-1){
    reactions+={
      case MousePressed(_,p,_,_,_)=>
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
  def initMatrix()={
    playerGridList.foreach(grid =>
      for (r <- 0 until grid.rows; c <- 0 until grid.columns)
        grid.contents+=new ButtonPanel(r,c){reactions+={case MousePressed(_,_,_,_,_)=>print(x+":"+y)}}
      grid.opaque_=(false)
      val fixedSize = new Dimension((CardGUI.width+3)  * grid.columns, (CardGUI.height+3)* grid.rows)
      grid.preferredSize_=(fixedSize)
      grid.minimumSize_=(fixedSize)
      grid.maximumSize_=(fixedSize)
      grid.border_=(EmptyBorder(10, 5, 10, 5))
    )
  }
  def updateMatrix(currplayer: Int) = {
    for(i<-0 until playerGridList.length)
      val grid = playerGridList(i)
      val mat = controller.getTabletop()(i)
      grid.border_=(if (i!=currplayer)EmptyBorder(10, 5, 10, 5) else LineBorder(Color.red,5,true))
      for (c <- 0 until grid.columns;r <- 0 until grid.rows)
        grid.contents(r*grid.columns+c).asInstanceOf[ButtonPanel].setContent(new CardGUI(mat.getCard(r, c))).active_(i==currplayer).highlight_(false)
  }

  val playerGridList = controller.getTabletop().map(mat =>{new GridPanel(mat.rows.size, mat.rows(0).size)})
  val table = new FlowPanel() {
    background = Color.black
    initMatrix()
    for (i <- 0 until playerGridList.length)
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
      controller.undo()
    case ButtonClicked(`redoMenu`) | KeyPressed(_, Key.Y, Key.Modifier.Control, _) =>
      controller.redo()
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
    stackCardButton.setContent(new CardGUI(controller.getStackCard()))
    trashCardButton.setContent(new CardGUI(controller.getTrashCard()))
    val currentPlayer = controller.getCurrenPlayer()
    updateMatrix(currentPlayer)
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
  def this(card: CardInterface) = this(card.value, card.opened)
  override def paint(g: Graphics2D): Unit = {
    super.paint(g)
    if(open)
      g.setPaint(Color.white)
      g.fillOval(2,3,20,20)
      g.fillOval(28,47,20,20)
    // else 
    //   val p=new TexturePaint(ImageIO.read(new File("pics"+File.separator+"backSide.png")),bounds.asInstanceOf[Rectangle2D])
    //   g.setPaint(p)
    //   g.fillRect(bounds.x-CardGUI.cardVPadding,bounds.y-CardGUI.cardHPadding,bounds.width,bounds.height)
    paintChildren(g)
  }
  if (open)
    val text= if(value.toString().length()==2) value.toString() else " "+value
    add(new Label(value.toString){font_=(font.deriveFont(20.0f));foreground_=(Color.black)}, BorderPanel.Position.Center)
    add(new Label(text,null,Alignment.Left){foreground_=(Color.black)}, BorderPanel.Position.North)
    add(new Label(value.toString().padTo(2,' '),null,Alignment.Right){foreground_=(Color.black)}, BorderPanel.Position.South)
    background_=(CardGUI.valueColor(value))
  else
    add(new GridPanel(1,1){
      // opaque_=(false)
      contents+=wrap(GradientPanel(true,Color.BLUE,Color.RED))
      // contents+=wrap(GradientPanel(true,Color.BLUE,Color.YELLOW))
      // contents+=wrap(GradientPanel(false,Color.YELLOW,Color.GREEN))
      // contents+=wrap(GradientPanel(false,new Color(153,0,153),Color.YELLOW))
      // contents+=wrap(GradientPanel(true,Color.YELLOW,Color.RED))
    }
    ,BorderPanel.Position.Center)
  val wborder = new LineBorder(Color.white, 3)
  val eborder = new EmptyBorder(2, 2, 2, 2)
  border_=(new CompoundBorder(eborder, wborder))
  preferredSize_=(dim)
  minimumSize_=(dim)
  maximumSize_=(dim)
  preferredSize_=(cardDim)
  minimumSize_=(cardDim)
  maximumSize_=(cardDim)
  override def enabled_=(b: Boolean)=
    if(b) 
      if(open)
        background_=(background.brighter())
      else
        contents(0).asInstanceOf[Container].contents.foreach(c=>c.peer.asInstanceOf[GradientPanel].enabled_(true))
    else 
      if(open)
        background_=(background.darker())
      else
        contents(0).asInstanceOf[Container].contents.foreach(c=>c.peer.asInstanceOf[GradientPanel].enabled_(false))
}

object CardGUI {
  val cardHPadding = 5
  val cardVPadding = 8
  val cardDim = new Dimension(50, 70)
  val width = cardDim.width+2*cardVPadding
  val height = cardDim.height+2*cardHPadding
  val buttonBorder=new EmptyBorder(5,8,5,8)
  val cardButtonDim = new Dimension(width, height)
  def valueColor(value:Int):Color={
    if(value<0)
      return Color.blue
    if(value==0)
      return Color(13,110,170,255)
    if(value<5)
      return Color.green
    if(value<9)
      return Color.yellow
    else
      Color.red.brighter()
  }
}

case class ButtonPanel(var active:Boolean,var highlight:Boolean,x:Int,y:Int)extends BoxPanel(Orientation.Vertical){
  def this(x:Int,y:Int)=this(true,false,x,y)
  border_=(CardGUI.buttonBorder)
  opaque_=(false)
  def highlight_(high:Boolean)={
    border_= (if (high)new CompoundBorder(new LineBorder(Color.yellow,3,true),new EmptyBorder(2,5,2,5)) 
    else CardGUI.buttonBorder)
    highlight=high
  }
  def active_(act:Boolean)={
    contents.foreach(c=>c.enabled_=(act))
    enabled_=(act)
    active=act
    this
  }
  def setContent(comp:Component)={contents.clear();contents+=comp; this}
  listenTo(mouse.clicks)
  reactions+={case MousePressed(_,_,_,_,_)=>if(active)highlight_(!highlight)}
}

class GradientPanel(startTop:Boolean,var color1:Color,var color2:Color) extends JPanel {
  import java.awt._
    override def paint(g: Graphics): Unit = 
        var g2d = g.asInstanceOf[Graphics2D]
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE)
        val y1 = if(startTop) 0 else getHeight()
        val y2 = if(!startTop) 0 else getHeight()
        var gp = new GradientPaint(0, y1, color1, getWidth(), y2, color2)
        g2d.setPaint(gp)
        g2d.fillRect(0, 0, getWidth(), getHeight())
        super.paint(g)
    setOpaque(false)
    def enabled_ (bool:Boolean)={
      if(bool)
        color1=color1.brighter()
        color2=color2.brighter()
      else
        color1=color1.darker()
        color2=color2.darker()
    }
}
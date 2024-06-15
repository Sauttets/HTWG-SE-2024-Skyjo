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
import java.awt.FlowLayout
import javax.swing.JFrame
import de.htwg.se.skyjo.aview.Util.colors
import de.htwg.se.skyjo.aview.Util.idx1
import de.htwg.se.skyjo.aview.Util.idx2

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
      }
  }
  stackCardButton.reactions+={
    case MousePressed(_,_,_,_,_)=>
      if(stackCardButton.active) 
        publish(new DrawEvent(true))
        stackCardButton.active_(false)
  }
  case class DrawEvent(stack:Boolean) extends Event
  val stackLabel, trashLabel = new Label()
  stackLabel.text = "Stack"
  trashLabel.text = "Trash"

  val drawPanel = new BoxPanel(Orientation.Horizontal) {
    opaque_=(false)
    val stackPanel=new BoxPanel(Orientation.Vertical) {
      opaque_=(false)
      contents += stackLabel += stackCardButton
    }
    val trashPanel=new BoxPanel(Orientation.Vertical) {
      opaque_=(false)
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
        grid.contents+=new ButtonPanel(r,c){reactions+={case MousePressed(_,_,_,_,_)=>if(phase2 && active){
          controller.doMove(new Move(stackCardButton.highlight,swapped,r,c))
          stackCardButton.active_(true)
          stackCardButton.highlight_(false)
          trashCardButton.highlight_(false)
          swapped=true
          phase2=false
        }}}
      grid.opaque_=(false)
      val fixedSize = new Dimension((Util.width+3)  * grid.columns, (Util.height+3)* grid.rows)
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
    initMatrix()
    for (i <- 0 until playerGridList.length)
      contents += playerGridList(i)
    opaque_=(false)
  }
  val mainPanel=new BoxPanel(Orientation.Vertical){
    focusable_=(true)
    contents += (drawPanel, Swing.VStrut(20), table)
    requestFocus()
    override def paint(g: Graphics2D)={
      super.paint(g)
      val p=new TexturePaint(ImageIO.read(new File("pics"+File.separator+"Wood2.png")),new Rectangle(0,0,bounds.width,bounds.height))
      g.setPaint(p)
      g.fillRect(0,0,bounds.width,bounds.height)
      paintChildren(g)
    }
  }
  contents = mainPanel

  listenTo(trashCardButton, stackCardButton, menuBar, quitMenu, undoMenu, redoMenu,mainPanel.keys)

  var phase2=false
  var swapped=true
  reactions += {
    case DrawEvent(false) =>
      if(phase2)
        swapped=false
      else
        controller.drawFromTrash()
        phase2=true
      // showMovePopup(false)
    case DrawEvent(true) =>
      if(!phase2)
        controller.drawFromStack()
        phase2=true
        // showMovePopup(true)
    case ButtonClicked(`undoMenu`) | KeyPressed(_, Key.Z, Key.Modifier.Control, _)=>
      controller.undo()
    case ButtonClicked(`redoMenu`) | KeyPressed(_, Key.Y, Key.Modifier.Control, _) =>
      controller.redo()
    case ButtonClicked(`quitMenu`) | KeyPressed(_, Key.Q, Key.Modifier.Control, _)=>
      sys.exit(0)
  }
  // def showMovePopup(draw: Boolean): Unit = {
  //   val dialog = new Dialog {
  //     title = "Enter Move Details"
  //     var swapped: Boolean = false
  //     val rowField, colField = new TextField { columns = 5 }
  //     val replaceButton = new Button { text = "Replace" }
  //     val throwButton = new Button { text = "Throw"; enabled_=(false); background=Color.green}
  //     val executeButton = new Button { text = "Execute" }

  //     contents = new BoxPanel(Orientation.Vertical) {
  //       contents ++= Seq(
  //         new BoxPanel(Orientation.Horizontal) {
  //           contents ++= Seq(replaceButton, throwButton)
  //         },
  //         new Label("Row:"), rowField,
  //         new Label("Column:"), colField,
  //         executeButton
  //       )
  //       border = Swing.EmptyBorder(10, 10, 10, 10)
  //     }

  //     listenTo(replaceButton, throwButton, executeButton)

  //     reactions += {
  //       case ButtonClicked(`replaceButton`) =>
  //         swapped = true
  //         replaceButton.enabled_=(false)
  //         replaceButton.background=Color.green
  //         throwButton.enabled_=(true)
  //         throwButton.background=new Button().background
  //       case ButtonClicked(`throwButton`) =>
  //         swapped = false
  //         throwButton.enabled_=(false)
  //         throwButton.background=Color.green
  //         replaceButton.enabled_=(true)
  //         replaceButton.background=new Button().background
  //       case ButtonClicked(`executeButton`) =>
  //         val moveInputString = s" ${if (swapped) "S" else "T"} ${rowField.text} ${colField.text}"
  //         moveInput(moveInputString, draw) match {
  //           case None =>
  //             Dialog.showMessage(contents.head, "Invalid move input", title = "Error", Dialog.Message.Error)
  //           case Some(move) =>
  //             controller.doMove(move)
  //             stackCardButton.active_(true).highlight_(false)
  //             trashCardButton.active_(true).highlight_(false)
  //             close()
  //         }
  //     }
  //     centerOnScreen()
  //     open()
  //   }
  // }

  // def moveInput(input: String, draw: Boolean): Option[Move] = {
  //   val Input = (" " + input).toUpperCase().replaceAll("\\s+", " ")
  //   if (Input.replaceAll(" ", "") == "Q") sys.exit(0)

  //   val parts = Input.split(" ")
  //   if (parts.length < 4) None
  //   else {
  //     Try {
  //       val swapped = parts(1) match {
  //         case "S" | "SWAP" => true
  //         case "T" | "D" | "DISCARD" => false
  //         case _ => throw new IllegalArgumentException("Invalid action")
  //       }
  //       Move(draw, swapped, parts(2).toInt, parts(3).toInt)
  //     } match {
  //       case Success(move) => Some(move)
  //       case Failure(_) => None
  //     }
  //   }
  // }
  def winScreen()={
    val scores=controller.getScores()
    val winner=scores.minBy(s=>s._1)
    new Dialog(this,null){
      val Winlabel=new Label("PLAYER "+(winner(1)+1)+" WON"){
        font_=(font.deriveFont(40.0f))
        foreground_=(Color.RED)
      }
      val scoreText=new TextArea(){
        rows_=(scores.length)
        this.
        text_=(scores.map(s=>"Player "+(s._2+1)+": "+s._1).mkString(sys.props("line.separator")))
        print(text)
        
      }
      contents_=(new BoxPanel(Orientation.Vertical){contents+=(Winlabel,scoreText)})
      centerOnScreen()
      open()
      peer.setDefaultCloseOperation(2)
      override def dispose()= {
        controller.reset()
        print("hi")
        // super.dispose()
      }
    }
  }
  var lastplayer=(-1)
  def refreshCards(): Unit = {
    stackCardButton.setContent(new CardGUI(controller.getStackCard()))
    trashCardButton.setContent(new CardGUI(controller.getTrashCard()))
    val currentPlayer = controller.getCurrenPlayer()
    if(controller.gameEnd()&& lastplayer==(-1))
      lastplayer=(currentPlayer+controller.getPLayerCount()-1)%controller.getPLayerCount()
      print(lastplayer)
    updateMatrix(currentPlayer)
    validate();
    repaint();
    mainPanel.requestFocus()
    if(currentPlayer==lastplayer)
      winScreen()
  }
  
  refreshCards()
  pack()
  minimumSize_=(size)
  centerOnScreen()
  open()
end GUI

class CardGUI(value: Int, open: Boolean, dim: Dimension = Util.cardDim) extends BorderPanel {
  import Util._
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
    background_=(Util.valueColor(value))
  else
    add(new GridPanel(1,1){
      // opaque_=(false)
      contents+=wrap(AnimatedPanel(true))
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
        contents(0).asInstanceOf[Container].contents.foreach(c=>c.peer.asInstanceOf[AnimatedPanel].enabled_(true))
    else 
      if(open)
        background_=(background.darker())
      else
        contents(0).asInstanceOf[Container].contents.foreach(c=>c.peer.asInstanceOf[AnimatedPanel].enabled_(false))
}

object Util {
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
  val steps=100
  val r=scala.List.tabulate(steps)(r=>new Color(r*255/100,       255,         0));
  val g=scala.List.tabulate(steps)(g=>new Color(      255, (100-g)*255/100,         0));
  val b=scala.List.tabulate(steps)(b=>new Color(      255,         0, b*255/100));
  val r2=scala.List.tabulate(steps)(r=>new Color((100-r)*255/100,         0,       255));
  val g2=scala.List.tabulate(steps)(g=>new Color(        0, g*255/100,       255));
  val b2=scala.List.tabulate(steps)(b=>new Color(        0,       255, (100-b)*255/100));
  val colors = scala.List(r,g,b,r2,g2,b2).flatten.appended(new Color(        0,       255,         0));
  var idx1=0
  var idx2=colors.size/2
}

case class ButtonPanel(var active:Boolean,var highlight:Boolean,x:Int,y:Int)extends BoxPanel(Orientation.Vertical){
  def this(x:Int,y:Int)=this(true,false,x,y)
  border_=(Util.buttonBorder)
  opaque_=(false)
  def highlight_(high:Boolean)={
    border_= (if (high)new CompoundBorder(new LineBorder(Color.yellow,3,true),new EmptyBorder(2,5,2,5)) 
    else Util.buttonBorder)
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

class AnimatedPanel(startTop:Boolean) extends JPanel {
  import java.awt._
  import Util._
    var state=true
    Timer(100){
      import util._
      idx1=(idx1+1)%colors.size
      idx2=(idx2-1+colors.size)%colors.size
      validate()
      repaint(20)
    }
    // for (int g=100; g>0; g--) colors.add(new Color(      255, g*255/100,         0));
    // for (int b=0; b<100; b++) colors.add(new Color(      255,         0, b*255/100));
    // for (int r=100; r>0; r--) colors.add(new Color(r*255/100,         0,       255));
    // for (int g=0; g<100; g++) colors.add(new Color(        0, g*255/100,       255));
    // for (int b=100; b>0; b--) colors.add(new Color(        0,       255, b*255/100));
    //                       colors.add(new Color(        0,       255,         0));
    override def paint(g: Graphics): Unit = 
        var g2d = g.asInstanceOf[Graphics2D]
        val second=System.currentTimeMillis()%1000
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        // g2d.setRenderingHint(RenderingHints.KEY_DITHERING,RenderingHints.VALUE_DITHER_ENABLE)
        val y1 = if(startTop) 0 else getHeight()
        val y2 = if(!startTop) 0 else getHeight()
        var gp = if(state) new GradientPaint(0, y1, colors(idx1), getWidth(), y2, colors(idx2)) else new GradientPaint(0, y1, colors(idx1).darker(), getWidth(), y2, colors(idx2).darker())
        g2d.setPaint(gp)
        g2d.fillRect(0, 0, getWidth(), getHeight())
        super.paint(g)
    setOpaque(false)
    def enabled_ (bool:Boolean)={
      state=bool
    }
}

object Timer {
  def apply(interval: Int, repeats: Boolean = true)(op: => Unit)={
    val timeOut = new javax.swing.AbstractAction() {
      def actionPerformed(e : java.awt.event.ActionEvent) = op
    }
    val t = new javax.swing.Timer(interval, timeOut)
    t.setRepeats(repeats)
    t.start()
  }
}
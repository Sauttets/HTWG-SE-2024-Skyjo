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
import de.htwg.se.skyjo.aview.Util.Backgrounds
import de.htwg.se.skyjo.aview.Util.playerColor
import scala.collection.mutable.ListBuffer
import de.htwg.se.skyjo.aview.Util.Timerlist
import scala.swing.ScrollPane.BarPolicy
import javax.swing.KeyStroke
import javax.swing.filechooser.FileNameExtensionFilter
import de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoJsonImpl.FileIO as JsonFileIO
import de.htwg.se.skyjo.model.modelComponent.fileIoComponent.fileIoXmlImpl.FileIO as XmlFileIO
import javax.swing.JFileChooser
import de.htwg.se.skyjo.aview.Util.pixel

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
  stackCardButton.reactions+={
    case MousePressed(_,_,_,_,_)=>
      if(stackCardButton.active) 
        publish(new DrawEvent(true))
        stackCardButton.active_(false)
  }
  case class DrawEvent(stack:Boolean) extends Event
  val stackLabel =new Label("STACK"){font_=(Util.mainFont.deriveFont(3.0f*pixel));foreground_=(Color.WHITE)}
  val trashLabel = new Label("TRASH"){font_=(Util.mainFont.deriveFont(3.0f*pixel));foreground_=(Color.WHITE)}

  val drawPanel = new BoxPanel(Orientation.Horizontal) {
    opaque_=(false)
    val stackPanel=new BorderPanel() {
      opaque_=(false)
      add(stackLabel,BorderPanel.Position.North)
      add(stackCardButton,BorderPanel.Position.South)
    }
    val trashPanel=new BorderPanel() {
      opaque_=(false)
      add(trashLabel,BorderPanel.Position.North)
      add(trashCardButton,BorderPanel.Position.South)
    }
    contents +=stackPanel+=trashPanel
    override def paint(g: Graphics2D)={
      val p=new TexturePaint(ImageIO.read(new File("assets"+File.separator+"GreyVectors.png")),new Rectangle(0,0,bounds.width,bounds.height))
      g.setPaint(p)
      g.fillRect(0,0,bounds.width,bounds.height)
      super.paint(g)
      paintChildren(g)
    }
  }
  val extenstionFilter = controller.fileIO match{
    case _:JsonFileIO=> new FileNameExtensionFilter("JSON","json","JSON")
    case _:XmlFileIO=> new FileNameExtensionFilter("XML","xml","XML")
  }
  val undoMenu = new MenuItem("Undo") { mnemonic = Key.Z; peer.setAccelerator(KeyStroke.getKeyStroke("control Z")); icon_=(ImageIcon(ImageIO.read(File("assets"+File.separator+"Undo.png"))))}
  val redoMenu = new MenuItem("Redo") { mnemonic = Key.Y; peer.setAccelerator(KeyStroke.getKeyStroke("control Y")); icon_=(ImageIcon(ImageIO.read(File("assets"+File.separator+"Redo.png"))))}
  val quitMenu = new MenuItem("Quit") { mnemonic = Key.Q; peer.setAccelerator(KeyStroke.getKeyStroke("control Q")); icon_=(ImageIcon(ImageIO.read(File("assets"+File.separator+"Quit.png"))))}
  val loadMenu= new MenuItem("Load") { mnemonic = Key.L; peer.setAccelerator(KeyStroke.getKeyStroke("control L")); icon_=(ImageIcon(ImageIO.read(File("assets"+File.separator+"Load.png"))))}
  val loadChooser=new FileChooser(new File("saves")) { fileFilter_=(extenstionFilter); title_=("Load File")}
  val saveMenu= new MenuItem("Save") { mnemonic = Key.S; peer.setAccelerator(KeyStroke.getKeyStroke("control S")); icon_=(ImageIcon(ImageIO.read(File("assets"+File.separator+"Save.png"))))}
  val saveChooser=new FileChooser(new File("saves")) { fileFilter_=(extenstionFilter); title_=("Save File")}
  menuBar = new MenuBar {
    contents += new Menu("  Util  ") {
      mnemonic = Key.U
      contents += (undoMenu, redoMenu, quitMenu)
    }
    contents+=new Menu("  FileIO  "){
      mnemonic = Key.F
      contents+=(loadMenu,saveMenu)
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
      val fixedSize = new Dimension((Util.width+3)  * grid.columns, (Util.height+3)* grid.rows+5)
      grid.preferredSize_=(fixedSize)
      grid.minimumSize_=(fixedSize)
      grid.maximumSize_=(fixedSize)
      grid.border_=(EmptyBorder(10, 5, 10, 5))
    )
  }
  var currentPlayerGrid=new GridPanel(1,1)
  def updateMatrix(currplayer: Int) = {
    for(i<-0 until controller.getPLayerCount())
      val grid = playerGridList(i)
      val mat = controller.getTabletop()(i)
      if((i!=currplayer))
       grid.border_=(EmptyBorder(10, 5, 10, 5))
      else
        currentPlayerGrid=grid
      for (c <- 0 until grid.columns;r <- 0 until grid.rows)
        grid.contents(r*grid.columns+c).asInstanceOf[ButtonPanel].setContent(new CardGUI(mat.getCard(r, c))).active_(i==currplayer).highlight_(false)
  }

  val playerGridList = controller.getTabletop().map(mat =>{new GridPanel(mat.rows.size, mat.rows(0).size)})
  val playerNameList = controller.getTabletop().zipWithIndex.map((mat,i) =>{
    new scala.swing.TextField("PLAYER "+(i+1)){
      foreground_=(playerColor(i,playerGridList.size))
      font_=(Util.mainFont.deriveFont(4.0f*pixel))
      opaque_=(false)
    }
  })
  val table = new FlowPanel() {
    initMatrix()
    for (i <- 0 until playerGridList.length)
      contents += new BoxPanel(Orientation.Vertical){
        contents+=(playerNameList(i), Swing.VStrut(10),playerGridList(i))
        opaque_=(false)
      }
    opaque_=(false)
  }
  val Instructions=new ScrollPane(new TextArea(){
    font_=(Util.mainFont.deriveFont(3.0f*pixel))
    text_=(scala.io.Source.fromFile("assets"+File.separator+"Instructions.txt").mkString)
    background_=(Color.black)
  }){
    preferredSize_=(java.awt.Dimension(80*pixel,45*pixel))
    opaque_=(false)
  }
  val upperPanel=new FlowPanel(Instructions,Swing.HStrut(40),drawPanel){opaque_=(false)}
  val mainPanel=new BoxPanel(Orientation.Vertical){
    focusable_=(true)
    contents += (Swing.HStrut(1),upperPanel, Swing.VStrut(20), table,Swing.VStrut(20))
    override def paint(g: Graphics2D)={
      super.paint(g)
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      val MainbackgroundPaint=new TexturePaint(ImageIO.read(new File("assets"+File.separator+"BlackVectors.png")),new Rectangle(0,0,bounds.width,bounds.height+20))
      g.setPaint(MainbackgroundPaint)
      g.fillRect(0,0,bounds.width,bounds.height)
      paintChildren(g)
    }
  }
  contents = mainPanel

  listenTo(trashCardButton, stackCardButton, menuBar, quitMenu, undoMenu, redoMenu,loadMenu,saveMenu)

  var phase2=false
  var swapped=true
  reactions += {
    case DrawEvent(false) =>
      if(phase2)
        swapped=false
      else
        controller.drawFromTrash()
        phase2=true
    case DrawEvent(true) =>
      if(!phase2)
        controller.drawFromStack()
        phase2=true
    case ButtonClicked(`undoMenu`)=>
      controller.undo()
    case ButtonClicked(`redoMenu`)=>
      controller.redo()
    case ButtonClicked(`quitMenu`)=>
      sys.exit(0)
    case ButtonClicked(`loadMenu`) =>
      val r=loadChooser.showOpenDialog(null)
      if(r.equals(FileChooser.Result.Approve))
        controller.load(new File(loadChooser.selectedFile.toString().replaceAll("\\..*","")))
    case ButtonClicked(`saveMenu`) =>
      val r=saveChooser.showSaveDialog(null)
      if(r.equals(FileChooser.Result.Approve)){
        controller.save((new File(saveChooser.selectedFile.toString().replaceAll("\\..*",""))))
      }
  }
  var gameScores=playerNameList.map(_=>Buffer[Int]()).toList
  def winScreen()={
    val scores=controller.getScores()
    val winner=scores.sortBy(s=>s._1).toList(0)
    val frame=peer
    val paritys=controller.getParitys()
    controller.openAll()
    if(paritys.size>0)
      paritys.foreach((player,row)=>{
        val grid=playerGridList(player)
        for(r<- 0 until grid.columns)
          val cards=grid.contents(grid.columns*row+r).asInstanceOf[ButtonPanel].contents(0).asInstanceOf[CardGUI]
          cards.opaque_=(false)
          cards.repaint()
      })
    new Dialog(this){
      frame.enable(false)
      val Winlabel=new Label(playerNameList(winner(1)).text+" WON THIS ROUND  "){
        font_=(Util.mainFont.deriveFont(8.0f*pixel))
        foreground_=(playerColor(winner(1),playerGridList.size))
        opaque_=(false)
      }
      val scoreText=new TextArea(){
        rows_=(scores.length)
        // text_=(scores.zipWithIndex.map((s,idx)=>(idx+1)+".  "+playerNameList(s._2).text+":\t"+s._1).mkString(sys.props("line.separator")))
        gameScores=gameScores.zipWithIndex.map((sl,idx)=>sl+=(scores(idx)(0)))
        val ln=sys.props("line.separator")
        val scoreText=gameScores.zipWithIndex.sortBy((b,idx)=>b.sum).map((b,idx)=>playerNameList(idx).text+" : "+b.mkString(" | ")+" = "+b.sum)
        text_=(ln+scoreText.mkString(ln+ln)+ln)
        font_=(font.deriveFont(4.0f*pixel))
        opaque_=(false)
      }
      contents_=(new BoxPanel(Orientation.Vertical){
        override def paint(g: Graphics2D)={
          super.paint(g)
          g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
          val MainbackgroundPaint=new TexturePaint(ImageIO.read(new File("assets"+File.separator+"GreyVectors.png")),new Rectangle(0,0,bounds.width,bounds.height+20))
          g.setPaint(MainbackgroundPaint)
          g.fillRect(0,0,bounds.width,bounds.height)
          paintChildren(g)
        }
        contents+=(Winlabel,scoreText)}
        )
      centerOnScreen()
      open()
      pack()
      minimumSize_=(size)
      override def closeOperation()= {
        controller.reset()
        lastplayer=(-1)
        refreshCards()
        frame.enable(true)
      }
    }
  }
  var lastplayer=(-1)
  def refreshCards(): Unit = {
    Backgrounds=scala.List[AnimatedPanel]()
    stackCardButton.setContent(new CardGUI(controller.getStackCard()))
    trashCardButton.setContent(new CardGUI(controller.getTrashCard()))
    val currentPlayer = controller.getCurrenPlayer()
    if(controller.gameEnd()&& lastplayer==(-1))
      lastplayer=(currentPlayer+controller.getPLayerCount()-1)%controller.getPLayerCount()
    updateMatrix(currentPlayer)
    validate();
    repaint();
    if(currentPlayer==lastplayer)
      lastplayer=(-1)
      controller.openAll()
      winScreen()
  }
  
  refreshCards()
  Timer(10){
      import util._
      idx1=(idx1+1)%colors.size
      idx2=(idx2-1+colors.size)%colors.size
      Backgrounds.foreach(b=>{b.validate();b.repaint(20)})
      currentPlayerGrid.border_=(new LineBorder(colors((idx1*4)%colors.size),3,true))
      drawPanel.border_=(CompoundBorder(LineBorder(colors((idx1)%colors.size),3,true),EmptyBorder(4,4,4,4)))
      stackLabel.foreground_=(colors((idx1+colors.size/2)%colors.size))
      trashLabel.foreground_=(colors((idx1+colors.size/2)%colors.size))
      currentPlayerGrid.repaint()
      Instructions.contents(0).foreground_=(colors((idx1+colors.size/2)%colors.size))
  }
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
      g.fillOval(pixel/2,pixel/2,pixel*4,pixel*4)
      g.fillOval(5*pixel,bounds.height-5*pixel,pixel*4,pixel*4)
    // else 
    //   val p=new TexturePaint(ImageIO.read(new File("pics"+File.separator+"backSide.png")),bounds.asInstanceOf[Rectangle2D])
    //   g.setPaint(p)
    //   g.fillRect(bounds.x-CardGUI.cardVPadding,bounds.y-CardGUI.cardHPadding,bounds.width,bounds.height)
    paintChildren(g)
  }
  val eWidth=if(pixel%2==0)pixel/2 else pixel/2+1
  val wWidth=if(pixel%2==1)pixel/2 else pixel/2+1
  if (open)
    val paddedValue= if(value.toString().length()==2) " "+value else "  "+value
    add(new Label(value.toString){font_=(Util.cardFont.deriveFont(4.0f*pixel));foreground_=(Color.black)}, BorderPanel.Position.Center)
    add(new Label(paddedValue,null,Alignment.Left){font_=(Util.cardFont.deriveFont(2.5f*pixel));foreground_=(Color.black)}, BorderPanel.Position.North)
    add(new Label(value.toString().padTo(3,' '),null,Alignment.Right){font_=(Util.cardFont.deriveFont(2.5f*pixel));foreground_=(Color.black)}, BorderPanel.Position.South)
    background_=(Util.valueColor(value))
    border_=(CompoundBorder(LineBorder(Util.valueColor(value), wWidth,true),LineBorder(Color.white, eWidth,true)))
  else
    opaque_=(false)
    add(new GridPanel(1,1){
      // opaque_=(false)
      contents+=wrap(AnimatedPanel(true))
    }
    ,BorderPanel.Position.Center)
    border_=(LineBorder(Color.white, eWidth,true))
  preferredSize_=(dim)
  minimumSize_=(dim)
  maximumSize_=(dim)

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
  var pixel=8
  val mainFont=java.awt.Font.createFont(0, new File("assets"+File.separator+"Glitch.ttf"))
  val cardFont=java.awt.Font.createFont(0, new File("assets"+File.separator+"MarqueeMoon-Regular.ttf"))
  val cardHPadding = pixel
  val cardVPadding = pixel
  val cardDim = new Dimension(10*pixel, 14*pixel)
  val width = cardDim.width+2*cardVPadding
  val height = cardDim.height+2*cardHPadding
  val buttonBorder=new EmptyBorder(cardHPadding,cardVPadding,cardHPadding,cardVPadding)
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
  val Timerlist=ListBuffer[javax.swing.Timer]()
  def playerColor(i:Int,max:Int)=colors(colors.size/max*i)
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
  var Backgrounds=scala.List[AnimatedPanel]()
}

case class ButtonPanel(var active:Boolean,var highlight:Boolean,x:Int,y:Int)extends BoxPanel(Orientation.Vertical){
  def this(x:Int,y:Int)=this(true,false,x,y)
  border_=(Util.buttonBorder)
  opaque_=(false)
  def highlight_(high:Boolean)={
    val width:Int=Util.pixel/2
    val hPad=Util.cardHPadding-width;
    val vPad=Util.cardVPadding-width;
    border_= (if (high)new CompoundBorder(new LineBorder(Color.yellow,width,true),new EmptyBorder(hPad,vPad,hPad,vPad)) 
    else Util.buttonBorder)
    highlight=high
  }
  def active_(act:Boolean)={
    contents.foreach(c=>c.enabled_=(act))
    enabled_=(act)
    active=act
    this
  }
  def setContent(comp:Component)={
    contents.clear();
    contents+=comp; 
    this}
  listenTo(mouse.clicks)
  reactions+={case MousePressed(_,_,_,_,_)=>if(active)highlight_(!highlight)}
}

class AnimatedPanel(startTop:Boolean) extends JPanel {
  import java.awt._
  import Util._
    Backgrounds=Backgrounds.appended(this)
    var state=true
    override def paint(g: Graphics): Unit = 
        var g2d = g.asInstanceOf[Graphics2D]
        val y1 = if(startTop) 0 else getHeight()
        val y2 = if(!startTop) 0 else getHeight()
        val gp = if(state) new GradientPaint(0, y1, colors(idx1), getWidth(), y2, colors(idx2)) else new GradientPaint(0, y1, colors(idx1).darker(), getWidth(), y2, colors(idx2).darker())
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
    val timer = new javax.swing.Timer(interval, timeOut)
    Util.Timerlist+=(timer)
    timer.setRepeats(repeats)
    timer.start()
  }
}

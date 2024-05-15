import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.controller._
import de.htwg.se.skyjo.model._

class DrawCommandSpec extends AnyWordSpec with Matchers {
  "A MoveCommand" when {
    val playerTable = new PlayerTable()
    val controller = TableController(playerTable)    
    "draw a card from stack" in {
      val oldTable = controller.table
      val command=new DrawFromStackCommand(controller)
      command.execute()
      controller.table should not be oldTable
    }
    }
}

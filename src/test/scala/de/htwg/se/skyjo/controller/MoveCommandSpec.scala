import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.controller._
import de.htwg.se.skyjo.model._

class MoveCommandSpec extends AnyWordSpec with Matchers {
    "A MoveCommand" when {
      "new" should {
        val playerTable = new PlayerTable()
        val controller = TableController(playerTable)
        "do a move with drawn from stack" in {
          val move = new Move(true, false, 1, 2)
          val oldTable = controller.table
          val command= new DoMoveCommand(controller,move)
          command.execute()
          controller.table should not be oldTable
        }
        "do a move with drawn from trash" in {
          val move = new Move(false, false, 2, 1)
          val oldTable = controller.table
          val command= new DoMoveCommand(controller,move)
          command.execute()
          controller.table should not be oldTable
        }
        "do a move with swap card" in {
          val move = new Move(true, true, 1, 2)
          val oldTable = controller.table
          val command= new DoMoveCommand(controller,move)
          command.execute()
          controller.table should not be oldTable
        }
    }
  }
}

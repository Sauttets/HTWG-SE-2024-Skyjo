import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.controller._
import de.htwg.se.skyjo.model._

class TableControllerTest extends AnyWordSpec with Matchers {
  "A TableController" when {
    "new" should {
      val playerTable = new PlayerTable()
      val controller = TableController(playerTable)
      "have a table" in {
        controller.table should be(playerTable)
      }
      "draw a card from stack" in {
        val oldTable = controller.table
        controller.drawFromStack()
        controller.table should not be oldTable
      }
      "do a move with drawn from stack" in {
        val move = new Move(true, false, 1, 2)
        val oldTable = controller.table
        controller.doMove(move)
        controller.table should not be oldTable
      }
      "do a move with swap card" in {
        val move = new Move(true, true, 1, 2)
        val oldTable = controller.table
        controller.doMove(move)
        controller.table should not be oldTable
      }
      "check if game ends" in {
        controller.gameEnd() shouldBe a[Boolean]
      }
      "return a string representation of the table" in {
        controller.toString shouldBe a[String]
      }
    }
  }
}
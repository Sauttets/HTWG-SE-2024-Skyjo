import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.{PlayerTable, CardBuilder, PlayerMatrix}
import de.htwg.se.skyjo.controller.controllerComponent.controllerimplementation.MoveCommand
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.LCardStack
import de.htwg.se.skyjo.util.{Memento, Move}

class CommandSpec extends AnyWordSpec with Matchers {
  "A MoveCommand" when {
    "executed" should {
      val playerTable = PlayerTable(2, 4, 3, 0, new LCardStack, List.tabulate(2)(_ => new PlayerMatrix(3, 4)))
      val move = new Move(true, true, 0, 1)
      val command = new MoveCommand(playerTable, move)

      "return a new Memento with the updated state" in {
        val memento = command.execute()
        memento shouldBe a[Memento]
        memento.state should not be playerTable
      }
    }
  }
}

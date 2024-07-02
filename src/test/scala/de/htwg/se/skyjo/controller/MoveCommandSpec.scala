import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.{CardBuilder, PlayerMatrix, PlayerTable, LCardStack}
import de.htwg.se.skyjo.util.{Memento, Move}
import de.htwg.se.skyjo.controller.controllerComponent.controllerimplementation.MoveCommand

class MoveCommandSpec extends AnyWordSpec with Matchers {
  "A MoveCommand" when {
    "executed" should {
      val playerCount = 2
      val width = 4
      val height = 3
      val currentPlayer = 0
      val cardStack = new LCardStack()
      val tableTop = List.tabulate(playerCount)(_ => new PlayerMatrix(height, width))
      val playerTable = PlayerTable(playerCount, width, height, currentPlayer, cardStack, tableTop)
      val move = new Move(true, true, 0, 1)
      val command = new MoveCommand(playerTable, move)

      "return a new Memento with the updated state" in {
        val memento = command.execute()
        memento shouldBe a[Memento]
        memento.state should not be playerTable
      }

      "swap a card and update the table state" in {
        val initialCard = playerTable.Tabletop(currentPlayer).getCard(0, 1)
        val memento = command.execute()
        val updatedTable = memento.state.asInstanceOf[PlayerTable]
        val swappedCard = updatedTable.Tabletop(currentPlayer).getCard(0, 1)
        swappedCard should not be initialCard
        }
    }
  }
}

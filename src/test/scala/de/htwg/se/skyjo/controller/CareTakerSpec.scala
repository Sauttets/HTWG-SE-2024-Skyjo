import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.util.Memento
import de.htwg.se.skyjo.controller.controllerComponent.controllerimplementation.CareTaker
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.{PlayerMatrix, PlayerTable, LCardStack}

class CareTakerSpec extends AnyWordSpec with Matchers {
  "A CareTaker" when {
    "managing mementos" should {
      val playerCount = 2
      val width = 4
      val height = 3
      val currentPlayer = 0
      val cardStack = new LCardStack()
      val tableTop = List.tabulate(playerCount)(_ => new PlayerMatrix(height, width))
      val playerTable = PlayerTable(playerCount, width, height, currentPlayer, cardStack, tableTop)
      val careTaker = new CareTaker()
      val memento = Memento(playerTable)

      "save a memento" in {
        careTaker.save(memento)
        careTaker.undo(memento).isDefined shouldBe true
      }

      "undo to a previous state" in {
        val newMemento = Memento(playerTable.copy(currentPlayer = 1))
        careTaker.save(newMemento)
        val undoneMemento = careTaker.undo(newMemento).get
      }

      "redo to a next state" in {
        val initialMemento = Memento(playerTable)
        careTaker.save(initialMemento)
        val newMemento = Memento(playerTable.copy(currentPlayer = 1))
        careTaker.save(newMemento)

        careTaker.undo(newMemento)
        val redoneMemento = careTaker.redo(initialMemento).get
        redoneMemento.state shouldBe newMemento.state
      }

      "clear all mementos" in {
        careTaker.clear()
        careTaker.undo(memento).isDefined shouldBe false
        careTaker.redo(memento).isDefined shouldBe false
      }
    }
  }
}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.PlayerTable
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.PlayerMatrix
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.LCardStack
import de.htwg.se.skyjo.util.Memento

class MementoSpec extends AnyWordSpec with Matchers {
  "A Memento" when {
    "created" should {
      val playerTable = PlayerTable(2, 4, 3, 0, new LCardStack, List.tabulate(2)(_ => new PlayerMatrix(3, 4)))
      val memento = Memento(playerTable)

      "hold the state of the player table" in {
        memento.state shouldBe playerTable
      }
    }
  }
}

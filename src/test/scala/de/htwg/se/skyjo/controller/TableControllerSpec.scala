import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.controller._
import de.htwg.se.skyjo.model._

class TableControllerSpec extends AnyWordSpec with Matchers {
  "A TableController" when {
    "new" should {
      val playerTable = new PlayerTable()
      val controller = TableController(playerTable)
      controller.setCardStackStrategy(new LCardStack)
      "check if game ends" in {
        controller.gameEnd() shouldBe a[Boolean]
      }
      "return a string representation of the table" in {
        controller.toString shouldBe a[String]
      }
    }
  }
}
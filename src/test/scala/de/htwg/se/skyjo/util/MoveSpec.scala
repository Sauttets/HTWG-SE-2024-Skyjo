import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.util.Move

class MoveSpec extends AnyWordSpec with Matchers {
  "A Move" when {
    "created" should {
      val move = new Move(true, true, 0, 1)

      "have correct attributes" in {
        move.drawnFromStack shouldBe true
        move.swapped shouldBe true
        move.row shouldBe 0
        move.col shouldBe 1
      }
    }
  }
}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model._

class MoveSpec extends AnyWordSpec with Matchers {
    "A Move" when {
        "created" should {
            "have the correct values" in {
                val move = Move(true, false, 1, 2)
                move.drawnFromStack shouldEqual true
                move.swapped shouldEqual false
                move.row shouldEqual 1
                move.col shouldEqual 2
            }
        }
    }
}
import de.htwg.se.skyjo.model._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerMatrixSpec extends AnyWordSpec with Matchers {
  "A PlayerMatrix" when {
    "created" should {
      "have the correct size" in {
        val playerMatrix = new PlayerMatrix(3, 3)
        playerMatrix.size shouldEqual 3
      }

      "and random all closed cards" in {
        val playerMatrix = new PlayerMatrix(2, 2)
        playerMatrix.rows.flatten.forall(!_.opened) shouldEqual true
      }
    }

    "flipCard" should {
      "correctly flip the specified card" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(new Card(), new Card()), Vector(new Card(), new Card())))
        val flippedMatrix = playerMatrix.flipCard(0, 0)
        val cardValue = flippedMatrix.getCard(0, 0).value
        flippedMatrix.getCard(0, 0).opened shouldEqual true
        flippedMatrix.getCard(0, 0).value shouldEqual cardValue
      }
    }

    "changeCard method is called" should {
      "correctly change the specified card" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(new Card(), new Card()), Vector(new Card(), new Card())))
        val originalCard = playerMatrix.getCard(0, 0)
        val newCard = new Card(5, true)
        val (newMatrix, oldCard) = playerMatrix.changeCard(0, 0, newCard)
        newMatrix.getCard(0, 0).value shouldEqual newCard.value
        newMatrix.getCard(0, 0).opened shouldEqual true
        originalCard.value shouldBe oldCard.value
      }
    }

    "checkFinished method is called" should {
      "return true if all cards are opened" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(new Card(1, true), new Card(2, true)), Vector(new Card(3, true), new Card(4, true))))
        playerMatrix.checkFinished() shouldEqual true
      }

      "return false if any card is not opened" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(new Card(1, true), new Card(2, false)), Vector(new Card(3, true), new Card(4, true))))
        playerMatrix.checkFinished() shouldEqual false
      }
    }
    "getRow method is called" should {
        "return the correct row of cards" in {
            val card1 = new Card(1, true)
            val card2 = new Card(2, true)
            val card3 = new Card(3, true)
            val card4 = new Card(4, true)
            val playerMatrix = new PlayerMatrix(Vector(Vector(card1, card2), Vector(card3, card4)))
            val row = playerMatrix.getRow(0)
            row shouldEqual Vector(card1, card2)
            }
        }
    }
}

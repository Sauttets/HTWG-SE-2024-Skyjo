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
        val playerMatrix = new PlayerMatrix(Vector(Vector(CardBuilder().build(), CardBuilder().build()), Vector(CardBuilder().build(), CardBuilder().build())))
        val flippedMatrix = playerMatrix.flipCard(0, 0)
        val cardValue = flippedMatrix.getCard(0, 0).value
        flippedMatrix.getCard(0, 0).opened shouldEqual true
        flippedMatrix.getCard(0, 0).value shouldEqual cardValue
      }
    }

    "changeCard method is called" should {
      "correctly change the specified card" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(CardBuilder().build(), CardBuilder().build()), Vector(CardBuilder().build(), CardBuilder().build())))
        val originalCard = playerMatrix.getCard(0, 0)
        val newCard = CardBuilder().value(5).opened(true).build()
        val (newMatrix, oldCard) = playerMatrix.changeCard(0, 0, newCard)
        newMatrix.getCard(0, 0).value shouldEqual newCard.value
        newMatrix.getCard(0, 0).opened shouldEqual true
        originalCard.value shouldBe oldCard.value
      }
    }

    "checkFinished method is called" should {
      "return true if all cards are opened" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(CardBuilder().value(1).opened(true).build(), CardBuilder().value(2).opened(true).build()), Vector(CardBuilder().value(3).opened(true).build(), CardBuilder().value(4).opened(true).build())))
        playerMatrix.checkFinished() shouldEqual true
      }

      "return false if any card is not opened" in {
        val playerMatrix = new PlayerMatrix(Vector(Vector(CardBuilder().value(1).opened(true).build(), CardBuilder().value(2).opened(false).build()), Vector(CardBuilder().value(3).opened(true).build(), CardBuilder().value(4).opened(true).build())))
        playerMatrix.checkFinished() shouldEqual false
      }
    }
    "getScore method is called" should{
      "return sum of all cards" in{
        val playerMatrix = new PlayerMatrix(Vector(Vector(CardBuilder().value(-2).opened(true).build(), CardBuilder().value(12).opened(true).build()), Vector(CardBuilder().value(5).opened(true).build(), CardBuilder().value(0).opened(true).build())))
        playerMatrix.getScore() shouldBe 15
      }
    }
    "getRow method is called" should {
        "return the correct row of cards" in {
            val card1 = CardBuilder().value(1).opened(true).build()
            val card2 = CardBuilder().value(2).opened(true).build()
            val card3 = CardBuilder().value(3).opened(true).build()
            val card4 = CardBuilder().value(4).opened(true).build()
            val playerMatrix = new PlayerMatrix(Vector(Vector(card1, card2), Vector(card3, card4)))
            val row = playerMatrix.getRow(0)
            row shouldEqual Vector(card1, card2)
            }
        }
    }
}

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.skyjo.model.modelComponent.modelImplementation.{Card, CardBuilder, PlayerMatrix}

class PlayerMatrixSpec extends AnyWordSpec with Matchers {
  "A PlayerMatrix" when {
    "new" should {
      val height = 3
      val width = 4
      val matrix = new PlayerMatrix(height, width)

      "have the correct dimensions" in {
        matrix.size shouldBe height
        matrix.rows.size shouldBe height
        matrix.rows.head.size shouldBe width
      }

      "contain cards" in {
        all(matrix.rows.flatten) shouldBe a[Card]
      }

      "flip a card" in {
        val row = 0
        val col = 0
        val initialCard = matrix.getCard(row, col)
        initialCard.opened shouldBe false

        val newMatrix = matrix.flipCard(row, col)
        val flippedCard = newMatrix.getCard(row, col)
        flippedCard.opened shouldBe true
      }

      "change a card" in {
        val row = 0
        val col = 0
        val newCard = CardBuilder().value(5).opened(true).build()
        val (newMatrix, oldCard) = matrix.changeCard(row, col, newCard)

        oldCard shouldBe matrix.getCard(row, col)
        newMatrix.getCard(row, col) shouldBe newCard
      }

      "check if finished" in {
        val finishedMatrix = new PlayerMatrix(matrix.rows.map(row => row.map(_.open())))
        finishedMatrix.checkFinished() shouldBe true
        matrix.checkFinished() shouldBe false
      }

      "calculate score" in {
        val row = 0
        val col = 0
        val matrixWithSameValueRow = matrix.changeCard(row, col, CardBuilder().value(0).opened(true).build())._1
        matrixWithSameValueRow.getScore() shouldBe a[Int]

        val matrixWithDifferentValues = matrix.changeCard(row, col, CardBuilder().value(1).opened(true).build())._1
        matrixWithDifferentValues.getScore() should not be 0
      }
    }
  }
}
